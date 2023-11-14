package test;

import llmExp.AgentEntry;
import py4j.GatewayServer;

public class ExampleClientApplication {

    public static void main(String[] args) {
          GatewayServer.turnLoggingOff();
          GatewayServer server = new GatewayServer();
          server.start();
          AgentEntry agentEntry = (AgentEntry) server.getPythonServerEntryPoint(new Class[] { AgentEntry.class });
          try {
        	  System.out.println(agentEntry.agentRun("0", "+50%"));
          } catch (Exception e) {
              e.printStackTrace();
          }
          server.shutdown();
          
          
    }
}