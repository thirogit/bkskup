package com.bk.bkskup3.dao.q;

import com.mysema.query.types.path.NumberPath;
import com.mysema.query.types.path.StringPath;

import static com.mysema.query.types.PathMetadataFactory.forProperty;

public class QCow {

    private QCow() {
    }

    public static final NumberPath<Integer> id = new NumberPath<Integer>(Integer.class, forProperty(null,"COWID"));
    public static final StringPath cowNo = new StringPath(forProperty(null,"EAN"));
    public static final NumberPath<Integer> invoiceId = new NumberPath<Integer>(Integer.class, forProperty(null,"INVOICE"));

}
