package com.mysema.serializer;

import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: SG0891787
 * Date: 11/15/2014
 * Time: 5:17 PM
 */
public class SQLiteQuerySerializer implements QuerySerializer {

    private StringBuilder where = new StringBuilder();
    private List<String> orderBys = new ArrayList<String>();
    private long limit = -1;

    @Override
    public void addWhere(String where) {
        if(this.where.length() > 0)
            this.where.append(" and (").append(where).append(')');
        else
            this.where.append('(').append(where).append(')');
    }

    @Override
    public void addOrderBy(String orderBy) {
        orderBys.add(orderBy);
    }

    @Override
    public void setLimit(long limit) {
        this.limit = limit;
    }

    public String getWhere() {
        return where.toString();
    }

    public List<String> getOrderBy() {
        return ImmutableList.copyOf(orderBys);
    }

    public long getLimit()
    {
        return limit;
    }
}
