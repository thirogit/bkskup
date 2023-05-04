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
import com.mysema.query.types.Ops.MathOps;
import com.mysema.util.MathUtils;

import javax.annotation.Nullable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * NumberExpression represents a numeric expression
 *
 * @author tiwe
 *
 * @param <T> expression type
 * @see Number
 */
public abstract class NumberExpression<T extends Number & Comparable<?>> extends ComparableExpressionBase<T> {

    private static final long serialVersionUID = -5485902768703364888L;

    @Nullable
    private static final NumberExpression<Double> random = NumberOperation.create(Double.class, MathOps.RANDOM);

    /**
     * Return the greater of the given values
     *
     * @return max(left, right)
     */
    public static <A extends Number & Comparable<?>> NumberExpression<A> max(Expression<A> left, Expression<A> right) {
        return NumberOperation.create(left.getType(), MathOps.MAX, left, right);
    }

    /**
     * Return the smaller of the given values
     *
     * @return min(left, right)
     */
    public static <A extends Number & Comparable<?>> NumberExpression<A> min(Expression<A> left, Expression<A> right) {
        return NumberOperation.create(left.getType(), MathOps.MIN, left, right);
    }

    /**
     * Returns the random expression
     * @return random()
     */
    public static NumberExpression<Double> random() {
        return random;
    }

    @Nullable
    private volatile NumberExpression<T> abs, sum, min, max, floor, ceil, round;

    @Nullable
    private volatile NumberExpression<Double> avg, sqrt;

    @Nullable
    private volatile NumberExpression<T> negation;

    public NumberExpression(Expression<T> mixin) {
        super(mixin);
    }

    /**
     * Get the absolute value of this expression
     *
     * @return abs(this)
     */
    public NumberExpression<T> abs() {
        if (abs == null) {
            abs = NumberOperation.create(getType(), MathOps.ABS, mixin);
        }
        return abs;
    }

    /**
     * Get the sum of this and right
     *
     * @param right
     * @return this + right
     */
    public <N extends Number & Comparable<?>> NumberExpression<T> add(Expression<N> right) {
        return NumberOperation.create(getType(), Ops.ADD, mixin, right);
    }

    /**
     * Get the sum of this and right
     *
     * @param right
     * @return this + right
     */
    public <N extends Number & Comparable<N>> NumberExpression<T> add(N right) {
        return NumberOperation.create(getType(), Ops.ADD, mixin, ConstantImpl.create(right));
    }

    /**
     * Get the average value of this expression (aggregation)
     *
     *  @return avg(this)
     */
    public NumberExpression<Double> avg() {
        if (avg == null) {
            avg = NumberOperation.create(Double.class, Ops.AggOps.AVG_AGG, mixin);
        }
        return avg;
    }

    /**
     * Get the byte expression of this numeric expression
     *
     * @return this.byteValue()
     * @see Number#byteValue()
     */
    public NumberExpression<Byte> byteValue() {
        return castToNum(Byte.class);
    }

    @SuppressWarnings("unchecked")
    private T cast(Number number) {
        Class<T> type = (Class<T>) getType();
        if (type.equals(number.getClass())) {
            return (T) number;
        } else if (Byte.class.equals(type)) {
            return (T) Byte.valueOf(number.byteValue());
        } else if (Double.class.equals(type)) {
            return (T) Double.valueOf(number.doubleValue());
        } else if (Float.class.equals(type)) {
            return (T) Float.valueOf(number.floatValue());
        } else if (Integer.class.equals(type)) {
            return (T) Integer.valueOf(number.intValue());
        } else if (Long.class.equals(type)) {
            return (T) Long.valueOf(number.longValue());
        } else if (Short.class.equals(type)) {
            return (T) Short.valueOf(number.shortValue());
        } else if (BigInteger.class.equals(type)) {
            return (T) new BigInteger(String.valueOf(number.longValue()));
        } else if (BigDecimal.class.equals(type)) {
            return (T) new BigDecimal(number.toString());
        } else {
            throw new IllegalArgumentException("Unsupported target type : " + type.getName());
        }
    }


    /**
     * Returns the smallest (closest to negative infinity)
     * <code>double</code> value that is greater than or equal to the
     * argument and is equal to a mathematical integer
     *
     * @return ceil(this)
     * @see Math#ceil(double)
     */
    public NumberExpression<T> ceil() {
        if (ceil == null) {
            ceil = NumberOperation.create(getType(), MathOps.CEIL, mixin);
        }
        return ceil;
    }

    private Class<?> getDivisionType(Class<?> left, Class<?> right) {
        if (!left.equals(right)) {
            return Double.class;
        } else {
            return left;
        }
    }

    /**
     * Get the result of the operation this / right
     *
     * @param right
     * @return this / right
     */
    public <N extends Number & Comparable<?>> NumberExpression<T> divide(Expression<N> right) {
        Class<?> type = getDivisionType(getType(), right.getType());
        return NumberOperation.create((Class<T>)type, Ops.DIV, mixin, right);
    }

    /**
     * Get the result of the operation this / right
     *
     * @param right
     * @return this / right
     */
    public <N extends Number & Comparable<?>> NumberExpression<T> divide(N right) {
        Class<?> type = getDivisionType(getType(), right.getClass());
        return NumberOperation.create((Class<T>)type, Ops.DIV, mixin, ConstantImpl.create(right));
    }

    /**
     * Get the double expression of this numeric expression
     *
     * @return this.doubleValue()
     * @see Number#doubleValue()
     */
    public NumberExpression<Double> doubleValue() {
        return castToNum(Double.class);
    }

    /**
     * Get the float expression of this numeric expression
     *
     * @return this.floatValue()
     * @see Number#floatValue()
     */
    public NumberExpression<Float> floatValue() {
        return castToNum(Float.class);
    }

    /**
     * Returns the largest (closest to positive infinity)
     * <code>double</code> value that is less than or equal to the
     * argument and is equal to a mathematical integer.
     *
     * @return floor(this)
     * @see Math#floor(double)
     */
    public NumberExpression<T> floor() {
        if (floor == null) {
            floor = NumberOperation.create(getType(), MathOps.FLOOR, mixin);
        }
        return floor;
    }

    /**
     * Create a <code>this &gt;= right</code> expression
     *
     * @param <A>
     * @param right rhs of the comparison
     * @return {@code this >= right}
     * @see Comparable#compareTo(Object)
     */
    public final <A extends Number & Comparable<?>> BooleanExpression goe(A right) {
        return goe(ConstantImpl.create(cast(right)));
    }

    /**
     * Create a <code>this &gt;= right</code> expression
     *
     * @param <A>
     * @param right rhs of the comparison
     * @return {@code this >= right}
     * @see Comparable#compareTo(Object)
     */
    public final <A extends Number & Comparable<?>> BooleanExpression goe(Expression<A> right) {
        return BooleanOperation.create(Ops.GOE, mixin, right);
    }

    /**
     * @param right
     * @return
     */
    public BooleanExpression goeAll(CollectionExpression<?, ? super T> right) {
        return goe(ExpressionUtils.<T>all(right));
    }

    /**
     * @param right
     * @return
     */
    public BooleanExpression goeAny(CollectionExpression<?, ? super T> right) {
        return goe(ExpressionUtils.<T>any(right));
    }

    /**
     * Create a <code>this &gt; right</code> expression
     *
     * @param <A>
     * @param right rhs of the comparison
     * @return {@code this > right}
     * @see Comparable#compareTo(Object)
     */
    public final <A extends Number & Comparable<?>> BooleanExpression gt(A right) {
        return gt(ConstantImpl.create(cast(right)));
    }

    /**
     * Create a <code>this &gt; right</code> expression
     *
     * @param <A>
     * @param right rhs of the comparison
     * @return {@code this > right}
     * @see Comparable#compareTo(Object)
     */
    public final <A extends Number & Comparable<?>> BooleanExpression gt(Expression<A> right) {
        return BooleanOperation.create(Ops.GT, mixin, right);
    }

    /**
     * @param right
     * @return
     */
    public BooleanExpression gtAll(CollectionExpression<?, ? super T> right) {
        return gt(ExpressionUtils.<T>all(right));
    }

    /**
     * @param right
     * @return
     */
    public BooleanExpression gtAny(CollectionExpression<?, ? super T> right) {
        return gt(ExpressionUtils.<T>any(right));
    }

    /**
     * Create a <code>from &lt; this &lt; to</code> expression
     *
     * @param <A>
     * @param from
     * @param to
     * @return
     */
    public final <A extends Number & Comparable<?>> BooleanExpression between(@Nullable A from, @Nullable A to) {
        if (from == null) {
            if (to != null) {
                return BooleanOperation.create(Ops.LOE, mixin, ConstantImpl.create(to));
            } else {
                throw new IllegalArgumentException("Either from or to needs to be non-null");
            }
        } else if (to == null) {
            return BooleanOperation.create(Ops.GOE, mixin, ConstantImpl.create(from));
        } else {
            return BooleanOperation.create(Ops.BETWEEN, mixin, ConstantImpl.create(from), ConstantImpl.create(to));
        }
    }

    /**
     * Create a <code>from &lt; this &lt; to</code> expression
     *
     * @param <A>
     * @param from
     * @param to
     * @return
     */
    public final <A extends Number & Comparable<?>> BooleanExpression between(@Nullable Expression<A> from, @Nullable Expression<A> to) {
        if (from == null) {
            if (to != null) {
                return BooleanOperation.create(Ops.LOE, mixin, to);
            } else {
                throw new IllegalArgumentException("Either from or to needs to be non-null");
            }
        } else if (to == null) {
            return BooleanOperation.create(Ops.GOE, mixin, from);
        } else {
            return BooleanOperation.create(Ops.BETWEEN, mixin, from, to);
        }
    }

    /**
     * @param from
     * @param to
     * @return
     */
    public final <A extends Number & Comparable<?>> BooleanExpression notBetween(A from, A to) {
        return between(from, to).not();
    }

    /**
     * @param from
     * @param to
     * @return
     */
    public final <A extends Number & Comparable<?>> BooleanExpression notBetween(Expression<A> from, Expression<A> to) {
        return between(from, to).not();
    }

    /**
     * Get the int expression of this numeric expression
     *
     * @return this.intValue()
     * @see Number#intValue()
     */
    public NumberExpression<Integer> intValue() {
        return castToNum(Integer.class);
    }

    /**
     * Expr: <code>this like str</code>
     *
     * @param str
     * @return
     */
    public BooleanExpression like(String str) {
        return BooleanOperation.create(Ops.LIKE, stringValue(), ConstantImpl.create(str));
    }

    /**
     * Expr: <code>this like str</code>
     *
     * @param str
     * @return
     */
    public BooleanExpression like(Expression<String> str) {
        return BooleanOperation.create(Ops.LIKE, stringValue(), str);
    }

    /**
     * Create a <code>this &lt;= right</code> expression
     *
     * @param <A>
     * @param right rhs of the comparison
     * @return {@code this <= right}
     * @see Comparable#compareTo(Object)
     */
    public final <A extends Number & Comparable<?>> BooleanExpression loe(A right) {
        return loe(ConstantImpl.create(cast(right)));
    }

    /**
     * Create a <code>this &lt;= right</code> expression
     *
     * @param <A>
     * @param right rhs of the comparison
     * @return {@code this <= right}
     * @see Comparable#compareTo(Object)
     */
    public final <A extends Number & Comparable<?>> BooleanExpression loe(Expression<A> right) {
        return BooleanOperation.create(Ops.LOE, mixin, right);
    }

    /**
     * @param right
     * @return
     */
    public BooleanExpression loeAll(CollectionExpression<?, ? super T> right) {
        return loe(ExpressionUtils.<T>all(right));
    }

    /**
     * @param right
     * @return
     */
    public BooleanExpression loeAny(CollectionExpression<?, ? super T> right) {
        return loe(ExpressionUtils.<T>any(right));
    }

    /**
     * Get the long expression of this numeric expression
     *
     * @return this.longValue()
     * @see Number#longValue()
     */
    public NumberExpression<Long> longValue() {
        return castToNum(Long.class);
    }

    /**
     * Create a <code>this &lt; right</code> expression
     *
     * @param <A>
     * @param right rhs of the comparison
     * @return {@code this < right}
     * @see Comparable#compareTo(Object)
     */
    public final <A extends Number & Comparable<?>> BooleanExpression lt(A right) {
        return lt(ConstantImpl.create(cast(right)));
    }

    /**
     * Create a <code>this &lt; right</code> expression
     *
     * @param <A>
     * @param right rhs of the comparison
     * @return {@code this < right}
     * @see Comparable#compareTo(Object)
     */
    public final <A extends Number & Comparable<?>> BooleanExpression lt(Expression<A> right) {
        return BooleanOperation.create(Ops.LT, this, right);
    }

    /**
     * @param right
     * @return
     */
    public BooleanExpression ltAll(CollectionExpression<?, ? super T> right) {
        return lt(ExpressionUtils.<T>all(right));
    }

    /**
     * @param right
     * @return
     */
    public BooleanExpression ltAny(CollectionExpression<?, ? super T> right) {
        return lt(ExpressionUtils.<T>any(right));
    }

    /**
     * Get the maximum value of this expression (aggregation)
     *
     * @return max(this)
     */
    @SuppressWarnings("unchecked")
    public NumberExpression<T> max() {
        if (max == null) {
            max = NumberOperation.create(getType(), Ops.AggOps.MAX_AGG, mixin);
        }
        return max;
    }

    /**
     * Get the minimum value of this expression (aggregation)
     *
     * @return min(this)
     */
    @SuppressWarnings("unchecked")
    public NumberExpression<T> min() {
        if (min == null) {
            min = NumberOperation.create(getType(), Ops.AggOps.MIN_AGG, mixin);
        }
        return min;
    }

    /**
     * @param num
     * @return
     */
    public NumberExpression<T> mod(Expression<T> num) {
        return NumberOperation.create(getType(), Ops.MOD, mixin, num);
    }

    /**
     * @param num
     * @return
     */
    public NumberExpression<T> mod(T num) {
        return NumberOperation.create(getType(), Ops.MOD, mixin, ConstantImpl.create(num));
    }

    /**
     * Get the result of the operation this * right
     *
     * @param right
     * @return this * right
     */
    public <N extends Number & Comparable<?>> NumberExpression<T> multiply(Expression<N> right) {
        return NumberOperation.create(getType(), Ops.MULT, mixin, right);
    }

    /**
     * Get the result of the operation this * right
     *
     * @param right
     * @return this * right
     */
    public <N extends Number & Comparable<N>> NumberExpression<T> multiply(N right) {
        return NumberOperation.create(getType(), Ops.MULT, mixin, ConstantImpl.create(right));
    }

    /**
     * Get the negation of this expression
     *
     * @return this * -1
     */
    public NumberExpression<T> negate() {
        if (negation == null) {
            negation = NumberOperation.create(getType(), Ops.NEGATE, mixin);
        }
        return negation;
    }

    /**
     * Returns the closest <code>int</code> to the argument.
     *
     * @return round(this)
     * @see Math#round(double)
     * @see Math#round(float)
     */
    public NumberExpression<T> round() {
        if (round == null) {
            round = NumberOperation.create(getType(), MathOps.ROUND, mixin);
        }
        return round;
    }

    /**
     * Get the short expression of this numeric expression
     *
     * @return this.shortValue()
     * @see Number#shortValue()
     */
    public NumberExpression<Short> shortValue() {
        return castToNum(Short.class);
    }

    /**
     * Get the square root of this numeric expressions
     *
     * @return sqrt(this)
     */
    public NumberExpression<Double> sqrt() {
        if (sqrt == null) {
            sqrt = NumberOperation.create(Double.class, MathOps.SQRT, mixin);
        }
        return sqrt;
    }

    /**
     * Get the difference of this and right
     *
     * @param right
     * @return this - right
     */
    public <N extends Number & Comparable<?>> NumberExpression<T> subtract(Expression<N> right) {
        return NumberOperation.create(getType(), Ops.SUB, mixin, right);
    }

    /**
     * Get the difference of this and right
     *
     * @param right
     * @return this - right
     */
    public <N extends Number & Comparable<?>> NumberExpression<T> subtract(N right) {
        return NumberOperation.create(getType(), Ops.SUB, mixin, ConstantImpl.create(right));
    }

    /**
     * Get the sum of this expression (aggregation)
     *
     * @return sum(this)
     */
    public NumberExpression<T> sum() {
        if (sum == null) {
            sum = NumberOperation.create(getType(), Ops.AggOps.SUM_AGG, mixin);
        }
        return sum;
    }

    @Override
    public BooleanExpression in(Number... numbers) {
        return super.in(convert(numbers));
    }

    @Override
    public BooleanExpression notIn(Number... numbers) {
        return super.notIn(convert(numbers));
    }

    private List<T> convert(Number... numbers) {
        List<T> list = new ArrayList<T>(numbers.length);
        for (int i = 0; i < numbers.length; i++) {
            list.add(MathUtils.cast(numbers[i], getType()));
        }
        return list;
    }

}
