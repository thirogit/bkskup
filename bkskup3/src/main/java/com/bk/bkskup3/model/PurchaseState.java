package com.bk.bkskup3.model;

/**
 * Created with IntelliJ IDEA.
 * User: SG0891787
 * Date: 11/11/2014
 * Time: 11:52 AM
 */
public enum PurchaseState {
    OPEN,
    CLOSED,
    SENT;

    public static PurchaseState fromString(String str)
    {
        for(PurchaseState state : values())
        {
            if(state.name().equals(str))
                return state;
        }
        return null;
    }
}
