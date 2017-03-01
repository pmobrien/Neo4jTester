package com.cleo.api.neo4j.embedded;

import java.util.UUID;
import org.neo4j.ogm.typeconversion.AttributeConverter;

public class UUIDConverter implements AttributeConverter<UUID, String> {

  @Override
  public String toGraphProperty(UUID uuid) {
    return uuid.toString();
  }

  @Override
  public UUID toEntityAttribute(String value) {
    return UUID.fromString(value);
  }
}
