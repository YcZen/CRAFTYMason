package crafty;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import sim.engine.SimState;
import sim.engine.Steppable;

import javax.swing.*;
import java.awt.*;


public class GridOfCharts implements ModelState,Steppable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private XYSeries series;
    private int timeElapsed = 0;
    ModelRunner modelRunner;
    public JFrame frame;

    public GridOfCharts() {
    	series = new XYSeries("Random Data");
        frame = new JFrame("Grid of Charts");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new GridLayout(2, 2));

        for (int i = 0; i < 4; i++) {
            JFreeChart chart;
            if (i == 0) {  // Just updating the first chart for demonstration
                XYSeriesCollection dataset = new XYSeriesCollection(series);
                chart = ChartFactory.createXYLineChart(
                        "Dynamic Chart", "Time", "Value", dataset
                );
            } else {
                chart = ChartFactory.createXYLineChart(
                        "Chart " + (i + 1), "X", "Y", null
                );
            }
            XYPlot plot = chart.getXYPlot();
            frame.add(new ChartPanel(chart));
        }

        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private void updateChart() {
        timeElapsed++;
        series.add(timeElapsed, Math.random() * 100);
    }

	@Override
	public void step(SimState arg0) {
		updateChart();
		
	}

	@Override
	public void setup(ModelRunner modelRunner) {
		this.modelRunner = modelRunner;
		timeElapsed=0;
		series.clear();
		
		
		
	}

	@Override
	public void onStartGo() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void go() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onEndGo() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void toSchedule() {
		modelRunner.schedule.scheduleRepeating(0.0, modelRunner.indexOf(this), this, 1.0);
	}

}
