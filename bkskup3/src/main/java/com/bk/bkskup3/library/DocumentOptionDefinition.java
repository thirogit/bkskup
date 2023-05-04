package com.bk.bkskup3.library;

import java.io.Serializable;

/**
 * Created by SG0891787 on 2/14/2017.
 */

public class DocumentOptionDefinition implements Serializable{
    private String title;
    private DocumentOptionType type;
    private String code;
    private String defaultValue;

    public DocumentOptionDefinition( String code,DocumentOptionType type) {
        this.type = type;
        this.code = code;
    }

    public DocumentOptionDefinition() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public DocumentOptionType getType() {
        return type;
    }

    public void setType(DocumentOptionType type) {
        this.type = type;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }
}
