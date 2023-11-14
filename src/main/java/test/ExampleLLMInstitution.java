package test;

import py4j.GatewayServer;

public class ExampleLLMInstitution{

    public static void main(String[] args) {
          GatewayServer.turnLoggingOff();
          GatewayServer server = new GatewayServer();
          server.start();
          InstitutionGateway institution = (InstitutionGateway) server.getPythonServerEntryPoint(new Class[] { InstitutionGateway.class });
          try {
        	  institution.decide();
        	  institution.extractResult();
        	  System.out.println(institution.outputIntervention());
          } catch (Exception e) {
              e.printStackTrace();
          }
          server.shutdown();
          
          
    }
}