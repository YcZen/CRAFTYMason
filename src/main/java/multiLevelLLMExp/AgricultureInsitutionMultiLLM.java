package multiLevelLLMExp;

import crafty.DataCenter;
import institution.AgriInstitution;
import institution.InformCollector;
import institution.Policy;
import institution.PolicyType;

public class AgricultureInsitutionMultiLLM extends AgriInstitution {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Override
	public void initialize() {
		
		fuzzyPrepare();
		Policy policy = new Policy.Builder().policyName("decrease meat").type(PolicyType.ECO)
				// .type(PolicyType.ECO)
				.goal(((MLLRunner) modelRunner).getMeatGoal()
						* modelRunner.getState(DataCenter.class).getInitSupplyMap().get("Meat"))
				// .goal(5 *
				// modelRunner.getState(DataCenter.class).getGlobalProductionMap().get("Meat"))
				.initialGuess(1000000.).inertia(0.12).policyLag(((MLLRunner) modelRunner).getMeatLag())
				.targetService("Meat").build();
		this.register(policy);

		policy = new Policy.Builder().policyName("increase crop").type(PolicyType.SUBSIDY)
				 .type(PolicyType.ECO)
				.goal(((MLLRunner) modelRunner).getCropGoal()
						* modelRunner.getState(DataCenter.class).getInitSupplyMap().get("Crops"))
				.initialGuess(1000000.).inertia(0.2).policyLag(((MLLRunner) modelRunner).getCropLag())
				.targetService("Crops").build();
	//	this.register(policy);

		demandCollector = new InformCollector("Meat", "Crops");
		supplyCollector = new InformCollector("Meat", "Crops");

		totalBugdet = 0;
	}
}
