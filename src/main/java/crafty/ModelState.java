package crafty;

import modelRunner.AbstractModelRunner;

public interface ModelState {
	
	public void setup(AbstractModelRunner abstractModelRunner);

	
	public void toSchedule();
	
}
