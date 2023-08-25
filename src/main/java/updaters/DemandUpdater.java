package updaters;

import java.util.HashMap;

import crafty.DataCenter;
import crafty.ModelRunner;
import crafty.ModelState;
import sim.engine.SimState;
import sim.engine.Steppable;

public class DemandUpdater extends AbstractUpdater{

	@Override
	public void step(SimState arg0) {
		modelRunner.getState(DataCenter.class).updateDemand();
	//	System.out.println("Utility Updater step");
	}

	@Override
	public void setup(ModelRunner modelRunner) {
		this.modelRunner = modelRunner;
		
	}


	@Override
	public void toSchedule() {
		modelRunner.schedule.scheduleRepeating(0, modelRunner.indexOf(this), this, 1.0);
		
	}

}
