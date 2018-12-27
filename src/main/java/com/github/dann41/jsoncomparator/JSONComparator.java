package com.github.dann41.jsoncomparator;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class JSONComparator {

  public ComparisonResult compare(JsonNode oldNode, JsonNode newNode) {
    return compare(oldNode, newNode, ComparatorConfig.aComparatorConfig().build());
  }

  public ComparisonResult compare(JsonNode oldNode, JsonNode newNode, ComparatorConfig config) {
    SimpleComparisonResult comparisonResult = new SimpleComparisonResult();
    calculateDifference(comparisonResult, 0, "", oldNode, newNode, config);
    return comparisonResult;
  }

  public ComparisonResult compare(String oldObject, String newObject) {
    return compare(oldObject, newObject, ComparatorConfig.aComparatorConfig().build());
  }

  public ComparisonResult compare(String oldObject, String newObject, ComparatorConfig config) {
    try {
      JsonNode oldNode = new ObjectMapper().readTree(oldObject);
      JsonNode newNode = new ObjectMapper().readTree(newObject);
      return compare(oldNode, newNode, config);
    } catch (IOException e) {
      return emptyComparisonResult();
    }
  }

  private void calculateDifference(SimpleComparisonResult simpleDifference,
                                   int level,
                                   String nodeName,
                                   JsonNode oldNode,
                                   JsonNode newNode,
                                   ComparatorConfig config) {
    // Nothing
    if (oldNode == null && newNode == null) {
      return;
    }

    // Added
    if (oldNode == null) {
      simpleDifference.added.put(nodeName, Difference.of(null, getNodeValue(newNode)));
      return;
    }

    // Removed
    if (newNode == null) {
      simpleDifference.removed.put(nodeName, Difference.of(getNodeValue(oldNode), null));
      return;
    }

    // Same
    if (oldNode.equals(newNode)) {
      return;
    }

    // Simple type with different value
    if (isSimpleType(oldNode) || isSimpleType(newNode)) {
      simpleDifference.updated.put(nodeName, Difference.of(getNodeValue(oldNode), getNodeValue(newNode)));
      return;
    }

    if (!config.isRecursive() && level == 1) {
      simpleDifference.updated.put(nodeName, Difference.of(getNodeValue(oldNode), getNodeValue(newNode)));
      return;
    }

    // Different complex type
    if (!oldNode.getNodeType().equals(newNode.getNodeType())) {
      simpleDifference.updated.put(nodeName, Difference.of(getNodeValue(oldNode), getNodeValue(newNode)));
      return;
    }

    // Both complex and same types

    String prefix = config.isConcatenateFields()
        ? nodeName.isEmpty()
            ? nodeName
            : nodeName + config.getFieldSeparator()
        : "";

    if (oldNode.isObject()) {
      Set<String> fields = new HashSet<>();
      oldNode.fieldNames().forEachRemaining(fields::add);
      newNode.fieldNames().forEachRemaining(fields::add);

      for (String field : fields) {
        calculateDifference(simpleDifference, level + 1, prefix + field, oldNode.get(field), newNode.get(field), config);
      }
    } else if (oldNode.isArray() && config.isArrayIncluded()) {
      Map<Integer, JsonNode> indexedOldArrayNode = getIndexedArrayNode(oldNode);
      Map<Integer, JsonNode> indexedNewArrayNode = getIndexedArrayNode(newNode);

      Set<Integer> indexes = new HashSet<>();
      indexes.addAll(indexedOldArrayNode.keySet());
      indexes.addAll(indexedNewArrayNode.keySet());

      for (Integer index : indexes) {
        calculateDifference(simpleDifference, level + 1, prefix + index, oldNode.get(index), newNode.get(index), config);
      }
    } else {
      simpleDifference.updated.put(nodeName, Difference.of(getNodeValue(oldNode), getNodeValue(newNode)));
    }
  }

  private Map<Integer, JsonNode> getIndexedArrayNode(JsonNode node) {
    Map<Integer, JsonNode> indexedMap = new HashMap<>();
    for (int i = 0; i < node.size(); i++) {
      indexedMap.put(i, node.get(i));
    }
    return indexedMap;
  }

  private boolean isSimpleType(JsonNode node) {
    return !node.isContainerNode();
  }

  private Object getNodeValue(JsonNode node) {
    if (node.isNumber())
      return node.numberValue();
    if (node.isTextual())
      return node.textValue();
    if (node.isBoolean())
      return node.booleanValue();
    return node;
  }

  private ComparisonResult emptyComparisonResult() {
    return new SimpleComparisonResult();
  }

  static class SimpleComparisonResult implements ComparisonResult {

    private Map<String, Difference<Object>> updated = new HashMap<>();
    private Map<String, Difference<Object>> removed = new HashMap<>();
    private Map<String, Difference<Object>> added = new HashMap<>();

    @Override
    public Map<String, Difference<Object>> getUpdated() {
      return updated;
    }

    @Override
    public Map<String, Difference<Object>> getRemoved() {
      return removed;
    }

    @Override
    public Map<String, Difference<Object>> getAdded() {
      return added;
    }

    @Override
    public Map<String, Difference<Object>> getAllDiffs() {
      Map<String, Difference<Object>> diff = new HashMap<>();
      diff.putAll(added);
      diff.putAll(updated);
      diff.putAll(removed);
      return diff;
    }

  }

}
