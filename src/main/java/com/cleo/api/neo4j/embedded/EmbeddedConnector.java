package com.cleo.api.neo4j.embedded;

import com.cleo.api.neo4j.minimal.Utils;
import com.cleo.api.neo4j.pojo.NeoEntity;
import com.cleo.api.neo4j.pojo.Person;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.neo4j.ogm.config.Configuration;
import org.neo4j.ogm.session.Session;
import org.neo4j.ogm.session.SessionFactory;

public class EmbeddedConnector {
  
  public static void main(String[] args) {
    try {
      Path path = Paths.get(Paths.get("").toAbsolutePath().toString(), "neo-store");
      path.toFile().mkdir();
      
      Configuration configuration = new Configuration();
      configuration.driverConfiguration()
          .setURI(path.toUri().toString())
          .setDriverClassName("org.neo4j.ogm.drivers.embedded.driver.EmbeddedDriver");

      SessionFactory sessionFactory = new SessionFactory(configuration, "com.cleo.api.neo4j.pojo");

      Session session = sessionFactory.openSession();
      
      session.save(
          NeoEntity.newEntity(Person.class)
              .setName(Utils.generateName())
      );
      
      session.save(
          NeoEntity.newEntity(Person.class)
              .setName(Utils.generateName())
      );
      
      session.loadAll(Person.class)
          .stream()
          .forEach(person -> System.out.println(person.toJson()));
    } catch(Exception ex) {
      ex.printStackTrace(System.out);
    } finally {
      System.exit(0);
    }
  }
}
