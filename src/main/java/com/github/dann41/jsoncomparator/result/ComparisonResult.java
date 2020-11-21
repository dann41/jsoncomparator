package com.github.dann41.jsoncomparator.result;

import java.util.Map;

public interface ComparisonResult {

  Map<String, Difference<Object>> getUpdated();

  Map<String, Difference<Object>> getRemoved();

  Map<String, Difference<Object>> getAdded();

  Map<String, Difference<Object>> getAllDiffs();

  default boolean isEmpty() {
    return getAllDiffs().isEmpty();
  }
}
