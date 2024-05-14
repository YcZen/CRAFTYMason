package multiLevelLLMExp;

import java.util.ArrayList;
import java.util.List;

import crafty.DataCenter;
import display.GridOfCharts;
import experiments.Intra;
import llmExp.ModelRunnerInterop;
import sim.engine.SimState;
import updaters.CapitalUpdater;
import updaters.DemandUpdater;
import updaters.InfluencedUtilityUpdater;
import updaters.MapUpdater;
import updaters.SupplyInitializer;
import updaters.SupplyUpdater;

public class MLLRunner extends Intra{
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6624456138431087734L;
	public int period = 10;
	
	
	public MLLRunner(long seed) {
		super(seed);
		loadStateManager();
	}

	public static void main(String[] args) {
	
//		doLoop(MLLRunner.class, args);
//		System.exit(0);
		SimState state = new MLLRunner(System.currentTimeMillis());
		List<Integer> actionList = new ArrayList<Integer>();

		state.start();
		do
		if (!state.schedule.step(state)) break;
		while(state.schedule.getSteps() <= 150);
		state.finish();

	}

	@Override
	public void loadStateManager() {
		////////////Experimental parameters///////////////////
		meatGoal = 3.5;
		cropGoal = 4;
		divGoal = 2;
		limit = 0.9;
		policyLag = 5;
		meatLag = 5;
		cropLag = 5;
		paInertia = 1.0;//0.1;
		threshold = 0.3;
		endYearProtc = 0;
		DataCenter dataCenter = new DataCenter(serviceNameFile, capitalNameFile, agentFilePath, baselineMapFilePath,
				anualCapitalFilePath, anualDemandFile);
		stateManager.add(dataCenter);	
		stateManager.add(new CapitalUpdater());
		stateManager.add(new DemandUpdater());
		stateManager.add(new InfluencedUtilityUpdater());
		stateManager.add(dataCenter.getManagerSet());
		stateManager.add(new InitialProductionCalculator()); //This only calculate the initial production
		stateManager.add(new SupplyUpdater());

		stateManager.add(new GatewayConnector());
		
	    stateManager.add(new AgricultureInsitutionMultiLLM());
		stateManager.add(new NatureInstitutionMultiLLM());
		

		stateManager.add(new MapUpdater());
	//	stateManager.add(new GridOfCharts());
	}
	
	
	public double getZIntervention() {
		var x = getState(AgricultureInsitutionMultiLLM.class).getPolicyMap().get("decrease meat");
		return x==null ? 0.0:x.getIntervention();
	}
	
	public double getZTotalBudget() {
		return getState(AgricultureInsitutionMultiLLM.class).getTotalBugdet();
	}
	
	public double getZBudgetSurplus() {
		return getState(AgricultureInsitutionMultiLLM.class).getBudgetSurplus();
	}
	
	public double getZbudgetGain() {
		return getState(AgricultureInsitutionMultiLLM.class).budgetGain;
	}

	public double getZNeededIntervention() {
		var x = getState(AgricultureInsitutionMultiLLM.class).getPolicyMap().get("decrease meat");
		return x==null ? 0.0:x.getInterventionNeeded();
	}
	
}