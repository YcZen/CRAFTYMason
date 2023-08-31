package experiment1;

import java.util.HashMap;

import crafty.DataCenter;
import display.GridOfCharts;
import institution.AgriInstitution;
import modelRunner.ModelRunner;
import updaters.CapitalUpdater;
import updaters.DemandUpdater;
import updaters.InfluencedUtilityUpdater;
import updaters.MapUpdater;
import updaters.SupplyInitializer;
import updaters.SupplyUpdater;

public class IndividualPolicies extends ModelRunner{
	
////////////Experimental parameters///////////////////
	protected  double goal; 

	public IndividualPolicies(long seed) {
		super(seed);
		loadStateManager();
	}
	
	public static void main(String[] args) {
		doLoop(IndividualPolicies.class, args);
		System.exit(0);
	}
	
	public void loadStateManager() {
		DataCenter dataCenter = new DataCenter(serviceNameFile, capitalNameFile, agentFilePath, baselineMapFilePath,
				anualCapitalFilePath, anualDemandFile);
		stateManager.add(dataCenter);
		stateManager.add(new SupplyInitializer());
		stateManager.add(new CapitalUpdater());
		stateManager.add(new DemandUpdater());
	//	stateManager.add(new UtilityUpdater());
		stateManager.add(new InfluencedUtilityUpdater());
		stateManager.add(dataCenter.getManagerSet());
		stateManager.add(dataCenter.getCellSet());
		stateManager.add(new SupplyUpdater());
	
		stateManager.add(new AgriInstitution());
//		stateManager.add(new MapUpdater());
//		stateManager.add(new GridOfCharts());
	}
	


// Getters
public double getMeatSupply() {
if(getState(DataCenter.class).getGlobalProductionMap().get("Meat") != null) {
    return getState(DataCenter.class).getGlobalProductionMap().get("Meat");
}
return 0;
}

// Getter for CropsSupply
public double getCropsSupply() {
if(getState(DataCenter.class).getGlobalProductionMap().get("Crops") != null) {
    return getState(DataCenter.class).getGlobalProductionMap().get("Crops");
}
return 0;
}

// Getter for DiversitySupply
public double getDiversitySupply() {
if(getState(DataCenter.class).getGlobalProductionMap().get("Diversity") != null) {
    return getState(DataCenter.class).getGlobalProductionMap().get("Diversity");
}
return 0;
}

// Getter for TimberSupply
public double getTimberSupply() {
if(getState(DataCenter.class).getGlobalProductionMap().get("Timber") != null) {
    return getState(DataCenter.class).getGlobalProductionMap().get("Timber");
}
return 0;
}

// Getter for CarbonSupply
public double getCarbonSupply() {
if(getState(DataCenter.class).getGlobalProductionMap().get("Carbon") != null) {
    return getState(DataCenter.class).getGlobalProductionMap().get("Carbon");
}
return 0;
}

// Getter for UrbanSupply
public double getUrbanSupply() {
if(getState(DataCenter.class).getGlobalProductionMap().get("Urban") != null) {
    return getState(DataCenter.class).getGlobalProductionMap().get("Urban");
}
return 0;
}

// Getter for RecreationSupply
public double getRecreationSupply() {
if(getState(DataCenter.class).getGlobalProductionMap().get("Recreation") != null) {
    return getState(DataCenter.class).getGlobalProductionMap().get("Recreation");
}
return 0;
}


public int getVEP() {
if (!(getState(DataCenter.class).AFTCounter.isEmpty())) {
    return getState(DataCenter.class).AFTCounter.get("VEP");
}
return 0;
}

public int getInt_AF() {
if (!(getState(DataCenter.class).AFTCounter.isEmpty())) {
    return getState(DataCenter.class).AFTCounter.get("Int_AF");
}
return 0;
}

public int getMix_P() {
if (!(getState(DataCenter.class).AFTCounter.isEmpty())) {
    return getState(DataCenter.class).AFTCounter.get("Mix_P");
}
return 0;
}

public int getIP() {
if (!(getState(DataCenter.class).AFTCounter.isEmpty())) {
    return getState(DataCenter.class).AFTCounter.get("IP");
}
return 0;
}

public int getMin_man() {
if (!(getState(DataCenter.class).AFTCounter.isEmpty())) {
    return getState(DataCenter.class).AFTCounter.get("Min_man");
}
return 0;
}

public int getEP() {
if (!(getState(DataCenter.class).AFTCounter.isEmpty())) {
    return getState(DataCenter.class).AFTCounter.get("EP");
}
return 0;
}

public int getExt_AF() {
if (!(getState(DataCenter.class).AFTCounter.isEmpty())) {
    return getState(DataCenter.class).AFTCounter.get("Ext_AF");
}
return 0;
}

public int getUMF() {
if (!(getState(DataCenter.class).AFTCounter.isEmpty())) {
    return getState(DataCenter.class).AFTCounter.get("UMF");
}
return 0;
}

public int getMultifun() {
if (!(getState(DataCenter.class).AFTCounter.isEmpty())) {
    return getState(DataCenter.class).AFTCounter.get("Multifun");
}
return 0;
}

public int getMix_For() {
if (!(getState(DataCenter.class).AFTCounter.isEmpty())) {
    return getState(DataCenter.class).AFTCounter.get("Mix_For");
}
return 0;
}

public int getUL() {
if (!(getState(DataCenter.class).AFTCounter.isEmpty())) {
    return getState(DataCenter.class).AFTCounter.get("UL");
}
return 0;
}

public int getIA() {
if (!(getState(DataCenter.class).AFTCounter.isEmpty())) {
    return getState(DataCenter.class).AFTCounter.get("IA");
}
return 0;
}

public int getMF() {
if (!(getState(DataCenter.class).AFTCounter.isEmpty())) {
    return getState(DataCenter.class).AFTCounter.get("MF");
}
return 0;
}

public int getMix_Fa() {
if (!(getState(DataCenter.class).AFTCounter.isEmpty())) {
    return getState(DataCenter.class).AFTCounter.get("Mix_Fa");
}
return 0;
}

public int getInt_Fa() {
if (!(getState(DataCenter.class).AFTCounter.isEmpty())) {
    return getState(DataCenter.class).AFTCounter.get("Int_Fa");
}
return 0;
}

public int getUr() {
if (!(getState(DataCenter.class).AFTCounter.isEmpty())) {
    return getState(DataCenter.class).AFTCounter.get("Ur");
}
return 0;
}

public int getP_Ur() {
if (!(getState(DataCenter.class).AFTCounter.isEmpty())) {
    return getState(DataCenter.class).AFTCounter.get("P-Ur");
}
return 0;
}

public HashMap<String, Integer> getAFTCounter(){
return getState(DataCenter.class).AFTCounter;
}




public void setThreshold(double thres) {threshold = thres;}
public double getThreshold() {return threshold;}
public Object domThreshold() {return new sim.util.Interval(0.0, 1.0);}

public void setServiceNameFile(String filePathString) { serviceNameFile = filePathString;}
public String getServiceNameFile() { return serviceNameFile; }

public String getCapitalNameFile() {
return capitalNameFile;
}
public void setCapitalNameFile(String capitalNameFile) {
this.capitalNameFile = capitalNameFile;
}
public String getAgentFilePath() {
return agentFilePath;
}
public void setAgentFilePath(String agentFilePath) {
this.agentFilePath = agentFilePath;
}
public String getBaselineMapFilePath() {
return baselineMapFilePath;
}
public void setBaselineMapFilePath(String baselineMapFilePath) {
this.baselineMapFilePath = baselineMapFilePath;
}
public String getAnualCapitalFilePath() {
return anualCapitalFilePath;
}
public void setAnualCapitalFilePath(String anualCapitalFilePath) {
this.anualCapitalFilePath = anualCapitalFilePath;
}
public String getAnualDemandFile() {
return anualDemandFile;
}
public void setAnualDemandFile(String anualDemandFile) {
this.anualDemandFile = anualDemandFile;
}

public void setGoal(double goal) {

this.goal = goal;

}
public double getGoal() {

	return goal;

}

}
