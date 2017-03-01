package com.cleo.api.neo4j.minimal;

import java.util.function.Consumer;
import org.neo4j.driver.v1.Session;

public interface Runner {
  
  default public Consumer<Session> writeNodes() {
    return session -> {
      UserService service = new UserService();
      
      for(int i = 0; i < NeoConnector.getNodeCount(); ++i) {
        service.addUser(session, "Some User" /*Utils.generateName()*/, i);
      }
    };
  }
  
  public Consumer<Session> readNodes();
}
