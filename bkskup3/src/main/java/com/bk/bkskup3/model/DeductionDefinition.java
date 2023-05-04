package com.bk.bkskup3.model;

/**
 * Created with IntelliJ IDEA.
 * User: SG0891787
 * Date: 12/3/2014
 * Time: 11:12 PM
 */
public interface DeductionDefinition extends Idable{

    String getCode();

    String getReason();

    Double getFraction();

    boolean isEnabledByDefault();
}
