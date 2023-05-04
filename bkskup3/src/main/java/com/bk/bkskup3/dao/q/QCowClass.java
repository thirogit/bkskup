package com.bk.bkskup3.dao.q;

import com.mysema.query.types.path.NumberPath;
import com.mysema.query.types.path.StringPath;

import static com.mysema.query.types.PathMetadataFactory.forProperty;

/**
 * Created with IntelliJ IDEA.
 * User: SG0891787
 * Date: 12/7/2014
 * Time: 10:22 PM
 */
public class QCowClass {
    public static final NumberPath<Integer> id = new NumberPath<Integer>(Integer.class, forProperty(null,"CLASSID"));
    public static final StringPath classCode = new StringPath(forProperty(null,"CODE"));
}
