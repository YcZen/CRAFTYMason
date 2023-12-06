package llmExp;

public interface AgentEntry{
	public int agentRun(String actionHistory, String AverageError);
	
	public int agentRun(String policyActions, String meatDemand, String meatSupply);
}
