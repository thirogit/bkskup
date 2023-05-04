package com.bk.bkskup3.jsonstore;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Created with IntelliJ IDEA.
 * User: SG0891787
 * Date: 1/16/2015
 * Time: 11:53 PM
 */
public class JsonObj {

    private int oid;
    private JsonNode node;

    public JsonObj() {
    }

    public JsonObj(int oid) {
        this(oid,null);
    }

    public JsonObj(int oid, JsonNode node) {
        this.oid = oid;
        this.node = node;
    }

    public int getOid() {
        return oid;
    }

    public JsonNode getNode() {
        return node;
    }

    public void setNode(JsonNode node) {
        this.node = node;
    }
}
