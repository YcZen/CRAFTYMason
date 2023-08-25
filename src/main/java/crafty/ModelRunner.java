package crafty;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import display.GridOfCharts;
import insitution.AgriInstitution;
import sim.engine.SimState;
import sim.field.grid.IntGrid2D;
import tech.tablesaw.api.Table;
import updaters.CapitalUpdater;
import updaters.DemandUpdater;
import updaters.InfluencedUtilityUpdater;
import updaters.MapUpdater;
import updaters.SupplyInitializer;
import updaters.SupplyUpdater;
import updaters.UtilityUpdater;

public class ModelRunner extends SimState{
	
	//========================  EU =======================
		String serviceNameFile = "C:\\data\\CRAFTY data set\\data_EUpaper_nocsv\\csv\\Services.csv";
		String capitalNameFile = "C:\\data\\CRAFTY data set\\data_EUpaper_nocsv\\csv\\Capitals.csv";
		String agentFilePath = "C:\\data\\CRAFTY data set\\data_EUpaper_nocsv\\production";
		String baselineMapFilePath = "C:\\data\\CRAFTY data set\\data_EUpaper_nocsv\\worlds\\EU\\regionalisations\\28\\capitals\\Baseline_map.csv";
		String anualCapitalFilePath = "C:\\data\\CRAFTY data set\\data_EUpaper_nocsv\\worlds\\EU\\regionalisations\\28\\capitals\\RCP2_6-SSP1";
		String anualDemandFile = "C:\\data\\CRAFTY data set\\data_EUpaper_nocsv\\worlds\\EU\\regionalisations\\28\\RCP2_6-SSP1\\RCP2_6-SSP1_demands_EU.csv";// - zero bio.csv";
	//====================================================	
//		String serviceNameFile = "C:\\data\\CRAFTY-GB\\csv\\Services.csv";
//		String capitalNameFile = "C:\\data\\CRAFTY-GB\\csv\\Capitals.csv";
//		String agentFilePath = "C:\\data\\CRAFTY-GB\\production\\RCP2_6-SSP1";
//		String baselineMapFilePath = "C:\\data\\CRAFTY-GB\\worlds\\UK\\Baseline_map_UK.csv";
//		String anualCapitalFilePath = "C:\\data\\CRAFTY-GB\\worlds\\UK\\capitals\\RCP8_5-SSP5";
//		String anualDemandFile = "C:\\data\\CRAFTY-GB\\worlds\\UK\\demand\\RCP8_5-SSP5_demands_UK.csv";
//		
		
	private List<ModelState> stateManager = new ArrayList<>();
	public int totalTicks = 70;
	private double threshold = 0.3;
	private int mapWidth;
	private int mapHeight ;
	public IntGrid2D landMap;
	
	////////////Experimental parameters///////////////////

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
   


//    public void setMeatSupply(double meatSupply) {
//        this.meatSupply = meatSupply;
//    }
	
	
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
		if(getState(AgriInstitution.class).getPolicy("increase meat")!=null) {
		getState(AgriInstitution.class).getPolicy("increase meat").setGoal(goal);
		}
	}
	public double getGoal() {
		if(getState(AgriInstitution.class).getPolicy("increase meat")!=null) {
			return getState(AgriInstitution.class).getPolicy("increase meat").getGoal();
		}
		return 0;
	}
	

	

	public ModelRunner(long seed) {
		super(seed);
		loadStateManager();
		detectMapSize(baselineMapFilePath);
		landMap = new IntGrid2D(mapWidth+1, mapHeight+1);
		
		
	}
	
	public void start() {
		// catcht errors and show them on a small window
		Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
		    public void uncaughtException(Thread t, Throwable e) {
		        SwingUtilities.invokeLater(() -> {
		            JOptionPane.showMessageDialog(null, 
		                "An unexpected error occurred: " + e.getMessage(), 
		                "Error", 
		                JOptionPane.ERROR_MESSAGE);
		        });
		    }
		});
		super.start();
		if(getState(GridOfCharts.class).frame!=null) {
			getState(GridOfCharts.class).frame.dispose();
		}
		stateManager.clear(); // this line is very important for the runs from the GUI.
		loadStateManager() ;
		setup(this);
		toSchedule();
	//	System.out.println("start again");
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
	//	stateManager.add(new PolicyMakerSet());
		//stateManager.add(new ProtectionMaker());
		stateManager.add(new MapUpdater());
		stateManager.add(new GridOfCharts());
	}
	
	public List<ModelState> getStateManager() {
		return stateManager;
	}
	
	private void detectMapSize(String cellDataPath) {

		Table table = Table.read().csv(cellDataPath);
		int rowNumber = table.rowCount();
		for (int i = 0; i < rowNumber; i++) {
			int x = table.row(i).getInt("x");
			int y = table.row(i).getInt("y");
			mapWidth = Math.max(mapWidth, x);
			mapHeight = Math.max(mapHeight, y);
		}
		table = null;
	}
	
	public static void main(String[] args) {
		doLoop(ModelRunner.class, args);
		System.exit(0);
	}
}
