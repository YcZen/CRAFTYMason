package updaters;

import crafty.ModelState;
import modelRunner.AbstractModelRunner;
import sim.engine.Steppable;

public abstract class AbstractUpdater implements ModelState, Steppable {
	protected AbstractModelRunner modelRunner;
}
