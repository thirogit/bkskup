package com.mysema.query.support;

import com.mysema.query.types.Predicate;

/**
 * Created with IntelliJ IDEA.
 * User: SG0891787
 * Date: 11/15/2014
 * Time: 10:51 PM
 */
public class QueryBuilder {
    private QueryBuilder() {
    }

    public static Query where(Predicate predicate)
    {
        return (new Query()).where(predicate);
    }
}
