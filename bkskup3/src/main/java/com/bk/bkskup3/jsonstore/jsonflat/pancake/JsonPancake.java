package com.bk.bkskup3.jsonstore.jsonflat.pancake;

/**
* Created with IntelliJ IDEA.
* User: SG0891787
* Date: 1/23/2015
* Time: 2:03 PM
*/
public class JsonPancake {

    public JsonPancake(String path, PancakeValue value) {
        this.path = path;
        this.value = value;
    }

    private String path;
    private PancakeValue value;

    public String getPath() {
        return path;
    }

    public PancakeValue getValue() {
        return value;
    }

    public void setValue(PancakeValue value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return path + " = " + value;
    }
}
