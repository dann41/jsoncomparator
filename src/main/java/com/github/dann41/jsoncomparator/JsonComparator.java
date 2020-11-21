package com.github.dann41.jsoncomparator;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.dann41.jsoncomparator.result.ComparisonResult;

public interface JsonComparator {

    ComparisonResult compare(JsonNode oldNode, JsonNode newNode);

    ComparisonResult compare(String oldObject, String newObject);
}
