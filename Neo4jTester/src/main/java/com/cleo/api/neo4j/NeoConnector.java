package com.cleo.api.neo4j;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import java.util.function.Consumer;
import org.neo4j.driver.v1.AuthTokens;
import org.neo4j.driver.v1.Config;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.GraphDatabase;
import org.neo4j.driver.v1.Session;

public class NeoConnector {
  
  private static final Long NODES = 10000L;

  private static final String URI = "bolt://10.10.30.225:7687";
  private static final String USERNAME = "neo4j";
  private static final String PASSWORD = "cleo";
  
  private static final Supplier<Driver> DRIVER = Suppliers.memoize(() -> initializeDriver());
  
  private static void log(String message) {
    System.out.println(message);
  }
  
  private static Driver initializeDriver() {
    log("Initializing Neo4j Driver...");
    
    Driver driver = GraphDatabase.driver(
        URI,
        AuthTokens.basic(USERNAME, PASSWORD),
        Config.build()
            .withMaxIdleSessions(5)
            .toConfig()
    );
    
    try(Session session = driver.session()) {
      // drop any previous data
      session.run(
          "MATCH (n) " +
          "DETACH DELETE n"
      );
    }
    
    return driver;
  }
  
  public static Driver getDriver() {
    return DRIVER.get();
  }
  
  public static long getNodeCount() {
    return NODES;
  }
  
  private void disconnect() {
    log("Closing Neo4j Driver...");
    DRIVER.get().close();
  }
  
  public void run() {
    try {
      DRIVER.get();
      
      Runner runner = getRunner();
      
      log(String.format("Writing %s nodes...", NODES));
      float writeTime = doRunnerAction(runner.writeNodes()) / 1000.F;
      
      log("Reading nodes...");
      float readTime = doRunnerAction(runner.readNodes()) / 1000.F;
      
      log(String.format("Write elapsed: %f s, %,.0f wps", writeTime, (float)NODES / writeTime));
      log(String.format("Read elapsed: %f s, %,.0f rps", readTime, (float)NODES / readTime));
    } catch(Exception ex) {
      ex.printStackTrace(System.out);
    } finally {
      disconnect();
    }
  }
  
  private Runner getRunner() {
    // TODO: command line arg
    return new MinimalRunner();
//    return new ThreadedMinimalRunner();
//    return new UpdateRunner();
  }
  
  private long doRunnerAction(Consumer<Session> action) {
    long start = System.currentTimeMillis();
    
    try(Session session = NeoConnector.getDriver().session()) {
      action.accept(session);
    } catch(Exception ex) {
      ex.printStackTrace(System.out);
    }
    
    return System.currentTimeMillis() - start;
  }
  
  public static void main(String[] args) {
    new NeoConnector().run();
  }
}
