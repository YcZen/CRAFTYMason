package modelRunner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import crafty.DataCenter;
import crafty.ModelState;
import display.GridOfCharts;
import institution.AgriInstitution;
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

public class ModelRunner extends AbstractModelRunner {

	// ======================== EU =======================
	protected String serviceNameFile = "C:\\data\\CRAFTY data set\\data_EUpaper_nocsv\\csv\\Services.csv";
	protected String capitalNameFile = "C:\\data\\CRAFTY data set\\data_EUpaper_nocsv\\csv\\Capitals.csv";
	protected String agentFilePath = "C:\\data\\CRAFTY data set\\data_EUpaper_nocsv\\production";
	protected String baselineMapFilePath = "C:\\data\\CRAFTY data set\\data_EUpaper_nocsv\\worlds\\EU\\regionalisations\\28\\capitals\\Baseline_map.csv";
	protected String anualCapitalFilePath = "C:\\data\\CRAFTY data set\\data_EUpaper_nocsv\\worlds\\EU\\regionalisations\\28\\capitals\\RCP2_6-SSP1";
	protected String anualDemandFile = "C:\\data\\CRAFTY data set\\data_EUpaper_nocsv\\worlds\\EU\\regionalisations\\28\\RCP2_6-SSP1\\RCP2_6-SSP1_demands_EU.csv";// -
																																									// zero
																																									// bio.csv";
	// ====================================================
//		String serviceNameFile = "C:\\data\\CRAFTY-GB\\csv\\Services.csv";
//		String capitalNameFile = "C:\\data\\CRAFTY-GB\\csv\\Capitals.csv";
//		String agentFilePath = "C:\\data\\CRAFTY-GB\\production\\RCP2_6-SSP1";
//		String baselineMapFilePath = "C:\\data\\CRAFTY-GB\\worlds\\UK\\Baseline_map_UK.csv";
//		String anualCapitalFilePath = "C:\\data\\CRAFTY-GB\\worlds\\UK\\capitals\\RCP8_5-SSP5";
//		String anualDemandFile = "C:\\data\\CRAFTY-GB\\worlds\\UK\\demand\\RCP8_5-SSP5_demands_UK.csv";
//		

	public ModelRunner(long seed) {
		super(seed);
		detectMapSize(baselineMapFilePath);
		landMap = new IntGrid2D(mapWidth + 1, mapHeight + 1);

	}

	public void loadStateManager() {
		DataCenter dataCenter = new DataCenter(serviceNameFile, capitalNameFile, agentFilePath, baselineMapFilePath,
				anualCapitalFilePath, anualDemandFile);
		stateManager.add(dataCenter);
		stateManager.add(new SupplyInitializer());
		stateManager.add(new CapitalUpdater());
		stateManager.add(new DemandUpdater());
		// stateManager.add(new UtilityUpdater());
		stateManager.add(new InfluencedUtilityUpdater());
		stateManager.add(dataCenter.getManagerSet());
		stateManager.add(dataCenter.getCellSet());
		stateManager.add(new SupplyUpdater());

		stateManager.add(new AgriInstitution());
		stateManager.add(new MapUpdater());
		stateManager.add(new GridOfCharts());
	}

	public void detectMapSize(String cellDataPath) {

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
