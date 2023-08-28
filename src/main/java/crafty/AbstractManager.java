package crafty;


import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import modelRunner.AbstractModelRunner;
import modelRunner.ModelRunner;
import sim.engine.SimState;
import sim.engine.Steppable;
import tech.tablesaw.api.Table;
import updaters.AbstractUpdater;

public abstract class AbstractManager extends AbstractUpdater {
	protected AbstractModelRunner modelRunner;
	protected String managerType;
	protected int id;
	protected double competitiveness;
	protected Set<AbstractCell> landSet = new HashSet<AbstractCell>();
	protected Table servicesTable; // to store all total production table
	protected Table sensitivityTable;
	protected SensitivityMap sensitivityMap = new SensitivityMap();
	protected HashMap<String, Double> serviceProductionMap = new HashMap<>();

	public HashMap<String, Double> getServiceProductionMap() {
		return serviceProductionMap;
	}

	public void setServiceProductionMap(HashMap<String, Double> serviceProductionMap) {
		this.serviceProductionMap = serviceProductionMap;
	}

	public void setManagerType(String managerType) {
		this.managerType = managerType;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Override
	public void setup(AbstractModelRunner modelRunner) {

	}


	protected abstract void managerProduce();

	protected abstract void managerAbandon();

	protected abstract void managerSearch();

	protected abstract void managerCompete();


	public String getManagerType() {
		return managerType;
	}

	public int getId() {
		return id;
	}

	public double getCompetitiveness() {
		return competitiveness;
	}

	public void setCompetitiveness(double competitiveness) {
		this.competitiveness = competitiveness;
	}

	public Set<AbstractCell> getLandSet() {
		return landSet;
	}

	public void setLandSet(Set<AbstractCell> landSet) {
		this.landSet = landSet;
	}

//	public Set<LandCell> getSearchedLandSet() {
//		return searchedLandSet;
//	}
//
//	public void setSearchedLandSet(Set<LandCell> searchedLandSet) {
//		this.searchedLandSet = searchedLandSet;
//	}

	public Table getServicesTable() {
		return servicesTable;
	}

	public void setServicesTable(Table servicesTable) {
		this.servicesTable = servicesTable;
	}

	public Table getSensitivityTable() {
		return sensitivityTable;
	}

	public void setSensitivityTable(Table sensitivityTable) {
		this.sensitivityTable = sensitivityTable;
	}

	public void abandonCell(AbstractCell cell) {
		landSet.remove(cell);
	}

	public void takeOverCell(AbstractCell cell) {
		landSet.add(cell);
	}

	public SensitivityMap getSensitivityMap() {
		return sensitivityMap;
	}

	public void setSensitivityMap(SensitivityMap sensitivityMap) {
		this.sensitivityMap = sensitivityMap;
	}

	@Override
	public void step(SimState args) {
//		managerProduce();
		managerAbandon();
		managerSearch();
		managerCompete();

	}
}
