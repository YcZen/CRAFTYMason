package insitution;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.checkerframework.checker.units.qual.Length;

import crafty.AbstractCell;
import crafty.CellSet;
import crafty.DataCenter;
import crafty.LandCell;
import crafty.ModelRunner;
import crafty.ModelState;

public class ProtectionMaker implements ModelState {

	double collectIntervention = 0;
	double collectGoal;
	double collectUnprotectedProportion;
	ModelRunner modelRunner;
	HashSet<AbstractCell> unProtectedSet = new HashSet<>();
	protected HashMap<String, SimplePolicy> policyMap = new HashMap<>();
	protected double supplyPredicted;
	protected double learningRate = 0.3;
	protected Integer id = null;
	String serviceType;

	public void setGoals(String serviceType, int startYear, int endYear, Double startQuantity, Double endQuantity) {
		List<Double> goalsDoubles = new ArrayList<>();
		double slope = (double) (endQuantity - startQuantity) / (double) (endYear - startYear);
		double intercept = startQuantity - slope * startYear;
		for (int i = startYear; i <= endYear; i++) {
			goalsDoubles.add(slope * (double) i + intercept);
		}
		SimplePolicy policy = new SimplePolicy();
		policy.setServiceType(serviceType);
		policy.setPolicyGoal(endQuantity);
		policy.setDecomposedGoals(goalsDoubles);
		policyMap.put(serviceType, policy);
	}

	public ProtectionMaker setGoals(ModelRunner modelRunner, int serviceIndex, double quantity) {
		// this.modelRunner = modelRunner;

		this.serviceType = modelRunner.getState(DataCenter.class).getServiceNameList().get(serviceIndex);
		int startYear = 0;
		int endYear = modelRunner.totalTicks;
		double startQuantity = modelRunner.getState(DataCenter.class).getInitSupplyMap().get(serviceType);
		double endQuantity = quantity * startQuantity;
		setGoals(serviceType, startYear, endYear, startQuantity, endQuantity);
		modelRunner.getState(CellSet.class).forEach(cell -> {
			if (cell.isProtected() == false) {
				unProtectedSet.add(cell);
			}
		});
		return this;
	}

	public List<Double> getGoals(String serviceType) {
		return policyMap.get(serviceType).getDecomposedGoals();
	}

	// 2. Predict next-year demand.
	public Double predictDemand(List<Double> list) {
		return exponentialSmoothing(list, 0.2);
	}

	// 3. Evaluate historic policy effectiveness.
	// 4. Adjust policy intervention coefficient.
	public void updatePolicyModifier(String service, List<Double> historicalSupply) {
		SimplePolicy policy = policyMap.get(service);
		double interventionModifier = policy.getIntervModifier();
		int recentTicks = 5;
		if (historicalSupply.size() >= recentTicks) {
			int start = historicalSupply.size() - recentTicks;
			int end = historicalSupply.size();
			List<Double> recentSupply = historicalSupply.subList(start, end);
			List<Double> recentGoals = policy.getDecomposedGoals().subList(start, end);
			double averageGap = 0;
			for (int i = 0; i < recentTicks; i++) {
				averageGap += (recentGoals.get(i) - recentSupply.get(i)) / recentGoals.get(i);
			}
			averageGap = averageGap / recentTicks;
			if ((averageGap * this.learningRate) + interventionModifier >= 0) {
				// policy.setIntervModifier((0 + averageGap * this.learningRate) +
				// interventionModifier);
				policy.setIntervModifier( averageGap * this.learningRate + interventionModifier);
				System.out.println("learningRate: " + learningRate);
				System.out.println("average gap: " + averageGap);
				;
			}

			// policy.setIntervModifier(averageGap * this.learningRate +
			// interventionModifier);
			System.out.println(service + "intervention modifier: " + policy.getIntervModifier());
		}

	}

	public Double predictSupply(List<Double> list) {
		return exponentialSmoothing(list, 0.5);
	}

	// 6. Make policy
	public void makePolicy(String service) {
		SimplePolicy policy = policyMap.get(service);
		// making policy for next tick, so the goal should be the goal in next tick.
		policy.setIntervention(policy.getIntervModifier());// * 2000);
		// intervention=policy.getIntervention();
		// intervention = policy.getIntervModifier() * 7000;
		// return intervention;
	}

	private double exponentialSmoothing(List<Double> list, double alpha) {
		double prevSmoothed = list.get(0);

		// Apply exponential smoothing to each data point
		for (int i = 1; i < list.size(); i++) {
			double currValue = list.get(i);
			double currSmoothed = alpha * currValue + (1 - alpha) * prevSmoothed;
			prevSmoothed = currSmoothed;
		}

		return prevSmoothed;
	}

//	public double getIntervention() {
//		return intervention;
//	}

	public double getSupplyPredicted() {
		return supplyPredicted;
	}

	public void setSupplyPredicted(double supplyPredicted) {
		this.supplyPredicted = supplyPredicted;
	}

//	public double sigmoid(double x) {
//		return 1.1 / (1 + Math.exp(-x));
//	}

	public ProtectionMaker setID(int id) {
		this.id = id;
		return this;
	}

	public int getID() {
		return id;
	}

	public HashMap<String, SimplePolicy> getPolicyMap() {
		return policyMap;
	}

	public void setProtectedAreas(String serviceType) {
		SimplePolicy policy = policyMap.get(serviceType);
		double intervention = policy.getIntervention();
		//int n = (int) (unProtectedSet.size() * intervention); 
		int n = (int) (unProtectedSet.size() * 0.3);

		List<AbstractCell> sortedList = new ArrayList<>(unProtectedSet);
		Collections.sort(sortedList,
				(a, b) -> Double.compare(((LandCell) b).getProtectionIndex(), ((LandCell) a).getProtectionIndex()));
		
		
		List<AbstractCell> topN = sortedList.subList(0, Math.min(n, sortedList.size()));
		
		if(unProtectedSet.size()/(double) (modelRunner.getState(CellSet.class).size()) <= 0.7) {
			topN = new ArrayList<AbstractCell>();
			System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
		}
		// change the state of cells to protected and modify
		// landCell.getProductionFilter()
		topN.forEach(landCell -> {

			landCell.setProteced(true);

//			landCell.getProductionFilter().put("Food.crops", 0.0);
//			landCell.getProductionFilter().put("Fodder.crops", 0.0);
//			landCell.getProductionFilter().put("GF.redMeat", 0.0);
//			landCell.getProductionFilter().put("Fuel", 0.0);
//			landCell.getProductionFilter().put("Softwood", 1.0);
//			landCell.getProductionFilter().put("Hardwood", 1.0);
//			landCell.getProductionFilter().put("Biodiversity", 1.0);
//			landCell.getProductionFilter().put("Carbon", 1.0);
//			landCell.getProductionFilter().put("Recreation", 1.0);
//			landCell.getProductionFilter().put("Flood.reg", 1.0);
//			landCell.getProductionFilter().put("Employment", 1.0);
//			landCell.getProductionFilter().put("Ldiversity", 1.0);
//			landCell.getProductionFilter().put("GF.milk", 0.0);
//			landCell.getProductionFilter().put("Sus.Prod", 1.0);
			
			landCell.getProductionFilter().put("Meat", 0.0);
			landCell.getProductionFilter().put("Crops", 0.0);
			landCell.getProductionFilter().put("Diversity", 1.0);
			landCell.getProductionFilter().put("Timber", 1.0);
			landCell.getProductionFilter().put("Carbon", 0.0);
			landCell.getProductionFilter().put("Urban", 0.0);
			landCell.getProductionFilter().put("Rereation", 1.0);
			
			//update production on protected area
			modelRunner.getState(DataCenter.class).getServiceNameList().forEach(service -> {
				double newProduction = landCell.getServiceProductionMap().get(service) * landCell.getProductionFilter().get(service);
				landCell.getServiceProductionMap().put(service, newProduction);
				
			});
			
		});

		// Remove the selected LandCell instances from unProtectedSet
		unProtectedSet.removeAll(topN);
		System.out.println("unProtected proportion:"
				+ (double) (unProtectedSet.size()) / (double) (modelRunner.getState(CellSet.class).size()));
		System.out.println("Protection intervention: " + policy.getIntervention());
	}

	@Override
	public void setup(ModelRunner modelRunner) {
		this.modelRunner = modelRunner;

	}

	
	public void onStartGo() {
		if (modelRunner.schedule.getTime() == 0) {
			setGoals(modelRunner, 2, 2.0);
		}
		makePolicy(serviceType);
		setProtectedAreas(serviceType);

	}

	
	public void go() {
		// TODO Auto-generated method stub

	}

	
	public void onEndGo() {
		updateCollectedData();
		updatePolicyModifier(serviceType,
				modelRunner.getState(PolicyMakerSet.class).getSupplyHistoryMap().get(serviceType));
	}

	public void updateCollectedData() {
		collectIntervention = policyMap.get(serviceType).getIntervention();
		collectGoal = policyMap.get(serviceType).getDecomposedGoals().get((int)modelRunner.schedule.getTime());
		collectUnprotectedProportion = (double) (unProtectedSet.size())
				/ (double) (modelRunner.getState(CellSet.class).size());
	}

	@Override
	public void toSchedule() {
		// TODO Auto-generated method stub
		
	}
}
