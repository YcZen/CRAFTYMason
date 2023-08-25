package updaters;

import crafty.ModelRunner;
import crafty.ModelState;
import sim.engine.Steppable;

public abstract class AbstractUpdater implements ModelState, Steppable{
	protected ModelRunner modelRunner;
}
