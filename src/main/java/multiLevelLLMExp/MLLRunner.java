package multiLevelLLMExp;

import crafty.DataCenter;
import display.GridOfCharts;
import experiments.Intra;
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
		doLoop(MLLRunner.class, args);
		System.exit(0);
	}

	@Override
	public void loadStateManager() {
		////////////Experimental parameters///////////////////
		meatGoal = 1;
		cropGoal = 4;
		divGoal = 2;
		limit = 1;
		policyLag = 5;
		meatLag = 5;
		cropLag = 5;
		paInertia = 0.1;
		threshold = 0.3;
		endYearProtc = 0;
		DataCenter dataCenter = new DataCenter(serviceNameFile, capitalNameFile, agentFilePath, baselineMapFilePath,
				anualCapitalFilePath, anualDemandFile);
		stateManager.add(dataCenter);
		stateManager.add(new SupplyInitializer());
		stateManager.add(new CapitalUpdater());
		stateManager.add(new DemandUpdater());
		stateManager.add(new InfluencedUtilityUpdater());
		stateManager.add(dataCenter.getManagerSet());
		stateManager.add(new SupplyUpdater());

		
	    stateManager.add(new AgricultureInsitutionMultiLLM());
	//	stateManager.add(new NatureInstitutionMultiLLM());
		
		stateManager.add(new GatewayConnector());
		stateManager.add(new MapUpdater());
		stateManager.add(new GridOfCharts());
	}
	
	

}