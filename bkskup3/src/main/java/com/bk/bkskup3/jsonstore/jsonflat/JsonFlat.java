package com.bk.bkskup3.jsonstore.jsonflat;

import com.bk.bkskup3.jsonstore.jsonflat.pancake.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.NullNode;
import com.google.common.base.Preconditions;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: SG0891787
 * Date: 1/18/2015
 * Time: 11:49 PM
 */
public class JsonFlat {


    private PancakeNodeFactory mFactory = new PancakeNodeFactory();

    public JsonNode inflate(Collection<JsonPancake> pancakes) {

        if (pancakes.isEmpty())
            return NullNode.getInstance();


        Iterator<JsonPancake> pancakeIt = pancakes.iterator();
        JsonPancake firstPancake = pancakeIt.next();
        JsonPath firstPancakePath = JsonPath.parse(firstPancake.getPath());


        JsonPathElement rootElement = firstPancakePath.front();
        Preconditions.checkArgument(rootElement.getName().isEmpty(), "root element should be nameless");

        PancakeTreeNode rootNode;
        if (rootElement.isArray()) {
            rootNode = new ArrayPancakeNode();
        } else {
            rootNode = new ObjectPancakeNode();
        }

        rootNode.append(firstPancakePath.popFront(), firstPancake.getValue());

        while (pancakeIt.hasNext()) {
            JsonPancake nextPancake = pancakeIt.next();
            rootNode.append(JsonPath.parse(nextPancake.getPath()).popFront(), nextPancake.getValue());
        }
        return rootNode.build(mFactory);
    }

    public void flatten(JsonNode root, PancakeStorage storage) {
        JsonPath path;
        if (root.isArray()) {
            path = JsonPath.rootArray();
        } else {
            path = JsonPath.root();
        }
        traverse(root, path, storage);
    }

    private void traverse(JsonNode node, JsonPath path, PancakeStorage storage) {
        Iterator<Map.Entry<String, JsonNode>> it = node.fields();

        while (it.hasNext()) {
            Map.Entry<String, JsonNode> entry = it.next();
            JsonNode n = entry.getValue();

            if (n.isObject()) {
                traverse(n, path.next(entry.getKey()), storage);
            } else if (n.isArray()) {
                traverseArray(n, path.nextArray(entry.getKey()), storage);
            } else {
                storage.store(new JsonPancake(path.next(entry.getKey()).toString(), mFactory.createValue(n)));
            }
        }
    }

    private void traverseArray(JsonNode node, JsonPath path, PancakeStorage storage) {
        ArrayNode arrayNode = (ArrayNode) node;

        int index = 0;
        Iterator<JsonNode> elementIt = arrayNode.elements();
        while (elementIt.hasNext()) {
            JsonNode elementNode = elementIt.next();
            traverse(elementNode, path.next(String.valueOf(index)), storage);
            index++;
        }
    }


}
