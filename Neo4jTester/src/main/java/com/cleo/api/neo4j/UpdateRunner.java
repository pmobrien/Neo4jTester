package com.cleo.api.neo4j;

import java.util.function.Consumer;
import org.neo4j.driver.v1.Session;

public class UpdateRunner implements Runner {
  
  private final UserService service;
  
  public UpdateRunner() {
    this.service = new UserService();
  }
  
  @Override
  public Consumer<Session> readNodes() {
    return session -> {
      for(int i = 0; i < NeoConnector.getNodeCount(); ++i) {
        service.addAttribute(session, i);
      }
    };
  }
}
