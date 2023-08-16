package crafty;

import java.util.HashMap;

import sim.engine.SimState;
import sim.engine.Steppable;

public class SupplyUpdater implements ModelState,Steppable{

	ModelRunner modelRunner;
	@Override
	public void step(SimState arg0) {
		modelRunner.getState(DataLoader.class).updateSupply();
		
	}

	@Override
	public void setup(ModelRunner modelRunner) {
		this.modelRunner = modelRunner;
		
	}

	@Override
	public void onStartGo() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void go() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onEndGo() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void toSchedule() {
		modelRunner.schedule.scheduleRepeating(0, 2, this, 1.0);
		
	}
	
	public HashMap<String, Double> getSupplyMap(){
		if(modelRunner != null) {
		return modelRunner.getState(DataLoader.class).getGlobalProductionMap();
		}
		return null;
	}

}
