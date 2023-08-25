package insitution;

import java.util.List;

import crafty.DataCenter;
import crafty.ModelRunner;
import net.sourceforge.jFuzzyLogic.FIS;
import net.sourceforge.jFuzzyLogic.FunctionBlock;
import net.sourceforge.jFuzzyLogic.plot.JFuzzyChart;
import sim.engine.SimState;

public class NatureInstitution extends AbstractInstitution {

	
	private InformCollector supplyCollector;
	private InformCollector demandCollector;
	FunctionBlock functionBlock ;
	@Override
	public void setup(ModelRunner modelRunner) {
		this.modelRunner = modelRunner;
		
	}

	@Override
	public void toSchedule() {

		
	}

	@Override
	public void step(SimState arg0) {

		
	}

	@Override
	protected void initialize() {
		fuzzyPrepare();
		Policy policy = new Policy.Builder()
				.policyName("subsidy to increase diversity")
				.type(PolicyType.SUBSIDY)
				.goal(1.0 * modelRunner.getState(DataCenter.class).getGlobalProductionMap().get("Diversity"))
				.initialGuess(10000.)
				.inertia(0.1)
				.policyLag(5)
				.targetService("Diversity")
				.build();		
		this.register(policy);
		
		policy = new Policy.Builder()
				.policyName("Protected areas")
				.type(PolicyType.PROTECTION)
				.goal(1.0 * modelRunner.getState(DataCenter.class).getGlobalProductionMap().get("Diversity"))
				.initialGuess(10000.)
				.inertia(0.1)
				.policyLag(5)
				.targetService("Diversity")
				.build();		
		this.register(policy);
		
		demandCollector = new InformCollector("Diversity");
		supplyCollector = new InformCollector("Diversity");
		
	}

	@Override
	protected void collectInformation() {
		demandCollector.collect("Diversity", modelRunner.getState(DataCenter.class).getAnualDemand().get("Diversity"));
		supplyCollector.collect("Diversity", modelRunner.getState(DataCenter.class).getGlobalProductionMap().get("Diversity"));
		
	}

	@Override
	protected void predict() {

		
	}

	@Override
	protected void policyEvaluation() {
		policyMap.values().forEach(policy -> {
			List<Double> historicalSupply = supplyCollector.get(policy.getTargetService());
			if (historicalSupply.size() >= policy.getPolicyLag() & historicalSupply.size()% policy.getPolicyLag()==0) {
				policy.setStartChanging(true);
			}else {
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
	protected void policyAdaptation() {
		policyMap.values().forEach(policy -> {
			if(policy.isStartChanging()) {
				double interventionModifier = policy.getInterventionModifier();
				functionBlock.setVariable("gap", policy.getEvluation());
				functionBlock.evaluate();
				policy.setInterventionModifier(functionBlock.getVariable("intervention").getValue() + interventionModifier);
				System.out.println("average gap: " + policy.getEvluation() + "; intervention: " + functionBlock.getVariable("intervention").getValue());
			}
		});
		
	}
	
	@Override
	protected void budgetUpdate() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void resourceAllocation() {
		
		
	}

	@Override
	public void implementPolicy() {

		
	}
	
	@Override
	public void updatePolicyHistory() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void fuzzyPrepare() {
		// Load from 'FCL' file
		String fileName = "resources/fcl/tipper.fcl";
		FIS fis = FIS.load(fileName, true);
		
		// Error while loading?
		if (fis == null) {
			System.err.println("Can't load file: '" + fileName + "'");
			return;
		}
		
		// Get policy function block
		functionBlock = fis.getFunctionBlock("policy");

		// Show
		JFuzzyChart.get().chart(functionBlock);
		
	}

}
