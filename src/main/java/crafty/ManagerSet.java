package crafty;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

public class ManagerSet extends HashSet<AbstractManager> implements ModelState {
	List<AbstractManager> managerList;

	public void generateAgents(int numberOfAManagers) {
		// TODO Auto-generated method stub

	}

	public AbstractManager findAgent(String nameString, int id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setup(ModelRunner modelRunner) {
		managerList = new ArrayList<>(this);
		Collections.shuffle(managerList);
		this.forEach(agent -> {
			agent.setup(modelRunner);
		});
	}

	@Override
	public void onStartGo() {
		managerList = new ArrayList<>(this);
		Collections.shuffle(managerList);
		this.forEach(agent -> {
			agent.onStartGo();
		});

	}

	@Override
	public void go() {

		managerList.forEach(agent -> {
			agent.go();
		});

	}

	@Override
	public void onEndGo() {
		this.forEach(agent -> {
			agent.onEndGo();
		});
	}

	@Override
	public boolean addAll(Collection<? extends AbstractManager> c) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void toSchedule() {
		this.forEach(agent -> {
			agent.toSchedule();
		});
		
	}

}
