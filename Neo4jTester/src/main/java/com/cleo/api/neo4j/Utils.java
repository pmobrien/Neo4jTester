package com.cleo.api.neo4j.minimal;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Random;

public class Utils {

  private static final Random RANDOM = new Random(System.currentTimeMillis());

  private static final List<String> FIRST_NAMES = Lists.newArrayList(
      "James",
      "John",
      "Michael",
      "William",
      "David",
      "Richard",
      "Charles",
      "Joseph",
      "Thomas",
      "Mary",
      "Patricia",
      "Linda",
      "Barbara",
      "Elizabeth",
      "Jennifer",
      "Maria",
      "Susan",
      "Margaret",
      "Dorothy"
  );

  private static final List<String> LAST_NAMES = Lists.newArrayList(
      "Smith",
      "Johnson",
      "Williams",
      "Jones",
      "Brown",
      "Davis",
      "Miller",
      "Wilson",
      "Moore",
      "Taylor",
      "Anderson",
      "Thomas",
      "Jackson",
      "White",
      "Harris",
      "Martin",
      "Thompson",
      "Martinez",
      "Garcia",
      "Robinson"
  );

  public static String generateName() {
    return String.format(
        "%s %s",
        FIRST_NAMES.get(Math.abs(RANDOM.nextInt()) % FIRST_NAMES.size()),
        LAST_NAMES.get(Math.abs(RANDOM.nextInt()) % LAST_NAMES.size())
    );
  }
}
