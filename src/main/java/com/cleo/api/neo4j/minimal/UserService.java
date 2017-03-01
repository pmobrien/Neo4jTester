package com.cleo.api.neo4j.minimal;

import org.neo4j.driver.v1.Session;
import org.neo4j.driver.v1.StatementResult;
import org.neo4j.driver.v1.Values;

public class UserService {

  public void addUser(Session session, String name, long id) {
      session.run(
          "CREATE (person:Person { name: {name}, index: {idx} })",
          Values.parameters("name", name, "idx", id)
      );
  }
  
  public String getUserByIndex(Session session, long id) {
    StatementResult result = session.run(
        "MATCH (person:Person { index: {idx} }) " +
        "RETURN person",
        Values.parameters("idx", id)
    );
    
    return "";
//    return result.next().get("person").get("name").asString();
  }
  
  // adds a meaningless attribute, strictly for testing updates
  public void addAttribute(Session session, long id) {
    StatementResult result = session.run(
        String.format(
          "MATCH (person:Person { index: '%s' }) " +
          "SET person.something = 'something' " +
          "RETURN person",
          id
        )
    );
  }
}
