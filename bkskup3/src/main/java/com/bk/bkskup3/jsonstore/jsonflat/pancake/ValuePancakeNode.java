package com.bk.bkskup3.jsonstore.jsonflat.pancake;

import com.bk.bkskup3.jsonstore.jsonflat.JsonPath;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.base.Preconditions;

/**
 * Created with IntelliJ IDEA.
 * User: SG0891787
 * Date: 1/23/2015
 * Time: 11:52 AM
 */
public class ValuePancakeNode extends PancakeTreeNode {

    private PancakeValue fieldValue;

    public ValuePancakeNode(PancakeValue fieldValue) {
        super(PancakeTreeNodeType.VALUE);
        this.fieldValue = fieldValue;
    }

    @Override
    public void append(JsonPath path, PancakeValue value) {
        Preconditions.checkState(false,"value node is last on path, therefore cannot have child nodes");
    }

    @Override
    public JsonNode build(PancakeNodeFactory factory) {
        return factory.createNode(fieldValue);
    }
}
