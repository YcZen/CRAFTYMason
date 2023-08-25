package updaters;

import java.util.HashMap;

import crafty.DataCenter;
import crafty.ModelRunner;
import crafty.ModelState;
import sim.engine.SimState;
import sim.engine.Steppable;

public class SupplyInitializer extends AbstractUpdater{

	@Override
	public void step(SimState arg0) {
		modelRunner.getState(DataCenter.class).updateSupply();
		//System.out.println("Supply Updater step");
	}

	@Override
	public void setup(ModelRunner modelRunner) {
		this.modelRunner = modelRunner;
		
	}


	@Override
	public void toSchedule() {
		modelRunner.schedule.scheduleOnce(0, modelRunner.indexOf(this), this);
		
	}
	
//	public HashMap<String, Double> getSupplyMap(){
//		if(modelRunner != null) {
//		return modelRunner.getState(DataLoader.class).getGlobalProductionMap();
//		}
//		return null;
//	}

}
