package multiLevelLLMExp;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import crafty.DataCenter;
import institution.AgriInstitution;
import institution.InformCollector;
import institution.Policy;
import institution.PolicyType;
import modelRunner.AbstractModelRunner;
import sim.engine.SimState;

public class AgricultureInsitutionMultiLLM extends AgriInstitution {


	/**inertia is set to 1.0, indicating there is no imperatively imposed inertia constraint.
	 * We want to let the budget alone constrain the supply change.
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public double budgetGain = 0;
	double budgetSurplus = 0;
	ArrayList<Integer> timeList = new ArrayList<Integer>();
	ArrayList<Double> budgetSurplusList = new ArrayList<Double>();
	ArrayList<Double> meatGoalList = new ArrayList<Double>();
	ArrayList<Double> meatSupplyList = new ArrayList<Double>();
	public double neededIntervention = 0;
	
	@Override
	public void setup(AbstractModelRunner modelRunner) {
		this.modelRunner = modelRunner;
		initialize();
		//var aVar = modelRunner.getState(DataCenter.class).getGlobalProductionMap().get("Meat");
	}
	
	@Override
	public void step(SimState arg0) {


		collectInformation();
		predict();
		policyEvaluation();
		policyAdaptation();
		budgetUpdate();
		resourceAllocation();
		// implementPolicy(); // this method is exectued in utilityUpdater
		updatePolicyHistory();
		updateRecordedData();
	}

	
	@Override
	public void initialize() {
		
		fuzzyPrepare();
		Policy policy = new Policy.Builder().policyName("decrease meat").type(PolicyType.ECO)
				// .type(PolicyType.ECO)
				.goal(((MLLRunner) modelRunner).getMeatGoal()
						* modelRunner.getState(DataCenter.class).getGlobalProductionMap().get("Meat"))
				// .goal(5 *
				// modelRunner.getState(DataCenter.class).getGlobalProductionMap().get("Meat"))
				.initialGuess(1000000.).inertia(1.0).policyLag(((MLLRunner) modelRunner).getMeatLag())
				.targetService("Meat").build();
		this.register(policy);

//		policy = new Policy.Builder().policyName("increase crop").type(PolicyType.SUBSIDY)
//				 .type(PolicyType.ECO)
//				.goal(((MLLRunner) modelRunner).getCropGoal()
//						* modelRunner.getState(DataCenter.class).getGlobalProductionMap().get("Crops"))
//				.initialGuess(1000000.).inertia(0.2).policyLag(((MLLRunner) modelRunner).getCropLag())
//				.targetService("Crops").build();
	//	this.register(policy);

		demandCollector = new InformCollector("Meat", "Crops");
		supplyCollector = new InformCollector("Meat", "Crops");

		totalBugdet = 0;
	}
	
@Override
	protected void budgetUpdate() {
		totalBugdet += budgetGain;

	}

@Override
public void resourceAllocation() {
	/*
	 * Every year the resources should be reallocated. Should consider priority
	 * later.
	 */
	policyMap.values().forEach(policy -> {
		if (policy.getType() == PolicyType.SUBSIDY
				|| (policy.getType() == PolicyType.ECO && policy.getIntervention() >= 0)) { // >= 0 instead of > 0
			double interventionNeeded = policy.getInterventionNeeded();
			double actualIntervention = (interventionNeeded < totalBugdet) ? interventionNeeded : totalBugdet;
			policy.setIntervention(actualIntervention);	
			//policy.setInterventionModifier(intervention/policy.getInitialGuess()); //set the modifier to the actual value
			budgetSurplus = totalBugdet - interventionNeeded;
			totalBugdet += -actualIntervention;
		}
	});

}
	
	public void setBudgetGain(double budgetGain) {
		this.budgetGain = budgetGain;
	}
	
	public void updateRecordedData() {
		timeList.add((int) modelRunner.schedule.getSteps());
		budgetSurplusList.add(budgetSurplus);
		meatGoalList.add(policyMap.get("decrease meat").getGoal());
		meatSupplyList.add(modelRunner.getState(DataCenter.class).getGlobalProductionMap().get("Meat"));
		
		int listSize = timeList.size();
        // Specify the file name
        String csvFile = "C:\\Publications\\LLM_mixed\\Meat_production.csv";

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(csvFile))) {
            // Write the header
            writer.write("Time,Policy goal of meat production,Actual meat production,Budget surplus"); // do not add needless blank in the string
            writer.newLine();

            // Write data from both lists to the file
            for (int i = 0; i < listSize; i++) {
                writer.write(timeList.get(i) + "," + meatGoalList.get(i) + "," + meatSupplyList.get(i) + "," + budgetSurplusList.get(i));
                writer.newLine();
            }

            System.out.println("Data successfully written to " + csvFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
	}
	
	public double getBudgetSurplus() {
		return budgetSurplus;
	}
	
	
	
}
