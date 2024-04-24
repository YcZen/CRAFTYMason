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

public class NatureInstitutionMultiLLM extends NatureInstitution {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Override
	protected void initialize() {
		fuzzyPrepare();
		Policy policy = new Policy.Builder().policyName("subsidy to increase diversity").type(PolicyType.SUBSIDY)
				.goal(((MLLRunner) modelRunner).getDivGoal()
						* modelRunner.getState(DataCenter.class).getGlobalProductionMap().get("Diversity"))
				.initialGuess(10000.).inertia(0.2).policyLag(5).targetService("Diversity").build();
		this.register(policy);

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

				List<AbstractCell> topN = sortedList.subList(0, Math.min(n, numOfCellsToProtect));// sortedList.size()));
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
					landCell.getProductionFilter().put("Carbon", 0.0);
					landCell.getProductionFilter().put("Urban", 0.0);
					landCell.getProductionFilter().put("Rereation", 0.0);

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
		System.out.println(unProtectedSet.size()
						/ (double) (modelRunner.getState(DataCenter.class).getCellSet().size()));
	}
}
