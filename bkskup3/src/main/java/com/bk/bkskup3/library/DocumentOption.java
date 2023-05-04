package com.bk.bkskup3.library;

import java.io.Serializable;

public class DocumentOption implements Serializable {

    private String optionName;
    private String optionValue;

    public DocumentOption(String optionName) {
        this.optionName = optionName;
    }

    public DocumentOption(String optionName, String optionValue) {
        this.optionName = optionName;
        this.optionValue = optionValue;
    }

    public String getOptionName() {
        return optionName;
    }

    public String getOptionValue() {
        return optionValue;
    }

    public void setOptionValue(String optionValue) {
        this.optionValue = optionValue;
    }
}
