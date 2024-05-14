package multiLevelLLMExp;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.antlr.analysis.DFA;
import org.hibernate.boot.spi.AbstractDelegatingSessionFactoryBuilder;

import crafty.DataCenter;
import modelRunner.AbstractModelRunner;
import net.sourceforge.jFuzzyLogic.fcl.FclParser.declaration_return;
import py4j.GatewayServer;
import sim.engine.SimState;
import updaters.AbstractUpdater;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.jgoodies.forms.layout.Size;


public class GatewayConnector extends AbstractUpdater{

	private GateEntry gateEntry;
	List<Integer> timeList = new ArrayList<Integer>();
	List<Double> meatSupply = new ArrayList<Double>();
	List<Double> meatDemand = new ArrayList<Double>();
	List<Double> paRatioList = new ArrayList<Double>();
	List<Double> agriBudgetList  = new ArrayList<Double>();
	List<Double> enviBudgetList  = new ArrayList<Double>();
	List<Double> agriPolicyList  = new ArrayList<Double>();
	List<Double> enviPolicyList = new ArrayList<Double>();
	Map<String, Object> map = new HashMap<>();
    ObjectMapper objectMapper = new ObjectMapper();
    // JSON string to be parsed
    String highInstitutionOutput = "{\n" +
        "\"Stakeholder input analysis\": \" None \",\n" +
        "\"Budget Allocation\": {\n" +
        "\"Budget allocation rationale\": \" None \",\n" +
        "\"Agricultural Institution\": 90,\n" +
        "\"Environmental Institution\": 10\n" +
        "},\n" +
        "\"Policy Goal\": {\n" +
        "\"Policy goal adjustment rationale\": \" None \",\n" +
        "\"Agricultural Institution\": 5,\n" +
        "\"Environmental Institution\": 10\n" +
        "}\n" +
        "}";
    // Create a Gson object
    Gson gson;// = new Gson();

    // Parse the JSON string into a JsonObject
    JsonObject jsonObject;
    JsonObject policyGoal;
    JsonObject budgetAllocation;
    double agriInstitutionBudgetRatio;
    double envInstitutionBudgetRatio;
    double agriInstitutionPolicy;
    double envInstitutionPolicy;
    double meatGoal;
    double paGoal;
    
    
	@Override
	public void setup(AbstractModelRunner abstractModelRunner) {
		this.modelRunner = abstractModelRunner;
		GatewayServer.turnLoggingOff();
		GatewayServer server = new GatewayServer();
		server.start();
		System.out.println("Server established...");
		gateEntry = (GateEntry) server.getPythonServerEntryPoint(new Class[] { GateEntry.class });
		gson = new Gson();
	}

	@Override
	public void toSchedule() {
		modelRunner.schedule.scheduleRepeating(0.0, modelRunner.indexOf(this), this, 1.0);
	}

	@Override
	public void step(SimState arg0) {
		
		/*
		 *  I. After setup, institutions are instantiated. Now, we can update the interventions and set policy goals 
		 *  and budget allocation. However, we should update the first intervention only one time at step = 0. 
		 */
		if (modelRunner.schedule.getSteps()==0){
			parseIntervention();  
			updateInstitutionGoals();    
			updateInstitutionBudgets();
		}
		
		try {
/*
 *      II. Every n years, the high-level institution update its intervention. The data coming from it needs to be parsed,
 *      updating operational institutions' policy goals and budgets.
 */
			if (modelRunner.schedule.getSteps()>0 && modelRunner.schedule.getSteps()%10==0) {

				String info = "data prepared";
				System.out.println(info);
			//	highInstitutionOutput = gateEntry.runAgents(info);
				parseIntervention();
				updateInstitutionGoals();
				updateInstitutionBudgets();
			}
			
			/*
			 *  III. Record the data. If III is executed then new data is recorded.
			 */
			updateRecordedData();
		}catch(Throwable ex){
	        System.err.println("Uncaught exception - " + ex.getMessage());
	        ex.printStackTrace(System.err);
			
		}
		
	}
	
    public static String formatDoubleListUsingStringFormat(List<Double> doubleList) {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i = 0; i < doubleList.size(); i++) {
            sb.append(String.format("%.2f", doubleList.get(i)));
            if (i < doubleList.size() - 1) {
                sb.append(", ");
            }
        }
        sb.append("]");
        return sb.toString();
    }
    
    public void parseIntervention() {
		jsonObject = gson.fromJson(highInstitutionOutput, JsonObject.class);
		
        // Extract numerical data from the "Budget Allocation" section as doubles
        budgetAllocation = jsonObject.getAsJsonObject("Budget Allocation");
        agriInstitutionBudgetRatio = budgetAllocation.get("Agricultural Institution").getAsDouble()/100;
        envInstitutionBudgetRatio = budgetAllocation.get("Environmental Institution").getAsDouble()/100;

        // Extract numerical data from the "Policy Goal" section as doubles
        policyGoal = jsonObject.getAsJsonObject("Policy Goal");
        agriInstitutionPolicy = policyGoal.get("Agricultural Institution").getAsDouble();
        meatGoal = modelRunner.getState(AgricultureInsitutionMultiLLM.class).getPolicy("decrease meat").getGoal();
        meatGoal = (agriInstitutionPolicy/100 + 1) * meatGoal;
       
       // agriInstitutionPolicy = (agriInstitutionPolicy/100 + 1) * 1040670.981;   // Just for testing
        envInstitutionPolicy = policyGoal.get("Environmental Institution").getAsDouble();
        paGoal = 1 - ((MLLRunner) modelRunner).getUnprotectionLimit();
        paGoal = (envInstitutionPolicy/100 + 1) * paGoal;
        double actualPaRatio = modelRunner.getState(NatureInstitutionMultiLLM.class).getPaRatio();
        paGoal = (paGoal > actualPaRatio)? paGoal:actualPaRatio;
       
        //envInstitutionPolicy = (envInstitutionPolicy/100 + 1) * 0.3;  // Just for testing
    }
    
    public void updateInstitutionGoals() {
    	 modelRunner.getState(AgricultureInsitutionMultiLLM.class).getPolicy("decrease meat").setGoal(meatGoal);
    	 ((MLLRunner) modelRunner).setUnprotectionLimit(1-paGoal);
    }
    
    public void updateInstitutionBudgets() {
    	Collection<Double> globalProductionValues = modelRunner.getState(DataCenter.class).getGlobalProductionMap().values();
        double sum = 0;
        for (Double value : globalProductionValues) {
            sum += value;
        }
        double totalBudget = sum*0.01; // 1% of total ecosystem service will be used as institutional budget.
    	
    	modelRunner.getState(AgricultureInsitutionMultiLLM.class).setBudgetGain(agriInstitutionBudgetRatio*totalBudget);
    	modelRunner.getState(NatureInstitutionMultiLLM.class).setBudgetGain(envInstitutionBudgetRatio*totalBudget);
    	System.out.println("totalBudget: "+totalBudget);
    }
    
    public void updateRecordedData() {
    	//		updateIntervention();
		timeList.add((int) modelRunner.schedule.getSteps());
		meatDemand.add(modelRunner.getState(DataCenter.class).getAnualDemand().get("Meat"));
		meatSupply.add(modelRunner.getState(DataCenter.class).getGlobalProductionMap().get("Meat"));
		paRatioList.add(modelRunner.getState(NatureInstitutionMultiLLM.class).getPaRatio());
		
    	agriBudgetList.add(agriInstitutionBudgetRatio);
    	enviBudgetList.add(envInstitutionBudgetRatio);
//    	agriPolicyList.add(agriInstitutionPolicy);
//    	enviPolicyList.add(envInstitutionPolicy);
    	agriPolicyList.add(meatGoal);
    	enviPolicyList.add(paGoal);
		
		int listSize = meatDemand.size();
        // Specify the file name
        String csvFile = "C:\\Publications\\LLM_mixed\\output_data.csv";

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(csvFile))) {
            // Write the header
            writer.write("Time,Meat Demand,Meat Supply,Protected Area Ratio,Policy goal of meat production,Policy goal of protected area ratio,Meat budget (percentage),Protected area budget (percentage)"); // do not add needless blank in the string
            writer.newLine();

            // Write data from both lists to the file
            for (int i = 0; i < listSize; i++) {
                writer.write(timeList.get(i) + "," + meatDemand.get(i) + "," + meatSupply.get(i) + "," + paRatioList.get(i) + ","
                			+ agriPolicyList.get(i) + "," + enviPolicyList.get(i) + "," + agriBudgetList.get(i) + "," + enviBudgetList.get(i));
                writer.newLine();
            }

            System.out.println("Data successfully written to " + csvFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    }

