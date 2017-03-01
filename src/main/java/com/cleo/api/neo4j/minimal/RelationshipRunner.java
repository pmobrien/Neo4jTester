package com.cleo.api.neo4j.minimal;

import java.util.function.Consumer;
import org.neo4j.driver.v1.Session;

public class RelationshipRunner implements Runner {

  @Override
  public Consumer<Session> writeNodes() {
    return Runner.super.writeNodes(); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public Consumer<Session> readNodes() {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }
}
