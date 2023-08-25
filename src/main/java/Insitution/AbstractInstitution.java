package insitution;

import java.util.HashMap;

import crafty.ModelRunner;
import crafty.ModelState;
import sim.engine.Steppable;
import updaters.AbstractUpdater;

public abstract class AbstractInstitution extends AbstractUpdater {
	
	String name;
	double uncertainties;
	double totalBugdet;
	HashMap<String, Policy> policyMap = new HashMap<String, Policy>();
	ModelRunner modelRunner;
	
	protected abstract void initialize();
	protected abstract void collectInformation();
	protected abstract void predict();
	protected abstract void policyEvaluation();
	protected abstract void policyAdaptation();
	protected abstract void budgetUpdate();
	protected abstract void resourceAllocation();
	public abstract void implementPolicy();
	public abstract void updatePolicyHistory();
	protected abstract void fuzzyPrepare();
	
	public void register(Policy policy) {
		policyMap.put(policy.getName(), policy);
	}
	
	public Policy getPolicy(String policyName) {
		return policyMap.get(policyName);
	}
	public double getTotalBugdet() {
		return totalBugdet;
	}
//	public void setTotalBugdet(double totalBugdet) {
//		this.totalBugdet = totalBugdet;
//	}
	public HashMap<String, Policy> getPolicyMap() {
		return policyMap;
	}
	
}
