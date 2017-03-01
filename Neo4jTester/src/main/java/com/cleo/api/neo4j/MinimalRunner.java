package com.cleo.api.neo4j;

import java.util.function.Consumer;
import org.neo4j.driver.v1.Session;

public class MinimalRunner implements Runner {

  private final UserService service;
  
  public MinimalRunner() {
    this.service = new UserService();
  }

  @Override
  public Consumer<Session> readNodes() {
    return session -> {
      for(int i = 0; i < NeoConnector.getNodeCount(); ++i) {
        service.getUserByIndex(session, i);
      }
    };
  }
}
