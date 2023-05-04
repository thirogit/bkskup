package com.bk.bkskup3.dao.q;

import com.mysema.query.types.path.NumberPath;
import com.mysema.query.types.path.StringPath;

import static com.mysema.query.types.PathMetadataFactory.forProperty;

public class QHent {

    private QHent() {
    }

    public static final NumberPath<Integer> id = new NumberPath<Integer>(Integer.class, forProperty(null,"HENTID"));
    public static final StringPath hentNo = new StringPath(forProperty(null,"HENTNO"));
    public static final StringPath hentName = new StringPath(forProperty(null,"HENTNAME"));

}
