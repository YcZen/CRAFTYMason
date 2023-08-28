package crafty;


import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

import modelRunner.AbstractModelRunner;
import modelRunner.ModelRunner;

public class CellSet extends HashSet<AbstractCell> implements ModelState {

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

	@Override
	public void setup(AbstractModelRunner modelRunner) {
		this.forEach(cell -> {
			cell.setup(modelRunner);
		});
	}



	public HashMap<String, AbstractCell> getCellHashMap() {
		return cellHashMap;
	}

	@Override
	public void toSchedule() {
		// TODO Auto-generated method stub
		
	}

}
