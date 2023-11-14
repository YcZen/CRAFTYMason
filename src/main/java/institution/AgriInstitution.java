package institution;

import java.util.List;
import crafty.DataCenter;
import experiments.Intra;
import modelRunner.AbstractModelRunner;
import net.sourceforge.jFuzzyLogic.FIS;
import net.sourceforge.jFuzzyLogic.FunctionBlock;
import sim.engine.SimState;

public class AgriInstitution extends AbstractInstitution {

	private static final long serialVersionUID = 1L;
	InformCollector demandCollector;
	InformCollector supplyCollector;
	FunctionBlock functionBlock;
	private FunctionBlock fuzzyTax;
	private FunctionBlock fuzzySubsidy;
	private FunctionBlock fuzzyECO;

	@Override
	public void initialize() {

		fuzzyPrepare();
		Policy policy = new Policy.Builder().policyName("decrease meat").type(PolicyType.ECO)
				// .type(PolicyType.ECO)
				.goal(((Intra) modelRunner).getMeatGoal()
						* modelRunner.getState(DataCenter.class).getInitSupplyMap().get("Meat"))
				// .goal(5 *
				// modelRunner.getState(DataCenter.class).getGlobalProductionMap().get("Meat"))
				.initialGuess(1000000.).inertia(0.2).policyLag(((Intra) modelRunner).getMeatLag())
				.targetService("Meat").build();
		this.register(policy);

		policy = new Policy.Builder().policyName("increase crop").type(PolicyType.SUBSIDY)
				 .type(PolicyType.ECO)
				.goal(((Intra) modelRunner).getCropGoal()
						* modelRunner.getState(DataCenter.class).getInitSupplyMap().get("Crops"))
				.initialGuess(1000000.).inertia(0.2).policyLag(((Intra) modelRunner).getCropLag())
				.targetService("Crops").build();
	//	this.register(policy);

		demandCollector = new InformCollector("Meat", "Crops");
		supplyCollector = new InformCollector("Meat", "Crops");

		totalBugdet = 0;
	}

	@Override
	public void collectInformation() {
		demandCollector.collect("Meat", modelRunner.getState(DataCenter.class).getAnualDemand().get("Meat"));
		demandCollector.collect("Crops", modelRunner.getState(DataCenter.class).getAnualDemand().get("Crops"));
		supplyCollector.collect("Meat", modelRunner.getState(DataCenter.class).getGlobalProductionMap().get("Meat"));
		supplyCollector.collect("Crops", modelRunner.getState(DataCenter.class).getGlobalProductionMap().get("Crops"));
	}

	@Override
	public void predict() {

	}

	@Override
	public void policyEvaluation() {
		policyMap.values().forEach(policy -> {
			List<Double> historicalSupply = supplyCollector.get(policy.getTargetService());
			if (historicalSupply.size() >= policy.getPolicyLag()
					& historicalSupply.size() % policy.getPolicyLag() == 0) {
				policy.setStartChanging(true);
			} else {
				policy.setStartChanging(false);
			}
			if (policy.isStartChanging()) {
				int start = historicalSupply.size() - policy.getPolicyLag();
				int end = historicalSupply.size();
				List<Double> recentSupply = historicalSupply.subList(start, end);
				double averageGap = 0;
				for (int i = 0; i < policy.getPolicyLag(); i++) {
					averageGap = averageGap + (policy.getGoal() - recentSupply.get(i)) / policy.getGoal();
				}
				averageGap = averageGap / policy.getPolicyLag();

				policy.setEvluation(averageGap);
			}
		});
	}

	@Override
	public void policyAdaptation() {
		policyMap.values().forEach(policy -> {
			if (policy.isStartChanging()) {
				if (policy.getType() == PolicyType.TAX) {
					functionBlock = fuzzyTax;
				}
				if (policy.getType() == PolicyType.SUBSIDY) {
					functionBlock = fuzzySubsidy;
				}
				if (policy.getType() == PolicyType.ECO) {
					functionBlock = fuzzyECO;
				}
				double interventionModifier = policy.getInterventionModifier();
				functionBlock.setVariable("gap", policy.getEvluation());
				functionBlock.evaluate();
				double fuzzyResult = functionBlock.getVariable("intervention").getValue();
				double bound = Math.signum(fuzzyResult) * policy.getInertia();
				double incrementalIntervention = (Math.abs(bound) < Math.abs(fuzzyResult)) ? bound : fuzzyResult;
				policy.setInterventionModifier(incrementalIntervention + interventionModifier);
//				double incrementalIntervention = Math.abs( policy.getInertia())<Math.abs( policy.getEvluation())? Math.abs( policy.getEvluation())/policy.getEvluation()*policy.getInertia():policy.getEvluation();//(policy.getInertia()<functionBlock.getVariable("intervention").getValue())? policy.getInertia(): functionBlock.getVariable("intervention").getValue();
//				policy.setInterventionModifier(incrementalIntervention + interventionModifier);
				policy.updateIntervention();
				System.out.println(
						"average gap: " + policy.getEvluation() + "; intervention: " + policy.getIntervention() + "; modifier: " + policy.getInterventionModifier());// +
																													// functionBlock.getVariable("intervention").getValue());
			}
		});
	}

	@Override
	protected void budgetUpdate() {
		/*
		 * The budget should be updated every year. Budget comes from two ways: a
		 * proportion of the total agricultural GDP (reflecting the individual income
		 * tax), plus extra taxes imposed by this institution (reflecting the
		 * cross-subsidization)
		 */
		// totalBugdet = 0;
		policyMap.values().forEach(policy -> {
			if ((policy.getType() == PolicyType.ECO || policy.getType() == PolicyType.TAX)
					&& !policy.getHistory().isEmpty()) {
				if (policy.getLatestHistory() < 0) { // this is to ensure the economic policy is taxing the farmers
					totalBugdet += Math.abs(policy.getLatestHistory());
				}
			}
		});

		String[] agriProductionList = { "Meat", "Crops" };
		double proportion = 1;
		for (String production : agriProductionList) {
			totalBugdet += (proportion
					* modelRunner.getState(DataCenter.class).getGlobalProductionMap().get(production));
		}

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
			}
		});

	}

	@Override
	public void implementPolicy() {
		policyMap.values().forEach(policy -> {
			if (policy.getType() == PolicyType.TAX || policy.getType() == PolicyType.SUBSIDY
					|| policy.getType() == PolicyType.ECO) {
				double utility = modelRunner.getState(DataCenter.class).getUtitlityMap().get(policy.getTargetService());
				utility = utility + policy.getIntervention();
				modelRunner.getState(DataCenter.class).getUtitlityMap().put(policy.getTargetService(), utility);
			}
		});
		// System.out.println("Policy implemented ---->> " +
		// modelRunner.schedule.getSteps() + " ----->>" +
		// modelRunner.getState(DataCenter.class).getUtitlityMap().get("Meat"));
	}

	@Override
	public void updatePolicyHistory() {
		policyMap.values().forEach(policy -> {
			policy.updatePolicyHistory();
		});

	}

	@Override
	public void fuzzyPrepare() {
		// Load from 'FCL' file
		String fileName = "resources/fcl/fuzzyPolicy.fcl";
		FIS fis = FIS.load(fileName, true);

		// Error while loading?
		if (fis == null) {
			System.err.println("Can't load file: '" + fileName + "'");
			return;
		}

		// Get policy function block
		fuzzyTax = fis.getFunctionBlock("tax");
		fuzzySubsidy = fis.getFunctionBlock("subsidy");
		fuzzyECO = fis.getFunctionBlock("policy");
		// Show
		// JFuzzyChart.get().chart(fuzzyTax);
		// JFuzzyChart.get().chart(fuzzySubsidy);
		// JFuzzyChart.get().chart(fuzzyECO);
	}

	@Override
	public void setup(AbstractModelRunner modelRunner) {
		this.modelRunner = modelRunner;

	}

	@Override
	public void toSchedule() {
		modelRunner.schedule.scheduleRepeating(0, modelRunner.indexOf(this), this, 1.0);
	}

	@Override
	public void step(SimState arg0) {
		// System.out.println("Institution step");
		if (modelRunner.schedule.getTime() == 0) {
			initialize();
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

}
