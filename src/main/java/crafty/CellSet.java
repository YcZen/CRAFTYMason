package crafty;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

import modelRunner.AbstractModelRunner;
import modelRunner.ModelRunner;
import sim.engine.SimState;
import sim.engine.Steppable;

public class CellSet extends HashSet<AbstractCell> {

	private HashMap<String, AbstractCell> cellHashMap = new HashMap<>();

	public void addCellToMap(int x, int y, AbstractCell landCell) {
		cellHashMap.put(x + "" + y, landCell);
		// System.out.println(cellHashMap.get(latitude + "" + longitude));
	}

	public AbstractCell getCell(int x, int y) {

		return cellHashMap.get(x + "" + y);
	}

	@Override
	public boolean addAll(Collection<? extends AbstractCell> c) {
		// TODO Auto-generated method stub
		return false;
	}


	public HashMap<String, AbstractCell> getCellHashMap() {
		return cellHashMap;
	}


}
