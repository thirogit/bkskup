package com.bk.bkskup3.jsonstore.jsonflat.pancake;

import com.bk.bkskup3.jsonstore.jsonflat.JsonPath;
import com.bk.bkskup3.jsonstore.jsonflat.JsonPathElement;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.google.common.collect.Lists;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: SG0891787
 * Date: 1/23/2015
 * Time: 11:52 AM
 */
public class ArrayPancakeNode extends PancakeTreeNode {

    private Map<Integer,PancakeTreeNode> elements = new HashMap<Integer, PancakeTreeNode>();

    public ArrayPancakeNode() {
        super(PancakeTreeNodeType.ARRAY);
    }

    public List<PancakeTreeNode> toList()
    {
        LinkedList<PancakeTreeNode> result = Lists.newLinkedList();
        LinkedList<Integer> indexes = Lists.newLinkedList(elements.keySet());
        Collections.sort(indexes);

        for(Integer index : indexes)
        {
            result.add(elements.get(index));
        }
        return result;
    }

    @Override
    public void append(JsonPath path, PancakeValue value) {
        JsonPathElement indexElement = path.front();
        int index = Integer.parseInt(indexElement.getName());

        PancakeTreeNode pancakeTreeNode = elements.get(index);
        JsonPath remainingPath = path.popFront();
        if(pancakeTreeNode != null) {
            pancakeTreeNode.append(remainingPath,value);
        } else {


            if(remainingPath.isEmpty())
            {
                ValuePancakeNode node = new ValuePancakeNode(value);
                elements.put(index,node);
            } else
            {
                if(indexElement.isArray())
                {
                    ArrayPancakeNode node = new ArrayPancakeNode();
                    node.append(remainingPath,value);
                    elements.put(index,node);
                }
                else
                {
                    ObjectPancakeNode node = new ObjectPancakeNode();
                    node.append(remainingPath,value);
                    elements.put(index,node);
                }
            }
        }


    }

    @Override
    public JsonNode build(PancakeNodeFactory factory) {

        ArrayNode node = new ArrayNode(new JsonNodeFactory(false));

        for(PancakeTreeNode element : toList()) {
            node.add(element.build(factory));
        }

        return node;
    }
}
