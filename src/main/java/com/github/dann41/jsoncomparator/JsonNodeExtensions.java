package com.github.dann41.jsoncomparator;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.HashMap;
import java.util.Map;

class JsonNodeExtensions {

    static Map<Integer, JsonNode> toIndexedMap(JsonNode node) {
        Map<Integer, JsonNode> indexedMap = new HashMap<>();
        for (int i = 0; i < node.size(); i++) {
            indexedMap.put(i, node.get(i));
        }
        return indexedMap;
    }

    static Object getNodeValue(JsonNode node) {
        if (node.isNumber())
            return node.numberValue();
        if (node.isTextual())
            return node.textValue();
        if (node.isBoolean())
            return node.booleanValue();
        return node;
    }


    static boolean isSimpleType(JsonNode node) {
        return !node.isContainerNode();
    }

}
