package com.cleo.api.neo4j.minimal;

import com.cleo.api.neo4j.pojo.NeoEntity;
import com.cleo.api.neo4j.pojo.Person;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.List;
import org.neo4j.ogm.config.Configuration;
import org.neo4j.ogm.cypher.Filter;
import org.neo4j.ogm.session.Session;
import org.neo4j.ogm.session.SessionFactory;

public class HAProxyTester {

  private static final int NODES = 500;
  
  private static final Supplier<SessionFactory> SESSIONS = Suppliers.memoize(() -> initializeSessionFactory());
  
  private static SessionFactory initializeSessionFactory() {
    Configuration configuration = new Configuration();
    configuration.driverConfiguration()
        .setURI("http://localhost:8090")
        .setCredentials("neo4j", "cleo")
        .setDriverClassName("org.neo4j.ogm.drivers.http.driver.HttpDriver");

    return new SessionFactory(configuration, "com.cleo.api.neo4j.pojo");
  }
  
  private void cleanup(Session session) {
    System.out.println("Deleting all nodes...");
    session.query("MATCH (n) DETACH DELETE n", Maps.newHashMap());
  }
  
  private void write(Session session) {
    System.out.println(String.format("Writing %s nodes...", NODES));
    for(int i = 0; i < NODES; ++i) {
      session.save(
          NeoEntity.newEntity(Person.class)
              .setIndex(i)
              .setName(Utils.generateName())
      );
    }
  }
  
  private void read(Session session) {
    System.out.println(String.format("Reading %s nodes...", NODES));
    
    long start = System.currentTimeMillis();
    
    List<Person> read = Lists.newArrayList();
    for(int i = 0; i < NODES; ++i) {
      read.addAll(session.loadAll(Person.class, new Filter("index", i)));
    }

    System.out.println(String.format("Read %s nodes in %s ms.", read.size(), System.currentTimeMillis() - start));
  }
  
  private void run() {
    try {
      Session session = SESSIONS.get().openSession();
      
      cleanup(session);
      write(session);
      read(session);
    } catch(Exception ex) {
      ex.printStackTrace(System.out);
    } finally {
      System.exit(0);
    }
  }
  
  public static void main(String[] args) {
    new HAProxyTester().run();
  }
}
