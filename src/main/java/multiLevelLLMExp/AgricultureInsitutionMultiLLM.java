package multiLevelLLMExp;

import crafty.DataCenter;
import institution.AgriInstitution;
import institution.InformCollector;
import institution.Policy;
import institution.PolicyType;
import modelRunner.AbstractModelRunner;
import sim.engine.SimState;

public class AgricultureInsitutionMultiLLM extends AgriInstitution {
	public double budgetGain = 0;

	/**inertia is set to 1.0, indicating there is no imperatively imposed inertia constraint.
	 * We want to let the budget alone constrain the supply change.
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	@Override
	public void setup(AbstractModelRunner modelRunner) {
		this.modelRunner = modelRunner;
		initialize();
		//var aVar = modelRunner.getState(DataCenter.class).getGlobalProductionMap().get("Meat");
	}
	
	@Override
	public void step(SimState arg0) {

		if (modelRunner.schedule.getTime() == 0) {
			//initialize();
		}

		collectInformation();
		predict();
		policyEvaluation();
		policyAdaptation();
		budgetUpdate();
		resourceAllocation();
		// implementPolicy(); // this method is exectued in utilityUpdater
		updatePolicyHistory();
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
				.initialGuess(1000000.).inertia(0.12).policyLag(((MLLRunner) modelRunner).getMeatLag())
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
					|| (policy.getType() == PolicyType.ECO && policy.getIntervention() > 0)) {
				double intervention = policy.getIntervention();
				intervention = (intervention < totalBugdet) ? intervention : totalBugdet;
				policy.setIntervention(intervention);	
				//policy.setInterventionModifier(intervention/policy.getInitialGuess()); //set the modifier to the actual value
				totalBugdet += -intervention;
				System.out.println("Agricultural Budget: " + totalBugdet);
			}
		});

	}
	
	public void setBudgetGain(double budgetGain) {
		this.budgetGain = budgetGain;
	}
}
