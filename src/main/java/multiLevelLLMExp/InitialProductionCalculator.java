package multiLevelLLMExp;

import crafty.DataCenter;
import modelRunner.AbstractModelRunner;
import sim.engine.SimState;
import updaters.AbstractUpdater;

public class InitialProductionCalculator extends AbstractUpdater{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public void step(SimState arg0) {
		

	}

	@Override
	public void setup(AbstractModelRunner modelRunner) {
		this.modelRunner = modelRunner;
		modelRunner.getState(DataCenter.class).updateSupply();

	}

	@Override
	public void toSchedule() {
		

	}


}
