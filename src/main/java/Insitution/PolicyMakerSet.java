package Insitution;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import crafty.DataLoader;
import crafty.ModelRunner;
import crafty.ModelState;

public class PolicyMakerSet extends HashSet<PolicyMaker> implements ModelState {

	/**
	 * 
	 */

	private static final long serialVersionUID = 1L;
	ModelRunner modelRunner;
	HashMap<String, Double> interventionMap = null;
	HashMap<String, List<Double>> demandHistoryMap = new HashMap<>();
	HashMap<String, List<Double>> supplyHistoryMap = new HashMap<>();

	@Override
	public void setup(ModelRunner modelRunner) {
		this.modelRunner = modelRunner;

	}

	@Override
	public void onStartGo() {

	}

	@Override
	public void go() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onEndGo() {
		// making policy for next tick
		updateHistory();
		if (modelRunner.schedule.getTime() == 0.) {
			setGoals();
		}
		refreshInterventionMap();
		makePolicies();
	}

	public PolicyMaker findPolicyMaker(int id) {
		PolicyMaker target = null;
		for (PolicyMaker policyMaker : this) {
			if (policyMaker.getID() == id) {
				target = policyMaker;
				return target;
			}
			;
		}
		return null;
	}

	public HashMap<String, Double> getInterventionMap() {
		return interventionMap;
	}

	private void setGoals() {
		// add agricultural policyMaker
		PolicyMaker agriPolicyMaker = new PolicyMaker();
		agriPolicyMaker.setID(0);
		agriPolicyMaker.setFinalGoal(modelRunner, 1, 0.7,5000000.);
	 //   agriPolicyMaker.setGoals(modelRunner, 1, 3.0);
		//agriPolicyMaker.setGoals(modelRunner, 0, 2.5);
		// agriPolicyMaker.setGoals(modelRunner, 4, 0.7);
		// agriPolicyMaker.setGoals(modelRunner, 4, 5.0);
		this.add(agriPolicyMaker);

		// add Forestry policyMaker
		PolicyMaker forePolicyMaker = new PolicyMaker();
		forePolicyMaker.setID(1);
		forePolicyMaker.setFinalGoal(modelRunner, 2, 2.,10000.);
		this.add(forePolicyMaker);

		// add Recreation policyMaker
		PolicyMaker recrPolicyMaker = new PolicyMaker();
		recrPolicyMaker.setID(2);
		recrPolicyMaker.setGoals("Recreation", 0, modelRunner.totalTicks, 1.54 * Math.pow(10, 16),
				2 * Math.pow(10, 16));
		// this.add(recrPolicyMaker);
	}

	private void refreshInterventionMap() {
		interventionMap = new HashMap<>();
		modelRunner.getState(DataLoader.class).getServiceNameList().forEach(service -> {
			interventionMap.put(service, 0.);
		});

	}

	private void makePolicies() {
		this.forEach(policyMaker -> {
			// 1.predict demand and supply
			policyMaker.policyMap.keySet().forEach(serviceName -> {
				Double demandPredicted = policyMaker.predictDemand(demandHistoryMap.get(serviceName));
				Double supplyPredicted = policyMaker.predictSupply(supplyHistoryMap.get(serviceName));

				policyMaker.policyMap.get(serviceName).getPredictedDemand().add(demandPredicted);
				policyMaker.policyMap.get(serviceName).getPredictedSupply().add(supplyPredicted);

				// 1.5 update policy modifier
				policyMaker.updatePolicyModifier(serviceName, supplyHistoryMap.get(serviceName), supplyPredicted);

				// 2.make policy
				int ticks = (int) modelRunner.schedule.getTime();
				double intervention = policyMaker.makePolicy(serviceName, ticks, demandPredicted, supplyPredicted);

				// 3. add this intervention to the current interventionMap
				interventionMap.put(serviceName, interventionMap.get(serviceName) + intervention);
				System.out.println(serviceName + " intervention: " + intervention);
			});
		});
	}

	private void updateHistory() {
		DataLoader dataLoader = modelRunner.getState(DataLoader.class);
		if (modelRunner.schedule.getTime() == 0.) {
			dataLoader.getServiceNameList().forEach(service -> {
				List<Double> demandHistoryList = new ArrayList<Double>();
				demandHistoryList.add(dataLoader.getAnualDemand().get(service));
				demandHistoryMap.put(service, demandHistoryList);
				List<Double> supplyHistoryList = new ArrayList<Double>();
				supplyHistoryList.add(dataLoader.getGlobalProductionMap().get(service));
				supplyHistoryMap.put(service, supplyHistoryList);

			});
		} else {

			dataLoader.getServiceNameList().forEach(service -> {
				demandHistoryMap.get(service).add(dataLoader.getAnualDemand().get(service));
				supplyHistoryMap.get(service).add(dataLoader.getGlobalProductionMap().get(service));
			});
		}
	}

	public HashMap<String, List<Double>> getDemandHistoryMap() {
		return demandHistoryMap;
	}

	public HashMap<String, List<Double>> getSupplyHistoryMap() {
		return supplyHistoryMap;
	}

	@Override
	public boolean addAll(Collection<? extends PolicyMaker> c) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void toSchedule() {
		// TODO Auto-generated method stub
		
	}
}