package com.mysema.query.support;

import com.bk.bkskup3.utils.NullUtils;
import com.mysema.query.DefaultQueryMetadata;
import com.mysema.query.QueryMetadata;
import com.mysema.query.QueryModifiers;
import com.mysema.query.types.Order;
import com.mysema.query.types.OrderSpecifier;
import com.mysema.query.types.Predicate;
import com.mysema.serializer.QSerializer;
import com.mysema.serializer.QuerySerializer;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: SG0891787
 * Date: 11/15/2014
 * Time: 12:09 AM
 */
public class Query extends QueryBase<Query> {
    public Query() {
        super(new QueryMixin<Query>(new DefaultQueryMetadata(), false));
        this.queryMixin.setSelf(this);
    }

    public void serialize(QuerySerializer serializer)
    {
        QSerializer whereSerializer = new QSerializer();
        QueryMetadata metadata = queryMixin.getMetadata();
        Predicate where = metadata.getWhere();
        if(where != null) {
            where.accept(whereSerializer, null);
            serializer.addWhere(whereSerializer.toString());
        }

        List<OrderSpecifier<?>> orderBy = metadata.getOrderBy();
        for(OrderSpecifier<?> orderSpecifier : orderBy)
        {
            QSerializer orderSerializer = new QSerializer();
            String order = orderSpecifier.getOrder() == Order.ASC ? "asc" : "desc";
            orderSpecifier.getTarget().accept(orderSerializer,null);
            serializer.addOrderBy(orderSerializer.toString() + " " + order);
        }

        QueryModifiers modifiers = metadata.getModifiers();
        serializer.setLimit(NullUtils.valueForNull(modifiers.getLimit(),-1L));
    }
}
