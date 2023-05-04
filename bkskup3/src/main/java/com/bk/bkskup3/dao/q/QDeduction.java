package com.bk.bkskup3.dao.q;

import com.mysema.query.types.path.NumberPath;
import com.mysema.query.types.path.StringPath;

import static com.mysema.query.types.PathMetadataFactory.forProperty;

/**
 * Created with IntelliJ IDEA.
 * User: SG0891787
 * Date: 12/15/2014
 * Time: 11:06 AM
 */
public class QDeduction {
    private QDeduction(){}

    public static final NumberPath<Integer> id = new NumberPath<Integer>(Integer.class, forProperty(null,"DEDUCTION_ID"));
    public static final StringPath code = new StringPath(forProperty(null,"CODE"));

}
