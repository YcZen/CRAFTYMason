package multiLevelLLMExp;

import java.util.ArrayList;
import java.util.List;

import org.antlr.analysis.SemanticContext.AND;

import crafty.DataCenter;
import institution.InformCollector;
import modelRunner.AbstractModelRunner;
import py4j.GatewayServer;
import sim.engine.SimState;
import updaters.AbstractUpdater;

public class GatewayConnector extends AbstractUpdater{

	private GateEntry gateEntry;
	List<Double> meatSupply = new ArrayList<Double>();
	List<Double> meatDemand = new ArrayList<Double>();

	@Override
	public void setup(AbstractModelRunner abstractModelRunner) {
		this.modelRunner = abstractModelRunner;
		GatewayServer.turnLoggingOff();
		GatewayServer server = new GatewayServer();
		server.start();
		System.out.println("Server established...");
		gateEntry = (GateEntry) server.getPythonServerEntryPoint(new Class[] { GateEntry.class });
		
	}

	@Override
	public void toSchedule() {
		modelRunner.schedule.scheduleRepeating(0, modelRunner.indexOf(this), this, 1.0);
		
	}

	@Override
	public void step(SimState arg0) {

		meatDemand.add(modelRunner.getState(DataCenter.class).getAnualDemand().get("Meat"));
		meatSupply.add(modelRunner.getState(DataCenter.class).getGlobalProductionMap().get("Meat"));
		
		if (modelRunner.schedule.getSteps()>0 && modelRunner.schedule.getSteps()%10==0) {
			String demandString = meatDemand.toString();
			String supplyString = meatSupply.toString();
			String info =  String.format("Given the following two lists of numbers: %s and %s."
					+ "They respectively represent the time series of demand and supply of meat in a region.\r\n"
					+ "Write some Python code to analyze the data in the way you think appropriate.\r\n"
					+ "Then, execute the code, provide the results, and interpret the results to inform policymaking.", demandString,supplyString);
			System.out.println(info);
			gateEntry.runAgents(info);
		}
		
	}

}
