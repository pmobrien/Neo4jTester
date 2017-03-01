package com.cleo.api.neo4j.minimal;

import com.cleo.api.neo4j.pojo.Person;
import com.google.common.collect.Maps;
import org.neo4j.ogm.config.Configuration;
import org.neo4j.ogm.session.Session;
import org.neo4j.ogm.session.SessionFactory;

public class OgmConnector {

  public void run() {
    try {
      Configuration configuration = new Configuration();
      configuration.driverConfiguration()
          .setURI("bolt://localhost:7687")
          .setCredentials("neo4j", "cleo");
      
      SessionFactory sessionFactory = new SessionFactory(configuration, "com.cleo.api.neo4j.minimal");
      
      Session session = sessionFactory.openSession();
      
      session.query("MATCH (n) DETACH DELETE n", Maps.newHashMap());
      
      long start = System.currentTimeMillis();
      
      for(int i = 0; i < 1000; ++i) {
        Person person = new Person()
            .setName("Patrick O'Brien")
            .setIndex(0L);

        session.save(person);
      }
      
      float writeTime = (System.currentTimeMillis() - start) / 1000.F;
      
      System.out.println(String.format("Write elapsed: %f s, %,.0f wps", writeTime, (float)1000 / writeTime));
    } catch(Exception ex) {
      ex.printStackTrace(System.out);
    } finally {
      System.exit(0);
    }
  }
  
  public static void main(String[] args) {
    new OgmConnector().run();
  }
}
