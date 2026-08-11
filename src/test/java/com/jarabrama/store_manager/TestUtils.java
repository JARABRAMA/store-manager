package com.jarabrama.store_manager;

import java.time.Instant;

public class TestUtils {
  public static boolean isBetweenToDates(Instant date, Instant first, Instant second) {
    return !date.isAfter(second) && !date.isBefore(first);
  }
}
