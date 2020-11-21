package com.github.dann41.jsoncomparator;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.dann41.jsoncomparator.config.ComparatorConfig;
import com.github.dann41.jsoncomparator.result.ComparisonResult;
import com.github.dann41.jsoncomparator.result.SimpleComparisonResult;

import java.io.IOException;
import java.util.*;

import static com.github.dann41.jsoncomparator.JsonNodeExtensions.*;

public class RecursiveJsonComparator implements JsonComparator {

    private final ComparatorConfig comparatorConfig;

    public RecursiveJsonComparator() {
        this(ComparatorConfig.aComparatorConfig().build());
    }

    public RecursiveJsonComparator(ComparatorConfig comparatorConfig) {
        this.comparatorConfig = comparatorConfig;
    }

    @Override
    public ComparisonResult compare(JsonNode oldNode, JsonNode newNode) {
        SimpleComparisonResult comparisonResult = new SimpleComparisonResult();
        calculateDifference(comparisonResult, 0, "", oldNode, newNode, comparatorConfig);
        return comparisonResult;
    }

    @Override
    public ComparisonResult compare(String oldObject, String newObject) {
        try {
            JsonNode oldNode = new ObjectMapper().readTree(oldObject);
            JsonNode newNode = new ObjectMapper().readTree(newObject);
            return compare(oldNode, newNode);
        } catch (IOException e) {
            return emptyComparisonResult();
        }
    }

    private void calculateDifference(
            SimpleComparisonResult simpleDifference,
            int level,
            String nodeName,
            JsonNode oldNode,
            JsonNode newNode,
            ComparatorConfig config
    ) {
        // Nothing
        if (areBothNodesNull(oldNode, newNode)) {
            return;
        }

        // Added
        if (oldNode == null) {
            simpleDifference.fieldAdded(nodeName, newNode);
            return;
        }

        // Removed
        if (newNode == null) {
            simpleDifference.fieldRemoved(nodeName, oldNode);
            return;
        }

        // Same
        if (Objects.equals(oldNode, newNode)) {
            return;
        }

        // Simple type with different value
        if (isSimpleType(oldNode) || isSimpleType(newNode)) {
            simpleDifference.fieldUpdated(nodeName, oldNode, newNode);
            return;
        }

        if (!config.isRecursive() && level == 1) {
            simpleDifference.fieldUpdated(nodeName, oldNode, newNode);
            return;
        }

        // Different complex type
        if (!oldNode.getNodeType().equals(newNode.getNodeType())) {
            simpleDifference.fieldUpdated(nodeName, oldNode, newNode);
            return;
        }

        // Both complex and same types
        String prefix = getPrefix(nodeName, config);
        ComparisonType comparisonType = getComparisonType(oldNode, config);
        calculateDiffByComparisonType(simpleDifference, level, nodeName, oldNode, newNode, config, prefix, comparisonType);
    }

    private boolean areBothNodesNull(JsonNode firstNode, JsonNode secondNode) {
        return firstNode == null && secondNode == null;
    }

    private String getPrefix(String nodeName, ComparatorConfig config) {
        if (!config.isConcatenateFields())
            return "";

        return nodeName.isEmpty()
                ? nodeName
                : nodeName + config.getFieldSeparator();
    }

    private ComparisonType getComparisonType(JsonNode oldNode, ComparatorConfig config) {
        if (oldNode.isObject())
            return ComparisonType.OBJECT;
        if (oldNode.isArray() && config.isArrayIncluded())
            return ComparisonType.ARRAY;
        return ComparisonType.SIMPLE;
    }

    private void calculateDiffByComparisonType(SimpleComparisonResult simpleDifference, int level, String nodeName, JsonNode oldNode, JsonNode newNode, ComparatorConfig config, String prefix, ComparisonType comparisonType) {
        switch (comparisonType) {
            case OBJECT:
                calculateObjectDiff(simpleDifference, level, oldNode, newNode, config, prefix);
                break;
            case ARRAY:
                calculateArrayDiff(simpleDifference, level, oldNode, newNode, config, prefix);
                break;
            case SIMPLE:
            default:
                simpleDifference.fieldUpdated(nodeName, oldNode, newNode);
        }
    }

    private void calculateObjectDiff(SimpleComparisonResult currentDifferences, int level, JsonNode oldNode, JsonNode newNode, ComparatorConfig config, String prefix) {
        Set<String> fields = new HashSet<>();
        oldNode.fieldNames().forEachRemaining(fields::add);
        newNode.fieldNames().forEachRemaining(fields::add);

        fields.forEach(
                field -> calculateDifference(currentDifferences, level + 1, prefix + field, oldNode.get(field), newNode.get(field), config)
        );
    }

    private void calculateArrayDiff(SimpleComparisonResult currentDifferences, int level, JsonNode oldNode, JsonNode newNode, ComparatorConfig config, String prefix) {
        Map<Integer, JsonNode> indexedOldArrayNode = toIndexedMap(oldNode);
        Map<Integer, JsonNode> indexedNewArrayNode = toIndexedMap(newNode);

        Set<Integer> indexes = new HashSet<>();
        indexes.addAll(indexedOldArrayNode.keySet());
        indexes.addAll(indexedNewArrayNode.keySet());

        for (Integer index : indexes) {
            calculateDifference(currentDifferences, level + 1, prefix + index, oldNode.get(index), newNode.get(index), config);
        }
    }

    private ComparisonResult emptyComparisonResult() {
        return new SimpleComparisonResult();
    }

    enum ComparisonType {
        OBJECT,
        ARRAY,
        SIMPLE
    }

}
