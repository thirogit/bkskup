package com.bk.bkskup3.jsonstore.jsonflat.pancake;

import com.bk.bkskup3.jsonstore.jsonflat.JsonPath;
import com.bk.bkskup3.jsonstore.jsonflat.JsonPathElement;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: SG0891787
 * Date: 1/23/2015
 * Time: 11:52 AM
 */
public class ObjectPancakeNode extends PancakeTreeNode {

    private Map<String,PancakeTreeNode> fields = new HashMap<String, PancakeTreeNode>();

    public ObjectPancakeNode() {
        super(PancakeTreeNodeType.OBJECT);
    }

    public void append(JsonPath path, PancakeValue value) {

        JsonPathElement fieldElement = path.front();
        String filedName = fieldElement.getName();

        PancakeTreeNode pancakeTreeNode = fields.get(filedName);
        JsonPath remainingPath = path.popFront();
        if(pancakeTreeNode == null) {

            if(fieldElement.isArray()) {
                pancakeTreeNode = new ArrayPancakeNode();
                pancakeTreeNode.append(remainingPath, value);
            }
            else
            {
                if(remainingPath.isEmpty()){
                    pancakeTreeNode = new ValuePancakeNode(value);
                } else
                {
                    pancakeTreeNode = new ObjectPancakeNode();
                    pancakeTreeNode.append(remainingPath, value);
                }
            }

            fields.put(filedName,pancakeTreeNode);
        }
        else {
            pancakeTreeNode.append(remainingPath, value);
        }

    }

    @Override
    public JsonNode build(PancakeNodeFactory factory) {

        ObjectNode node = new ObjectNode(new JsonNodeFactory(false));
        for(Map.Entry<String,PancakeTreeNode> entry : fields.entrySet())
        {
            node.replace(entry.getKey(),entry.getValue().build(factory));
        }

        return node;
    }

}
