package com.mysema.serializer;

/**
 * Created with IntelliJ IDEA.
 * User: SG0891787
 * Date: 11/15/2014
 * Time: 5:15 PM
 */
public interface QuerySerializer {

    void addWhere(String where);
    void addOrderBy(String orderBy);
    void setLimit(long limit);
}
