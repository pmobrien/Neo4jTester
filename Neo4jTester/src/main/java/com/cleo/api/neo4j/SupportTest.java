package com.cleo.api.neo4j;

import org.neo4j.driver.v1.*;
import java.sql.Timestamp;
import java.util.Date;

public class SupportTest {

  private static final int NODES = 1000;
  
  public static void main(String[] args) {
    java.util.Date startdate = new java.util.Date();

    try(Driver driver = GraphDatabase.driver("bolt://localhost:7687", AuthTokens.basic("neo4j", "cleo"))) {
      try(Session session = driver.session()) {
        System.out.println(String.format("Start loading %s nodes", NODES));
        System.out.println("Start Time : " + new Timestamp(startdate.getTime()));
        
        for(int i = 1; i < NODES; i++) {
          session.run("Create (x:Person { name: 'Some Guy', index:{i}}) return x", Values.parameters("i", i));
        }
      }
      
      Date enddate = new Date();
      float writeTime = (enddate.getTime() - startdate.getTime()) / 1000.F;
      
      System.out.println("End Time : " + new Timestamp(enddate.getTime()));
//      System.out.println("Time Elapsed : " + writeTime + " ms");
      System.out.println(String.format("Write elapsed: %f s, %,.0f wps", writeTime, (float)NODES / writeTime));
      
      System.out.println("");
      
      java.util.Date startdate2 = new java.util.Date();
      
      try(Session session = driver.session()) {
        System.out.println(String.format("Start Matching %s nodes", NODES));
        System.out.println("Start Time : " + new Timestamp(startdate2.getTime()));
        
        for(int i = 1; i < NODES; i++) {
          session.run("Match (x:Person {index:{i}}) return x", Values.parameters("i", i));
//              .next()
//              .get("x")
//              .get("name")
//              .asString();
        }
        
        Date enddate2 = new Date();
        float readTime = (enddate2.getTime() - startdate2.getTime()) / 1000.F;
        
        System.out.println("End Time : " + new Timestamp(enddate2.getTime()));
//        System.out.println("Time Elapsed : " + readTime + " ms");
        System.out.println(String.format("Read elapsed: %f s, %,.0f rps", readTime, (float)NODES / readTime));
      }
    } catch(Exception ex) {
      ex.printStackTrace(System.out);
    } finally {
      System.exit(0);
    }
  }
}
