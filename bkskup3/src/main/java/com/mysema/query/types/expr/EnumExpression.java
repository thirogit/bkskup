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
package com.mysema.query.types.expr;

import com.mysema.query.types.*;

/**
 * EnumExpression represents Enum typed expressions
 *
 * @param <T> expression type
 * @author tiwe
 */
public abstract class EnumExpression<T extends Enum<T>> extends ComparableExpression<T> {

    private static final long serialVersionUID = 8819222316513862829L;

    public EnumExpression(Expression<T> mixin) {
        super(mixin);
    }


    public BooleanExpression eq(T right) {
        if (right == null) {
            throw new IllegalArgumentException("eq(null) is not allowed. Use isNull() instead");
        } else {
            return BooleanOperation.create(Ops.EQQ, mixin, ConstantImpl.create(right));
        }
    }


    /**
     * @return
     */
    public NumberExpression<Integer> ordinal() {
        return NumberOperation.create(Integer.class, Ops.ORDINAL, mixin);
    }

}