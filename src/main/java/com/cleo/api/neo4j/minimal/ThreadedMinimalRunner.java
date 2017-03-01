package com.cleo.api.neo4j.minimal;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.neo4j.driver.v1.Session;

public class ThreadedMinimalRunner implements Runner {
  
  private static final int THREADS = 5;

  @Override
  public Consumer<Session> writeNodes() {
    return session -> {
      ExecutorService pool = Executors.newCachedThreadPool();

      pool.execute(getWriter(0, 249));
      pool.execute(getWriter(250, 499));
      pool.execute(getWriter(500, 749));
      pool.execute(getWriter(750, 999));
      
      pool.shutdown();
        
      try {
        pool.awaitTermination(1, TimeUnit.MINUTES);
      } catch(InterruptedException ex) {
        Logger.getLogger(ThreadedMinimalRunner.class.getName()).log(Level.SEVERE, null, ex);
      }
    };
  }

  @Override
  public Consumer<Session> readNodes() {
    return new MinimalRunner().readNodes();
  }
  
  private Runnable getWriter(int startIndex, int endIndex) {
    return () -> {
      Session session = NeoConnector.getDriver().session();
      UserService service = new UserService();

      for(int i = startIndex; i <= endIndex; ++i) {
        service.addUser(session, Utils.generateName(), i);
      }
    };
  }
}
