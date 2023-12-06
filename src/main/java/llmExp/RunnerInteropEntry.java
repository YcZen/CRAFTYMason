package llmExp;

import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

import py4j.GatewayServer;
import sim.engine.SimState;

public class RunnerInteropEntry {
	//CopyOnWriteArrayList<ModelRunnerInterop> runnerList = new CopyOnWriteArrayList<ModelRunnerInterop>();
	
	public ModelRunnerInterop getRunner() {
		ModelRunnerInterop state = new ModelRunnerInterop(System.currentTimeMillis());
		//runnerList.add(state);
		return state;
	}
	
	public void removeState(ModelRunnerInterop state) {
		//runnerList.remove(state);
	}
	
	public static void main(String[] args) {
		GatewayServer gatewayServer = new GatewayServer(new RunnerInteropEntry());
		gatewayServer.start();
		System.out.println("Gateway Server Started");
	}
}
