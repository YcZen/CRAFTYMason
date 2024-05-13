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
	List<Integer> timelList = new ArrayList<Integer>();
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
        "\"Agricultural Institution\": 50,\n" +
        "\"Environmental Institution\": 50\n" +
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
    double agriInstitutionBudget;
    double envInstitutionBudget;
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
		modelRunner.schedule.scheduleRepeating(0, modelRunner.indexOf(this), this, 1.0);
		
	}

	@Override
	public void step(SimState arg0) {
		try {
			allocateBudget();
		//	int q = 1/0;
			timelList.add((int) modelRunner.schedule.getSteps());
			meatDemand.add(modelRunner.getState(DataCenter.class).getAnualDemand().get("Meat"));
			meatSupply.add(modelRunner.getState(DataCenter.class).getGlobalProductionMap().get("Meat"));
			paRatioList.add(modelRunner.getState(NatureInstitutionMultiLLM.class).getPaRatio());
	        
			if (modelRunner.schedule.getSteps()==0) {
				updateIntervention();
			}
			
			
	    	agriBudgetList.add(agriInstitutionBudget);
	    	enviBudgetList.add(envInstitutionBudget);
//	    	agriPolicyList.add(agriInstitutionPolicy);
//	    	enviPolicyList.add(envInstitutionPolicy);
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
	                writer.write(timelList.get(i) + "," + meatDemand.get(i) + "," + meatSupply.get(i) + "," + paRatioList.get(i) + ","
	                			+ agriPolicyList.get(i) + "," + enviPolicyList.get(i) + "," + agriBudgetList.get(i) + "," + enviBudgetList.get(i));
	                writer.newLine();
	            }

	            System.out.println("Data successfully written to " + csvFile);
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
			
			
//			System.out.println("pa List:" + paRatioList);
			if (modelRunner.schedule.getSteps()>0 && modelRunner.schedule.getSteps()%10==0) {

//				
//				String info = "The data in a JSON structure below represents the time series of a set of variables."
//						+ "Write Python code to analyze the data in the way you think appropriate.\r\n"
//						+ "Then, execute the code, provide the results, and interpret the results.\r\n"
//						+ "Your output will inform policy-making and decision-making of relevant entities.\r\n"
//						+ jsonString;
				String info = "data prepared";
				System.out.println(info);
			//	highInstitutionOutput = gateEntry.runAgents(info);
				updateIntervention();
			}
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
    
    public void updateIntervention() {
		jsonObject = gson.fromJson(highInstitutionOutput, JsonObject.class);
		
        // Extract numerical data from the "Budget Allocation" section as doubles
        budgetAllocation = jsonObject.getAsJsonObject("Budget Allocation");
        agriInstitutionBudget = budgetAllocation.get("Agricultural Institution").getAsDouble();
        envInstitutionBudget = budgetAllocation.get("Environmental Institution").getAsDouble();

        // Extract numerical data from the "Policy Goal" section as doubles
        policyGoal = jsonObject.getAsJsonObject("Policy Goal");
        agriInstitutionPolicy = policyGoal.get("Agricultural Institution").getAsDouble();
        meatGoal = modelRunner.getState(AgricultureInsitutionMultiLLM.class).getPolicy("decrease meat").getGoal();
        meatGoal = (agriInstitutionPolicy/100 + 1) * meatGoal;
        modelRunner.getState(AgricultureInsitutionMultiLLM.class).getPolicy("decrease meat").setGoal(meatGoal);
       // agriInstitutionPolicy = (agriInstitutionPolicy/100 + 1) * 1040670.981;   // Just for testing
        envInstitutionPolicy = policyGoal.get("Environmental Institution").getAsDouble();
        paGoal = 1 - ((MLLRunner) modelRunner).getUnprotectionLimit();
        paGoal = (envInstitutionPolicy/100 + 1) * paGoal;
        double actualPaRatio = modelRunner.getState(NatureInstitutionMultiLLM.class).getPaRatio();
        paGoal = (paGoal > actualPaRatio)? paGoal:actualPaRatio;
        ((MLLRunner) modelRunner).setUnprotectionLimit(1-paGoal);
        //envInstitutionPolicy = (envInstitutionPolicy/100 + 1) * 0.3;  // Just for testing
    }
    
    public void allocateBudget() {
    	Collection<Double> globalProductionValues = modelRunner.getState(DataCenter.class).getGlobalProductionMap().values();
        double sum = 0;
        for (Double value : globalProductionValues) {
            sum += value;
        }
        double totalBudget = sum*0.01; // 10% of total ecosystem service will be used as institutional budget.
    	
    	modelRunner.getState(AgricultureInsitutionMultiLLM.class).setBudgetGain(0.9*totalBudget);
    	modelRunner.getState(NatureInstitutionMultiLLM.class).setBudgetGain(0.1*totalBudget);
    	System.out.println("totalBudget: "+totalBudget);
    }
    
    }

