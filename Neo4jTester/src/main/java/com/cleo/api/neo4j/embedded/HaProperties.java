package com.cleo.api.neo4j.embedded;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.nio.file.Paths;

public class HaProperties {

  private static final String NEO_HA_PROPERTIES = "neo4j-ha.properties";
  private static final Supplier<Path> PATH = Suppliers.memoize(() -> initializePath());
  
  public static Path getPath() {
    return PATH.get();
  }
  
  private static Path initializePath() {
    Path path = Paths.get(EmbeddedConnector.NEO_STORE_PATH.get().toString(), NEO_HA_PROPERTIES);
    
    try {
      path.toFile().createNewFile();
      
      try(PrintWriter writer = new PrintWriter(path.toFile())) {
        writer.write(
            new StringBuilder()
                .append("ha.server_id=").append(getServerId()).append(System.lineSeparator())
                .append("ha.initial_hosts=").append(getInitialHosts()).append(System.lineSeparator())
                .append("ha.pull_interval=").append(5).append(System.lineSeparator())
                .append("ha.tx_push_factor=").append(1).append(System.lineSeparator())
                .append("ha.allow_init_cluster=").append(getAllowInitCluster()).append(System.lineSeparator())
                .toString()
        );
      }

      Method addUrl = URLClassLoader.class.getDeclaredMethod("addURL", new Class[] { URL.class });
      addUrl.setAccessible(true);
      addUrl.invoke(
          ClassLoader.getSystemClassLoader(),
          new Object[] {
            EmbeddedConnector.NEO_STORE_PATH.get().toFile().toURI().toURL() 
          }
      );
    } catch(IOException | ReflectiveOperationException ex) {
      ex.printStackTrace(System.out);
    }
    
    return path;
  }
  
  private static int getServerId() {
    String serverId = System.getProperty("server_id");
    
    return serverId == null
        ? 1
        : Integer.parseInt(serverId);
  }

  private static String getInitialHosts() {
    return "localhost:5001";
  }
  
  private static boolean getAllowInitCluster() {
    return true;
  }
}
