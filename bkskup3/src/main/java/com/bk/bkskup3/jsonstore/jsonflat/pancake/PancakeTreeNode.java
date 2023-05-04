package com.bk.bkskup3.jsonstore.jsonflat.pancake;


import com.bk.bkskup3.jsonstore.jsonflat.JsonPath;
import com.fasterxml.jackson.databind.JsonNode;

public abstract class PancakeTreeNode {

    public PancakeTreeNodeType nodeType;

    public PancakeTreeNode(PancakeTreeNodeType nodeType) {
        this.nodeType = nodeType;
    }

    public PancakeTreeNodeType getNodeType() {
        return nodeType;
    }
    public abstract void append(JsonPath path, PancakeValue value);

    public abstract JsonNode build(PancakeNodeFactory factory);
}
