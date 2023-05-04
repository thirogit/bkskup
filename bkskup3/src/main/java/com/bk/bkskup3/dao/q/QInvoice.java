package com.bk.bkskup3.dao.q;

import com.mysema.query.types.path.NumberPath;

import static com.mysema.query.types.PathMetadataFactory.forProperty;

public class QInvoice {

    private QInvoice() {
    }

    public static final NumberPath<Integer> id = new NumberPath<Integer>(Integer.class, forProperty(null,"INVOICEID"));
    public static final NumberPath<Integer> purchase = new NumberPath<Integer>(Integer.class, forProperty(null,"PURACHE"));

}
