package multiLevelLLMExp;

import javax.swing.UIManager;

import display.ModelRunnerWithUI;
import sim.display.Console;

public class MLLRunnerWithUI extends ModelRunnerWithUI{
	public MLLRunnerWithUI() {
		// super(new ModelRunner(System.currentTimeMillis()));
		super(new MLLRunner(System.currentTimeMillis()));
	}
	
	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		MLLRunnerWithUI vid = new MLLRunnerWithUI();
		Console c = new Console(vid);
		c.setVisible(true);
	}
}
