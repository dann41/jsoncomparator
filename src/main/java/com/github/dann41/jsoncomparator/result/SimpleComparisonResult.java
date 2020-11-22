package com.github.dann41.jsoncomparator.result;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.HashMap;
import java.util.Map;

import static com.github.dann41.jsoncomparator.JsonNodeExtensions.getNodeValue;

public class SimpleComparisonResult implements ComparisonResult {

    private Map<String, Difference<Object>> updated = new HashMap<>();
    private Map<String, Difference<Object>> removed = new HashMap<>();
    private Map<String, Difference<Object>> added = new HashMap<>();

    public void fieldAdded(String nodeName, JsonNode newNode) {
        added.put(nodeName, Difference.of(null, getNodeValue(newNode)));
    }

    public void fieldRemoved(String nodeName, JsonNode oldNode) {
        removed.put(nodeName, Difference.of(getNodeValue(oldNode), null));
    }

    public void fieldUpdated(String nodeName, JsonNode oldNode, JsonNode newNode) {
        updated.put(nodeName, Difference.of(getNodeValue(oldNode), getNodeValue(newNode)));
    }

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
