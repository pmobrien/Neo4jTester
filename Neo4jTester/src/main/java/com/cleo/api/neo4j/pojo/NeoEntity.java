package com.cleo.api.neo4j.pojo;

import com.cleo.api.neo4j.pojo.converters.UUIDConverter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.UUID;
import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.typeconversion.Convert;

public class NeoEntity {

  @GraphId
  private Long id;
  
  @Convert(UUIDConverter.class)
  protected UUID uuid;

  public UUID getUuid() {
    return uuid;
  }

  protected void setUuid(UUID uuid) {
    this.uuid = uuid;
  }
  
  public String toJson() {
    try {
      return new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(this);
    } catch(JsonProcessingException ex) {
      ex.printStackTrace(System.out);
      
      return "";
    }
  }
  
  public static <T extends NeoEntity> T newEntity(Class<T> type) {
    try {
      T instance = type.newInstance();
      instance.setUuid(UUID.randomUUID());
      
      return instance;
    } catch(ReflectiveOperationException ex) {
      return null;  // TODO: something better than this...
    }
  }
}
