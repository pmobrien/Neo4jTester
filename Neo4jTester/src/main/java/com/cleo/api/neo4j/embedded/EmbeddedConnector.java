package com.cleo.api.neo4j.embedded;

import com.cleo.api.neo4j.Utils;
import com.cleo.api.neo4j.pojo.NeoEntity;
import com.cleo.api.neo4j.pojo.Person;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.Maps;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.neo4j.ogm.config.Configuration;
import org.neo4j.ogm.session.Session;
import org.neo4j.ogm.session.SessionFactory;

public class EmbeddedConnector {
  
  private static final String NEO_HA_PROPERTIES = "neo4j-ha.properties";
  
  private static final Supplier<Path> NEO_STORE_PATH = Suppliers.memoize(() -> initializeNeoStorePath());
  private static final Supplier<Path> NEO_HA_PROPERTIES_PATH = Suppliers.memoize(() -> initializeHaProperties());
  private static final Supplier<SessionFactory> SESSION_FACTORY = Suppliers.memoize(() -> initializeSessionFactory());
  
  private static Path initializeNeoStorePath() {
    Path path = Paths.get(Paths.get("").toAbsolutePath().toString(), "neo-store");
    path.toFile().mkdir();
    
    return path;
  }
  
  private static Path initializeHaProperties() {
    Path path = Paths.get(NEO_STORE_PATH.get().toString(), NEO_HA_PROPERTIES);
    
    try {
      path.toFile().createNewFile();

      try(PrintWriter writer = new PrintWriter(path.toFile())) {
        writer.write(
            new StringBuilder()
                .append("ha.server_id=1").append(System.lineSeparator())
                .append("ha.initial_hosts=localhost:5001").append(System.lineSeparator())
                .append("ha.allow_init_cluster=true").append(System.lineSeparator())
                .toString()
        );
      }

      Method addUrl = URLClassLoader.class.getDeclaredMethod("addURL", new Class[] { URL.class });
      addUrl.setAccessible(true);
      addUrl.invoke(ClassLoader.getSystemClassLoader(), new Object[] { NEO_STORE_PATH.get().toFile().toURI().toURL() });
    } catch(IOException | ReflectiveOperationException ex) {
      ex.printStackTrace(System.out);
    }
    
    return path;
  }
  
  private static SessionFactory initializeSessionFactory() {
    Configuration configuration = new Configuration(NEO_HA_PROPERTIES_PATH.get().getFileName().toString());
    
    configuration.driverConfiguration()
        .setURI(NEO_STORE_PATH.get().toUri().toString())
        .setDriverClassName("org.neo4j.ogm.drivers.embedded.driver.EmbeddedDriver");

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
