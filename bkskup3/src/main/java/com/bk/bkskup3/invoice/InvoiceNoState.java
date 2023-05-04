package com.bk.bkskup3.invoice;

/**
 * Created by sg0891787 on 9/1/2017.
 */

public enum InvoiceNoState {

    ACQUIRED("A"),
    USED("U"),
    RESET("R");

    private String id;

    InvoiceNoState(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }


    public static InvoiceNoState fromString(String idStr)
    {
        for(InvoiceNoState state : values())
        {
            if(state.getId().equals(idStr))
            {
                return state;
            }
        }
        return null;
    }
}
