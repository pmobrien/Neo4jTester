package com.cleo.api.neo4j.pojo;

import org.neo4j.ogm.annotation.NodeEntity;

@NodeEntity
public class Person extends NeoEntity {

  private String name;
  private long index;
  
  public String getName() {
    return name;
  }

  public Person setName(String name) {
    this.name = name;
    return this;
  }

  public long getIndex() {
    return index;
  }

  public Person setIndex(long index) {
    this.index = index;
    return this;
  }
}
