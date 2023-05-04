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
package com.mysema.query.support;

import com.mysema.query.*;
import com.mysema.query.types.Expression;
//import com.mysema.query.types.FactoryExpressionUtils.FactoryExpressionAdapter;
import com.mysema.query.types.OrderSpecifier;
import com.mysema.query.types.Path;
import com.mysema.query.types.Predicate;

import javax.annotation.Nullable;

/**
 * Mixin style Query implementation
 *
 * @author tiwe
 *
 * @param <T> type of wrapped query
 */
public class QueryMixin<T> {

    private final QueryMetadata metadata;

    private final boolean expandAnyPaths;

//    private ReplaceVisitor replaceVisitor;

    private T self;

    public QueryMixin() {
        this.metadata = new DefaultQueryMetadata();
        this.expandAnyPaths = true;
    }

    public QueryMixin(QueryMetadata metadata) {
        this.metadata = metadata;
        this.expandAnyPaths = true;
    }

    public QueryMixin(QueryMetadata metadata, boolean expandAnyPaths) {
        this.metadata = metadata;
        this.expandAnyPaths = expandAnyPaths;
    }

    public QueryMixin(T self) {
        this(self, new DefaultQueryMetadata());
    }

    public QueryMixin(T self, QueryMetadata metadata) {
        this.self = self;
        this.metadata = metadata;
        this.expandAnyPaths = true;
    }

    public QueryMixin(T self, QueryMetadata metadata, boolean expandAnyPaths) {
        this.self = self;
        this.metadata = metadata;
        this.expandAnyPaths = expandAnyPaths;
    }


//    public T addFlag(QueryFlag queryFlag) {
//        metadata.addFlag(queryFlag);
//        return self;
//    }


//    public T removeFlag(QueryFlag queryFlag) {
//        metadata.removeFlag(queryFlag);
//        return self;
//    }

//    public <E> Expression<E> addProjection(Expression<E> e) {
//        e = convert(e, false);
//        metadata.addProjection(e);
//        return e;
//    }
//
//    public T addProjection(Expression<?>... o) {
//        for (Expression<?> e : o) {
//            metadata.addProjection(convert(e, false));
//        }
//        return self;
//    }

    private <P extends Path<?>> P assertRoot(P p) {
        if (!p.getRoot().equals(p)) {
            throw new IllegalArgumentException(p + " is not a root path");
        }
        return p;
    }

//    private Path<?> normalizePath(Path<?> expr) {
//        Context context = new Context();
//        Path<?> replaced = (Path<?>)expr.accept(CollectionAnyVisitor.DEFAULT, context);
//        if (!replaced.equals(expr)) {
//            for (int i = 0; i < context.paths.size(); i++) {
//                Path path = context.paths.get(i).getMetadata().getParent();
//                Path replacement = context.replacements.get(i);
////                this.innerJoin(path, replacement);
//            }
//            return replaced;
//        } else {
//            return expr;
//        }
//    }

    @SuppressWarnings("rawtypes")
    public <RT> Expression<RT> convert(Expression<RT> expr, boolean forOrder) {
//        if (expandAnyPaths) {
//            if (expr instanceof Path) {
//                expr = (Expression)normalizePath((Path)expr);
//            } else if (expr != null) {
//                if (replaceVisitor == null) {
//                    replaceVisitor = new ReplaceVisitor() {
//                        public Expression<?> visit(Path<?> expr, @Nullable Void context) {
//                            return normalizePath(expr);
//                        }
//                    };
//                }
//                expr = (Expression)expr.accept(replaceVisitor, null);
//            }
//        }
//        if (expr instanceof ProjectionRole<?>) {
//            return convert(((ProjectionRole) expr).getProjection(), forOrder);
//        } else if (expr instanceof FactoryExpression<?> && !(expr instanceof FactoryExpressionAdapter<?>)) {
//            return FactoryExpressionUtils.wrap((FactoryExpression<RT>)expr);
//        } else {
//            return expr;
//        }
        return expr;
    }
//
//    public Expression<Tuple> createProjection(Expression<?>[] args) {
//        return new QTuple(args);
//    }

    public final T distinct() {
        metadata.setDistinct(true);
        return self;
    }

    public final T from(Expression<?> arg) {
        metadata.setSource(arg);
        return self;
    }


    public final QueryMetadata getMetadata() {
        return metadata;
    }

    public final T getSelf() {
        return self;
    }

    public final T groupBy(Expression<?> e) {
        metadata.addGroupBy(e);
        return self;
    }

    public final T groupBy(Expression<?>... o) {
        for (Expression<?> e : o) {
            metadata.addGroupBy(e);
        }
        return self;
    }

    public final T having(Predicate e) {
        metadata.addHaving(normalize(e, false));
        return self;
    }

    public final T having(Predicate... o) {
        for (Predicate e : o) {
            metadata.addHaving(normalize(e, false));
        }
        return self;
    }


    public final boolean isDistinct() {
        return metadata.isDistinct();
    }

    public final boolean isUnique() {
        return metadata.isUnique();
    }


    public final T limit(long limit) {
        metadata.setLimit(limit);
        return self;
    }

    public final T offset(long offset) {
        metadata.setOffset(offset);
        return self;
    }

    public final T orderBy(OrderSpecifier<?> spec) {
        Expression<?> e = convert(spec.getTarget(), true);
        if (!spec.getTarget().equals(e)) {
            metadata.addOrderBy(new OrderSpecifier(spec.getOrder(), e));
        } else {
            metadata.addOrderBy(spec);
        }
        return self;
    }

    public final T orderBy(OrderSpecifier<?>... o) {
        for (OrderSpecifier<?> spec : o) {
            orderBy(spec);
        }
        return self;
    }

    public final T restrict(QueryModifiers modifiers) {
        metadata.setModifiers(modifiers);
        return self;
    }


//    public final <P> T set(ParamExpression<P> param, P value) {
//        metadata.setParam(param, value);
//        return self;
//    }

    public final void setDistinct(boolean distinct) {
        metadata.setDistinct(distinct);
    }

    public final void setSelf(T self) {
        this.self = self;
    }

    public final void setUnique(boolean unique) {
        metadata.setUnique(unique);
    }

    public final T where(Predicate e) {
        metadata.addWhere(normalize(e, true));
        return self;
    }

    public final T where(Predicate... o) {
        for (Predicate e : o) {
            metadata.addWhere(normalize(e, true));
        }
        return self;
    }

    protected Predicate normalize(Predicate condition, boolean where) {
        return condition;
    }

    @Override
    public final boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (o instanceof QueryMixin) {
            QueryMixin q = (QueryMixin)o;
            return q.metadata.equals(metadata);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return metadata.hashCode();
    }

    @Override
    public String toString() {
        return metadata.toString();
    }

}
