package updaters;

import java.util.HashMap;

import crafty.DataCenter;
import crafty.ModelRunner;
import crafty.ModelState;
import insitution.AbstractInstitution;
import sim.engine.SimState;
import sim.engine.Steppable;

public class UtilityUpdater extends AbstractUpdater{

	@Override
	public void step(SimState arg0) {
		modelRunner.getState(DataCenter.class).updateUtility();
		System.out.println("running");
		
		
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
