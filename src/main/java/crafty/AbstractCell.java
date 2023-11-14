package crafty;

import java.util.HashMap;
import java.util.HashSet;

import tech.tablesaw.api.Row;
import tech.tablesaw.api.Table;
import updaters.AbstractUpdater;

public abstract class AbstractCell {
	protected Row cellData;
	protected Table cellServiceTable;
	protected Manager owner;
	protected HashMap<String, Double> capitalHashMap = new HashMap<>();
	protected HashMap<String, Double> serviceProductionMap = new HashMap<>();
	protected boolean isProtected = false;
	protected HashMap<String, Double> capitalFilter = new HashMap<>();
	protected HashMap<String, Double> productionFilter = new HashMap<>();
	protected double protectionIndex;
	protected HashSet<AbstractCell> neighborSet = new HashSet<>();

	public HashMap<String, Double> getCapitalHashMap() {
		return capitalHashMap;
	}

	public Manager getOwner() {
		return owner;
	}

	public void setOwner(Manager owner) {
		this.owner = owner;
	}

	public Table getCellServiceTable() {
		return cellServiceTable;
	}

	public void setCellServiceTable(Table cellServiceTable) {
		this.cellServiceTable = cellServiceTable;
	}

	public Row getInformationTable() {
		return cellData;
	}

	public void setInformationTable(Row cellData) {
		this.cellData = cellData;
	}

	public HashMap<String, Double> getServiceProductionMap() {
		return serviceProductionMap;
	}

	public void setServiceProductionMap(HashMap<String, Double> serviceProductionMap) {
		this.serviceProductionMap = serviceProductionMap;
	}

	public abstract void initializeCapitalFilter();

	public abstract void initializeProductionFilter();

	public abstract void initializeNeighborSet(CellSet cellSet);

	public abstract double calculateProtectionIndex();

	public boolean isProtected() {
		return isProtected;
	}

	public void setProteced(boolean protectOrNot) {
		this.isProtected = protectOrNot;
	}

	public HashMap<String, Double> getProductionFilter() {
		return productionFilter;
	}

	public void initializeProductionFilter(DataCenter dataLoader) {
		// TODO Auto-generated method stub

	}
}
