package multiLevelLLMExp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import crafty.AbstractCell;
import crafty.DataCenter;
import crafty.LandCell;
import institution.InformCollector;
import institution.NatureInstitution;
import institution.Policy;
import institution.PolicyType;
import modelRunner.AbstractModelRunner;
import net.sourceforge.jFuzzyLogic.FIS;
import net.sourceforge.jFuzzyLogic.fcl.FclParser.declaration_return;
import sim.engine.SimState;

public class NatureInstitutionMultiLLM extends NatureInstitution {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private double paRatio = 0;
	double budgetGain = 0;
	double budgetSurplus = 0;
	ArrayList<Double> budgetSurplusList = new ArrayList<Double>();
	ArrayList<Integer> time = new ArrayList<Integer>();
	ArrayList<Double> paGoalList = new ArrayList<Double>();
	ArrayList<Double> paRaioList = new ArrayList<Double>();
	
	@Override
	public void setup(AbstractModelRunner modelRunner) {
		this.modelRunner = modelRunner;
//		modelRunner.getState(DataCenter.class).getCellSet().forEach(cell -> {
//			if (cell.isProtected() == false) {
//				unProtectedSet.add(cell);
//			}
//		});
		initialize();
	}
	
	
	@Override
	public void step(SimState arg0) {
		// System.out.println("Institution step");
		if (modelRunner.schedule.getTime() == 0) {
	//		initialize();
		}

		collectInformation();
		predict();
		policyEvaluation();
		policyAdaptation();
		budgetUpdate();
//		resourceAllocation();  //not needed in here.
		// implementPolicy(); // this method is exectued in InfluencedutilityUpdater
		updatePolicyHistory();
	}
	
	@Override
	protected void initialize() {
		fuzzyPrepare();
		Policy policy = new Policy.Builder().policyName("subsidy to increase diversity").type(PolicyType.SUBSIDY)
				.goal(((MLLRunner) modelRunner).getDivGoal()
						* modelRunner.getState(DataCenter.class).getGlobalProductionMap().get("Diversity"))
				.initialGuess(10000.).inertia(0.2).policyLag(5).targetService("Diversity").build();
	//	this.register(policy);

		double paInertia = ((MLLRunner) modelRunner).getPaInertia();
		policy = new Policy.Builder().policyName("Protected areas").type(PolicyType.PROTECTION)
				.goal(((MLLRunner) modelRunner).getDivGoal()
						* modelRunner.getState(DataCenter.class).getGlobalProductionMap().get("Diversity"))
				.initialGuess(10000.).inertia(paInertia).policyLag(((MLLRunner) modelRunner).getPolicyLag())
				.targetService("Diversity").build();
		this.register(policy);

		demandCollector = new InformCollector("Diversity");
		supplyCollector = new InformCollector("Diversity");

		modelRunner.getState(DataCenter.class).getCellSet().forEach(cell -> {
			if (cell.isProtected() == false) {
				unProtectedSet.add(cell);
			}
		});
		
		/**
		Updating paRatio here is necessary, because the policy implementation of this institution is in influencedUtilityUpdater,
		which executes setProtectedAreas prior to initialize(). This counter-intuitive arrangement should be improved at some point.
		 */
	//	paRatio = 1 - unProtectedSet.size() / (double) (modelRunner.getState(DataCenter.class).getCellSet().size()); 

	}
	

	@Override
	protected void policyEvaluation() {
		policyMap.values().forEach(policy -> {
			List<Double> historicalSupply = supplyCollector.get(policy.getTargetService());
			if (historicalSupply.size() >= policy.getPolicyLag()
					& historicalSupply.size() % policy.getPolicyLag() == 0) {
				policy.setStartChanging(true);
			} else {
				policy.setStartChanging(false);
			}
			if (policy.isStartChanging()) {
//				int start = historicalSupply.size() - policy.getPolicyLag();
//				int end = historicalSupply.size();
//				List<Double> recentSupply = historicalSupply.subList(start, end);
//				double averageGap = 0;
//				for (int i = 0; i < policy.getPolicyLag(); i++) {
//					averageGap = averageGap + (policy.getGoal() - recentSupply.get(i)) / policy.getGoal();
//				}
//				averageGap = averageGap / policy.getPolicyLag();
				if (policy.getName()=="Protected areas") {
					policy.setEvluation((1 - ((MLLRunner) modelRunner).getUnprotectionLimit()) - paRatio);
				}
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
				if (policy.getType() == PolicyType.PROTECTION) {
					functionBlock = fuzzyProect;
				}
				double interventionModifier = policy.getInterventionModifier();
				functionBlock.setVariable("gap", policy.getEvluation());
				functionBlock.evaluate();
				double fuzzyResult = functionBlock.getVariable("intervention").getValue();
				double bound = Math.signum(fuzzyResult) * policy.getInertia();
				double incrementalIntervention = (Math.abs(bound) < Math.abs(fuzzyResult)) ? bound : fuzzyResult;
				policy.setInterventionModifier(incrementalIntervention + interventionModifier);
				policy.updateIntervention();
//				System.out.println("average gap: " + policy.getEvluation() + "; intervention: "
//						+ functionBlock.getVariable("intervention").getValue());
			}
		});
	}

	@Override
	public void budgetUpdate() {

			totalBugdet += budgetGain;

		
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
		setProtectedAreas();
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
		fuzzyProect = fis.getFunctionBlock("policy");
		// Show
		// JFuzzyChart.get().chart(fuzzyTax);
		// JFuzzyChart.get().chart(fuzzySubsidy);
		// JFuzzyChart.get().chart(fuzzyECO);
	}

	@Override
	public void setProtectedAreas() {
		policyMap.values().forEach(policy -> {
			if (policy.getType() == PolicyType.PROTECTION && policy.isStartChanging() == true) {
				double intervention = policy.getIntervention();
				System.out.println(intervention);
				int n = (int) intervention;
				if (n < 0) {
					n = 0;
				}
				int totalOfCellsToProtect = (int) (modelRunner.getState(DataCenter.class).getCellSet().size() * (1 - ((MLLRunner) modelRunner).getUnprotectionLimit()));
				int numOfCellsToProtect = totalOfCellsToProtect - (modelRunner.getState(DataCenter.class).getCellSet().size() - unProtectedSet.size());
				
				List<AbstractCell> sortedList = new ArrayList<>(unProtectedSet);
				Collections.sort(sortedList, (a, b) -> Double.compare(((LandCell) b).calculateProtectionIndex(),
						((LandCell) a).calculateProtectionIndex()));

				int numberOfCellsShouldbeProtectedThisTime = Math.min(n,numOfCellsToProtect);
				int numberOfCellsCanBeProtectedThisTime = (int)(totalBugdet/1000);
				int numberofCellsToBeProtectedThisTime = Math.min(numberOfCellsShouldbeProtectedThisTime,numberOfCellsCanBeProtectedThisTime);
				List<AbstractCell> topN = sortedList.subList(0, numberofCellsToBeProtectedThisTime);// sortedList.size()));
				budgetSurplus = (numberOfCellsCanBeProtectedThisTime - numberOfCellsShouldbeProtectedThisTime) * 1000;
				
				totalBugdet -= numberofCellsToBeProtectedThisTime*1000;
				System.out.println("Nature Budget: " + totalBugdet);
				if (topN.size() != 0) {
					((MLLRunner) modelRunner).setEndYearProtc((int) modelRunner.schedule.getSteps());
					//System.out.println(((Intra) modelRunner).getEndYearProtc());
				}

				// change the state of cells to protected and modify
				// landCell.getProductionFilter()
				topN.forEach(landCell -> {

					landCell.setProteced(true);
					landCell.getProductionFilter().put("Meat", 0.0);
					landCell.getProductionFilter().put("Crops", 0.0);
					landCell.getProductionFilter().put("Diversity", 1.0);
					landCell.getProductionFilter().put("Timber", 0.0);
					landCell.getProductionFilter().put("Carbon", 1.0);
					landCell.getProductionFilter().put("Urban", 0.0);
					landCell.getProductionFilter().put("Rereation", 1.0);

					// update production on protected area
					modelRunner.getState(DataCenter.class).getServiceNameList().forEach(service -> {
						double newProduction = landCell.getServiceProductionMap().get(service)
								* landCell.getProductionFilter().get(service);
						landCell.getServiceProductionMap().put(service, newProduction);

					});

				});

				// Remove the selected LandCell instances from unProtectedSet
				unProtectedSet.removeAll(topN);
			}
		});
		
		paRatio = 1 - unProtectedSet.size() / (double) (modelRunner.getState(DataCenter.class).getCellSet().size());
		System.out.println("PA ratio:" + paRatio);
	}
	
	public double getPaRatio() {
		return paRatio;
	}
	
	public void setBudgetGain(double budgetGain) {
		this.budgetGain = budgetGain;
	}
}
