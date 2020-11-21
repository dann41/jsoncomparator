package com.github.dann41.jsoncomparator;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class JsonNodeExtensions {

    /**
     * Converts a JsonNode of tyoe array into an indexed map.
     * @param node
     * @return indexed map or empty map in case node is not an array
     */
    public static Map<Integer, JsonNode> toIndexedMap(JsonNode node) {
        if (!node.isArray())
            return Collections.emptyMap();

        Map<Integer, JsonNode> indexedMap = new HashMap<>();
        for (int i = 0; i < node.size(); i++) {
            indexedMap.put(i, node.get(i));
        }
        return indexedMap;
    }

    /**
     * Unwraps the value of a node depending on its type
     * @param node
     * @return value contained by JsonNode or the node itself it it's a complex type
     */
    public static Object getNodeValue(JsonNode node) {
        if (node.isNumber())
            return node.numberValue();
        if (node.isTextual())
            return node.textValue();
        if (node.isBoolean())
            return node.booleanValue();
        return node;
    }


    public static boolean isSimpleType(JsonNode node) {
        return !node.isContainerNode();
    }

}
