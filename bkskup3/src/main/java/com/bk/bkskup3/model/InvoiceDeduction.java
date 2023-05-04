package com.bk.bkskup3.model;

/**
 * Created with IntelliJ IDEA.
 * User: SG0891787
 * Date: 12/22/2014
 * Time: 11:51 PM
 */
public interface InvoiceDeduction {
    public String getCode();

    public String getReason();

    public Double getFraction();

    public boolean isEnabled();

}
