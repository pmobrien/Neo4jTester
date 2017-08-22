package com.cleo.api.neo4j.embedded;

import com.cleo.api.neo4j.Utils;
import com.cleo.api.neo4j.pojo.NeoEntity;
import com.cleo.api.neo4j.pojo.Person;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.Maps;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.neo4j.ogm.config.Configuration;
import org.neo4j.ogm.drivers.embedded.driver.EmbeddedDriver;
import org.neo4j.ogm.session.Session;
import org.neo4j.ogm.session.SessionFactory;

public class EmbeddedConnector {
  
  protected static final Supplier<Path> NEO_STORE_PATH = Suppliers.memoize(() -> initializeNeoStorePath());
  private static final Supplier<SessionFactory> SESSION_FACTORY = Suppliers.memoize(() -> initializeSessionFactory());
  
  private static Path initializeNeoStorePath() {
    Path path = Paths.get(Paths.get("").toAbsolutePath().toString(), "neo-store");
    path.toFile().mkdir();
    
    return path;
  }
  
  private static SessionFactory initializeSessionFactory() {
    Configuration configuration = new Configuration(HaProperties.getPath().getFileName().toString());
    
    configuration.driverConfiguration()
        .setURI(NEO_STORE_PATH.get().toUri().toString())
        .setDriverClassName(EmbeddedDriver.class.getName());

    return new SessionFactory(configuration, "com.cleo.api.neo4j.pojo");
  }
  
  public static Session openSession() {
    return SESSION_FACTORY.get().openSession();
  }
  
  public static void main(String[] args) {
    try {
      Session session = openSession();
      
      session.query("MATCH (n) DETACH DELETE n", Maps.newHashMap());
      
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
