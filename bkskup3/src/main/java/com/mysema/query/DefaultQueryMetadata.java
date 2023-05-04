/*
 * Copyright 2011, Mysema Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.mysema.query;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.mysema.query.types.Expression;
import com.mysema.query.types.ExpressionUtils;
import com.mysema.query.types.OrderSpecifier;
import com.mysema.query.types.Predicate;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.mysema.util.CollectionUtils.*;

/**
 * DefaultQueryMetadata is the default implementation of the {@link QueryMetadata} interface
 *
 * @author tiwe
 */
public class DefaultQueryMetadata implements QueryMetadata, Cloneable {

    private static final long serialVersionUID = 317736313966701232L;

    private boolean distinct;

    private Expression<?> source;

    private List<Expression<?>> groupBy = ImmutableList.of();

    @Nullable
    private Predicate having;

    @Nullable
    private QueryModifiers modifiers = QueryModifiers.EMPTY;

    private List<OrderSpecifier<?>> orderBy = ImmutableList.of();

    private List<Expression<?>> projection = ImmutableList.of();

    // NOTE : this is not necessarily serializable
//    private Map<ParamExpression<?>,Object> params = ImmutableMap.<ParamExpression<?>, Object>of();

    private boolean unique;

    @Nullable
    private Predicate where;

//    private Set<QueryFlag> flags = ImmutableSet.of();

    private boolean extractParams = true;

    private boolean validate = true;

//    private ValidatingVisitor validatingVisitor = ValidatingVisitor.DEFAULT;

    private static Predicate and(Predicate lhs, Predicate rhs) {
        if (lhs == null) {
            return rhs;
        } else {
            return ExpressionUtils.and(lhs, rhs);
        }
    }

    /**
     * Create an empty DefaultQueryMetadata instance
     */
    public DefaultQueryMetadata() {}

    /**
     * Disable validation
     *
     * @return
     */
    public DefaultQueryMetadata noValidate() {
        validate = false;
        return this;
    }


//    @Override
//    public void addFlag(QueryFlag flag) {
//        flags = addSorted(flags, flag);
//    }


    @Override
    public void addGroupBy(Expression<?> o) {
        validate(o);
        groupBy = add(groupBy, o);
    }

    @Override
    public void addHaving(Predicate e) {
        if (e == null) {
            return;
        }
        e = (Predicate) ExpressionUtils.extract(e);
        if (e != null) {
            validate(e);
            having = and(having, e);
        }
    }


    @Override
    public void addOrderBy(OrderSpecifier<?> o) {
        // order specifiers can't be validated, since they can refer to projection elements
        // that are declared later
        orderBy = add(orderBy, o);
    }

    @Override
    public void addProjection(Expression<?> o) {
        validate(o);
        projection = add(projection, o);
    }

    @Override
    public void addWhere(Predicate e) {
        if (e == null) {
            return;
        }
        e = (Predicate) ExpressionUtils.extract(e);
        if (e != null) {
            validate(e);
            where = and(where, e);
        }
    }

    @Override
    public void clearOrderBy() {
        orderBy = ImmutableList.of();
    }

    @Override
    public void clearProjection() {
        projection = ImmutableList.of();
    }

    @Override
    public void clearWhere() {
        where = new BooleanBuilder();
    }

    @Override
    public QueryMetadata clone() {
//        try {
            DefaultQueryMetadata clone = new DefaultQueryMetadata();//(DefaultQueryMetadata); super.clone();
            clone.groupBy = copyOf(groupBy);
            clone.having = having;
            clone.modifiers = modifiers;
            clone.orderBy = copyOf(orderBy);
            clone.projection = copyOf(projection);
//            clone.params = copyOf(params);
            clone.where = where;
//            clone.flags = copyOfSorted(flags);
            return clone;
//        } catch (CloneNotSupportedException e) {
//            throw new QueryException(e);
//        }
    }

    @Override
    public List<Expression<?>> getGroupBy() {
        return groupBy;
    }

    @Override
    public Predicate getHaving() {
        return having;
    }

    @Override
    public Expression<?> getSource() {
        return source;
    }


    @Override
    @Nullable
    public QueryModifiers getModifiers() {
        return modifiers;
    }

//    @Override
//    public Map<ParamExpression<?>,Object> getParams() {
//        return params;
//    }

    @Override
    public List<OrderSpecifier<?>> getOrderBy() {
        return orderBy;
    }

    @Override
    public List<Expression<?>> getProjection() {
        return projection;
    }

    @Override
    public Predicate getWhere() {
        return where;
    }

    @Override
    public boolean isDistinct() {
        return distinct;
    }

    @Override
    public boolean isUnique() {
        return unique;
    }

    @Override
    public void reset() {
        clearProjection();
//        params = ImmutableMap.of();
        modifiers = QueryModifiers.EMPTY;
    }

    @Override
    public void setDistinct(boolean distinct) {
        this.distinct = distinct;
    }

    @Override
    public void setLimit(Long limit) {
        if (modifiers == null || modifiers.getOffset() == null) {
            modifiers = QueryModifiers.limit(limit);
        } else {
            modifiers = new QueryModifiers(limit, modifiers.getOffset());
        }
    }

    @Override
    public void setModifiers(@Nullable QueryModifiers restriction) {
        this.modifiers = restriction;
    }

    @Override
    public void setOffset(Long offset) {
        if (modifiers == null || modifiers.getLimit() == null) {
            modifiers = QueryModifiers.offset(offset);
        } else {
            modifiers = new QueryModifiers(modifiers.getLimit(), offset);
        }
    }

    @Override
    public void setUnique(boolean unique) {
        this.unique = unique;
    }

//    @Override
//    public <T> void setParam(ParamExpression<T> param, T value) {
//        params = put(params, param, value);
//    }

//    @Override
//    public Set<QueryFlag> getFlags() {
//        return flags;
//    }
//
//    @Override
//    public boolean hasFlag(QueryFlag flag) {
//        return flags.contains(flag);
//    }
//
//    @Override
//    public void removeFlag(QueryFlag flag) {
//        flags = removeSorted(flags, flag);
//    }

    private void validate(Expression<?> expr) {
//        if (extractParams) {
//            expr.accept(ParamsVisitor.DEFAULT, this);
//        }
    }

    @Override
    public void setValidate(boolean v) {
        this.validate = v;
    }

    @Override
    public void setSource(Expression<?> arg) {
        this.source = arg;
    }

//    public void setValidatingVisitor(ValidatingVisitor visitor) {
//        this.validatingVisitor = visitor;
//    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof QueryMetadata) {
            QueryMetadata q = (QueryMetadata)o;
            return
//                    q.getFlags().equals(flags)
//                &&
                    q.getGroupBy().equals(groupBy)
                && Objects.equal(q.getHaving(), having)
                && q.isDistinct() == distinct
                && q.isUnique() == unique
                && Objects.equal(q.getModifiers(), modifiers)
                && q.getOrderBy().equals(orderBy)
//                && q.getParams().equals(params)
                && q.getProjection().equals(projection)
                && Objects.equal(q.getWhere(), where);

        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(/*flags,*/ groupBy, having, modifiers,
                orderBy, /*params,*/ projection, unique, where);
    }


}
