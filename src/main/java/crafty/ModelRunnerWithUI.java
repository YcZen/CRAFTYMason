package crafty;

import java.awt.Color;
import javax.swing.JFrame;

import sim.display.Console;
import sim.display.Controller;
import sim.display.Display2D;
import sim.display.GUIState;
import sim.engine.SimState;
import sim.portrayal.Inspector;
import sim.portrayal.grid.ValueGridPortrayal2D;
import sim.util.gui.SimpleColorMap;


public class ModelRunnerWithUI extends GUIState{
	
	public Display2D display;
	public JFrame displayFrame;
	ValueGridPortrayal2D grid2D = new ValueGridPortrayal2D();
	Inspector dataInspector;
	//SimState state;
	
	public ModelRunnerWithUI() {
		super(new ModelRunner(System.currentTimeMillis())); 
	}
	
	public ModelRunnerWithUI(SimState state) {
		super(state);
//	    ((ModelRunner)state).loadStateManager();
//	    ((ModelRunner)state).setup((ModelRunner)state);
	}
	
	public static String getName() {
		return "CRAFTY-EU MASON Test";
	}
	
	public Object getSimulationInspectedObject() { return state;}
	public Inspector getInspector() {
		
			Inspector i = super.getInspector();
			i.setVolatile(true);
			return i;
		
	}
	
	public void init(Controller c) {
		super.init(c);
		display = new Display2D(500,500, this);
		display.setClipping(false);
		displayFrame = display.createFrame();
		displayFrame.setTitle("CRAFTY-EU");
		c.registerFrame(displayFrame);
		displayFrame.setVisible(true);
		display.attach(grid2D, "");		
	}
	
	public void start()
	{
		super.start();
		setupPortrayals();
	}
	
	public void load(SimState state)
	{
		super.load(state);
		setupPortrayals();
	}
	
	public void setupPortrayals() {
		int AFTnumber = ((ModelRunner) state).getState(DataLoader.class).getAgentTypeMap().size();
		grid2D.setMap(new SimpleColorMap(generateDistinctColors(AFTnumber+1)));
		grid2D.setField(((ModelRunner) state).landMap);
		
		display.reset();
		display.setBackdrop(Color.white);
		display.repaint();
		
	}
	
	
	public void quit() {
		super.quit();
		if (displayFrame!=null) displayFrame.dispose();
		displayFrame = null;
		display = null;
	}
	

	public Color[] generateDistinctColors(int count) {
        if (count <= 0) {
            throw new IllegalArgumentException("Count must be positive");
        }

        Color[] distinctColors = new Color[count];
        // Set the first color to white
        distinctColors[0] = Color.WHITE;

        for (int i = 1; i < count; i++) {
            float hue = (float) i / (count - 1);  // adjusted the denominator
            distinctColors[i] = Color.getHSBColor(hue, 1f, 1f); // 1f for max saturation & brightness
        }
        return distinctColors;
    }
	
	
	public static void main(String[] args) {
		ModelRunnerWithUI vid = new ModelRunnerWithUI();
		Console c = new Console(vid);
		c.setVisible(true);
	}
}
