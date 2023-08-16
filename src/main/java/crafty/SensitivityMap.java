package crafty;


import java.util.HashMap;

public class SensitivityMap extends HashMap<String, Double> {

	public void putAll(SensitivityMap sensitivityMap) {
		sensitivityMap.keySet().forEach(key -> {
			this.put(key, sensitivityMap.get(key));
		});
	}

	public void add(String serviceName, String capitalName, Double sensitivity) {
		this.put(serviceName + capitalName, sensitivity);
	}

	public double get(String serviceName, String capitalName) {
		return this.get(serviceName + capitalName);
	}
}
