package crafty;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.DefaultXYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import net.sourceforge.jFuzzyLogic.fcl.FclParser.declaration_return;
import sim.engine.SimState;
import sim.engine.Steppable;

import javax.swing.*;
import java.awt.*;
import java.sql.PreparedStatement;
import java.util.HashMap;


public class GridOfCharts implements ModelState,Steppable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
    ModelRunner modelRunner;
    public JFrame frame;
    private HashMap<String, XYSeries> totalProductionSeries = new HashMap<String, XYSeries>();
    private HashMap<String, XYSeries> totalDemandSeries = new HashMap<String, XYSeries>();
    private HashMap<String, Double> productionHashMap;
	private HashMap<String, Double> demandHashMap;
	private boolean isInilized = false;
    
    public GridOfCharts() {
    	
        frame = new JFrame("Ecoservices");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

       
    }
    
    public void prepare() {
    	productionHashMap = modelRunner.getState(DataLoader.class).getGlobalProductionMap();
    	demandHashMap = modelRunner.getState(DataLoader.class).getAnualDemand();
    

         for (String productName : productionHashMap.keySet()) {
        	 
        	 XYSeriesCollection dataset = new XYSeriesCollection();
             JFreeChart chart;
             
             XYSeries supplySeries = new XYSeries(productName + " Supply");
             XYSeries demandSeries = new XYSeries(productName + " Demand");
             
             totalProductionSeries.put(productName, supplySeries);
             totalDemandSeries.put(productName,demandSeries);
             
                 dataset.addSeries(supplySeries);
                 dataset.addSeries(demandSeries);
                
                 chart = ChartFactory.createXYLineChart(
                		 productName, "Time", "Quantity", dataset);
                 
              // Set chart and plot background to white
                 chart.setBackgroundPaint(Color.WHITE);
                 XYPlot plot = (XYPlot) chart.getPlot();
                 plot.setBackgroundPaint(Color.WHITE);

                 // Make lines thicker
                 XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) plot.getRenderer();
                 BasicStroke thickStroke = new BasicStroke(2.0f);
                 renderer.setStroke(thickStroke);

             frame.add(new ChartPanel(chart));
         }
         
     
			int dataSize = modelRunner.getState(DataLoader.class).getGlobalProductionMap().size();
			int gridWidth = (int) Math.ceil(Math.sqrt(dataSize));
			frame.setLayout(new GridLayout(gridWidth, gridWidth));
			frame.pack();
			frame.setLocationRelativeTo(null);
			frame.setVisible(true);
	       
         

    }

    private void updateChart() {
        modelRunner.getState(DataLoader.class).getServiceNameList().forEach(productionName -> {
        	totalProductionSeries.get(productionName).add(modelRunner.schedule.getSteps(), productionHashMap.get(productionName));
        	totalDemandSeries.get(productionName).add(modelRunner.schedule.getSteps(),demandHashMap.get(productionName));
        });
    }

	@Override
	public void step(SimState arg0) {
		updateChart();
		
	}

	@Override
	public void setup(ModelRunner modelRunner) {
		this.modelRunner = modelRunner;
		totalProductionSeries.clear();
		totalDemandSeries.clear();
		prepare();
		
		
		
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
