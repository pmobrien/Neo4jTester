package com.cleo.api.neo4j;

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
  
  private void cleanup() {
    System.out.println("Deleting all nodes...");
    SESSIONS.get().openSession().query("MATCH (n) DETACH DELETE n", Maps.newHashMap());
  }
  
  private void write() {
    System.out.println(String.format("Writing %s nodes...", NODES));
    
    int failures = 0;
    for(int i = 0; i < NODES; ++i) {
      try {
        SESSIONS.get().openSession().save(
            NeoEntity.newEntity(Person.class)
                .setIndex(i)
                .setName(Utils.generateName())
        );
      } catch(Exception ex) {
        if(failures >= 4) {
          throw ex;
        }
        
        ++failures;
        --i;
        
        System.out.println(String.format("Failure #%s at index %s. Trying again...", failures, i));
      }
    }
  }
  
  private void read() {
    System.out.println(String.format("Reading %s nodes...", NODES));
    
    long start = System.currentTimeMillis();
    
    List<Person> read = Lists.newArrayList();
    
    int failures = 0;
    for(int i = 0; i < NODES; ++i) {
      try {
        read.addAll(SESSIONS.get().openSession().loadAll(Person.class, new Filter("index", i)));
      } catch(Exception ex) {
        if(failures >= 4) {
          throw ex;
        }
        
        ++failures;
        --i;
        
        System.out.println(String.format("Failure #%s at index %s. Trying again...", failures, i));
      }
    }

    System.out.println(String.format("Read %s nodes in %s ms.", read.size(), System.currentTimeMillis() - start));
  }
  
  private void run() {
    try {
      cleanup();
      write();
      read();
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
