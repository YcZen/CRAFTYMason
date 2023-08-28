package modelRunner;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import crafty.ModelState;
import display.GridOfCharts;
import sim.engine.SimState;
import sim.field.grid.IntGrid2D;

public abstract class AbstractModelRunner extends SimState{
	

	private static final long serialVersionUID = 1L;
	protected  List<ModelState> stateManager = new ArrayList<>();
	public int totalTicks = 70;
	protected  double threshold = 0.3;
	protected  int mapWidth;
	protected  int mapHeight ;
	public IntGrid2D landMap;
	
	public AbstractModelRunner(long seed) {
		super(seed);
	}
	
	public void start() {
		// catcht errors and show them on a small window
		Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
		    public void uncaughtException(Thread t, Throwable e) {
		        SwingUtilities.invokeLater(() -> {
		            JOptionPane.showMessageDialog(null, 
		                "An unexpected error occurred: " + e.getMessage(), 
		                "Error", 
		                JOptionPane.ERROR_MESSAGE);
		        });
		    }
		});
		super.start();
		if(getState(GridOfCharts.class)!=null && getState(GridOfCharts.class).frame!=null) {
			getState(GridOfCharts.class).frame.dispose();
		}
		stateManager.clear(); // this line is very important for the runs from the GUI.
		loadStateManager() ;
		setup(this);
		toSchedule();
	}
	
	public <T extends ModelState> T getState(Class<T> stateClass) {
		for (ModelState modelState : stateManager) {
			if (stateClass.isInstance(modelState)) {
				return stateClass.cast(modelState);
			}
		}
		return null;
	}
	
	public void setup(AbstractModelRunner abstractModelRunner) {
		for (ModelState modelState : stateManager) {
			modelState.setup((AbstractModelRunner) this);
		}
		
	}
	
	public void toSchedule() {
		for (ModelState modelState : stateManager) {
			modelState.toSchedule();
		}
	}
	
	public int indexOf(ModelState modelState) {
		return stateManager.indexOf(modelState);
	}
	
	public abstract void loadStateManager();
	
	public List<ModelState> getStateManager() {
		return stateManager;
	}
	

	public double getThreshold() {
		// TODO Auto-generated method stub
		return threshold;
	}
}
