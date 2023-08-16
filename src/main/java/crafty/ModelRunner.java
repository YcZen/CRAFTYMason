package crafty;
import java.util.ArrayList;
import java.util.List;

import Insitution.PolicyMakerSet;
import Insitution.ProtectionMaker;
import sim.engine.SimState;
import sim.field.grid.IntGrid2D;

public class ModelRunner extends SimState{
	
	//========================  EU =======================
		String serviceNameFile = "C:\\data\\CRAFTY data set\\data_EUpaper_nocsv\\csv\\Services.csv";
		String capitalNameFile = "C:\\data\\CRAFTY data set\\data_EUpaper_nocsv\\csv\\Capitals.csv";
		String agentFilePath = "C:\\data\\CRAFTY data set\\data_EUpaper_nocsv\\production";
		String baselineMapFilePath = "C:\\data\\CRAFTY data set\\data_EUpaper_nocsv\\worlds\\EU\\regionalisations\\28\\capitals\\Baseline_map.csv";
		String anualCapitalFilePath = "C:\\data\\CRAFTY data set\\data_EUpaper_nocsv\\worlds\\EU\\regionalisations\\28\\capitals\\RCP2_6-SSP1";
		String anualDemandFile = "C:\\data\\CRAFTY data set\\data_EUpaper_nocsv\\worlds\\EU\\regionalisations\\28\\RCP2_6-SSP1\\RCP2_6-SSP1_demands_EU.csv";// - zero bio.csv";
	//====================================================	
		double randomMultiplier;

	IntGrid2D landMap = new IntGrid2D(300, 300);
	private List<ModelState> stateManager = new ArrayList<>();
	public int totalTicks = 70;
	private int mapWidth;
	
	
	public double getCropSupply(){
		//System.out.println(this.getState(DataLoader.class).getUtitlityMap());
		Double returnedValue = this.getState(DataLoader.class).getGlobalProductionMap().get("Crops");
		if(returnedValue != null) {
			return returnedValue; 
		}
		return 0.;
	}
//	
//	public double getCropDemand(){
//		//System.out.println(this.getState(DataLoader.class).getUtitlityMap());
//		Double returnedValue = this.getState(DataLoader.class).getAnualDemand().get("Crops");
//		if(returnedValue != null) {
//			return returnedValue; 
//		}
//		return 0.;
//	}
//	
//	public String getserviceNameFile() { return serviceNameFile; }
//	public void setRserviceNameFile(String nameString) { serviceNameFile = nameString; }
//	//public Object domRandomMultiplier() { return new sim.util.Interval(0.0, 100.0); }
//	
	public int getMapWidth() { return mapWidth; }
	public void setMapWidth(int val) { if (val > 0) mapWidth = val; }
	
	public DataLoader getDataLoader() {
		return this.getState(DataLoader.class);
	}
	
	public ModelRunner(long seed) {
		super(seed);
		loadStateManager();
		
	}
	
	public void start() {
		super.start();
		if(getState(GridOfCharts.class).frame!=null) {
			getState(GridOfCharts.class).frame.dispose();
		}
		stateManager.clear(); // this line is very important!
		loadStateManager() ;
		setup(this);
		toSchedule();
		System.out.println("start again!!!!!!!!!!!!!!!!!!!!!");
		

	}
	
	public <T extends ModelState> T getState(Class<T> stateClass) {
		for (ModelState modelState : stateManager) {
			if (stateClass.isInstance(modelState)) {
				return stateClass.cast(modelState);
			}
		}
		return null;
	}
	
	public void setup(ModelRunner modelRunner) {
		for (ModelState modelState : stateManager) {
			modelState.setup((ModelRunner) this);
		}
	}
	
	public void toSchedule() {
		for (ModelState modelState : stateManager) {
			modelState.toSchedule();
		}
	}
	
	public int indexOf(ModelState modelState) {
		return stateManager.indexOf(modelState);
	}
	
	public void loadStateManager() {
		DataLoader dataLoader = new DataLoader(serviceNameFile, capitalNameFile, agentFilePath, baselineMapFilePath,
				anualCapitalFilePath, anualDemandFile);
		stateManager.add(dataLoader);
		stateManager.add(dataLoader.getManagerSet());
		stateManager.add(dataLoader.getCellSet());
		stateManager.add(new SupplyUpdater());
	//	dataLoader.getUtitlityMap().put("Crops", 0.);
	
//		stateManager.add(new PolicyMakerSet());
//		stateManager.add(new ProtectionMaker());
		stateManager.add(new MapUpdater());
		stateManager.add(new GridOfCharts());
	}
	
	public static void main(String[] args) {
		doLoop(ModelRunner.class, args);
		System.exit(0);
	}
}
