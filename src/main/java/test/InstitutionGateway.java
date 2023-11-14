package test;


public interface InstitutionGateway {
	
	public void updatePrompt();
	
	public void decide();
	
	public void extractResult();
	
	public String outputIntervention();
	
	public String outputType();

	public String outputSummary();

	
}
