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
package com.mysema.serializer;

import com.google.common.collect.ImmutableList;
//import com.mysema.query.QueryFlag;
import com.mysema.query.types.*;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * SerializerBase is a stub for Serializer implementations which serialize query metadata to Strings
 *
 * @author tiwe
 */
public abstract class SerializerBase<S extends SerializerBase<S>> implements Visitor<Void, Void> {

    private static final String VISITOBJECTMETHODNAME = "visitObject";

    private final StringBuilder builder = new StringBuilder(128);

    @SuppressWarnings("unchecked")
    private final S self = (S) this;

    private final Templates templates;

    public SerializerBase(Templates templates) {
        this.templates = templates;
    }

    public final S prepend(final String str) {
        builder.insert(0, str);
        return self;
    }

    public final S insert(int position, String str) {
        builder.insert(position, str);
        return self;
    }

    public final S append(final String str) {
        builder.append(str);
        return self;
    }

    public int getLength() {
        return builder.length();
    }

    protected final Template getTemplate(Operator<?> op) {
        return templates.getTemplate(op);
    }

    public final S handle(Expression<?> expr) {
        expr.accept(this, null);
        return self;
    }

    public final S handle(Object arg) {
        if (arg instanceof Expression) {
            ((Expression) arg).accept(this, null);
        } else {
            visitConstant(arg);
        }
        return self;
    }


    public final S handle(final String sep, final Expression<?>[] expressions) {
        for (int i = 0; i < expressions.length; i++) {
            if (i != 0) {
                append(sep);
            }
            handle(expressions[i]);
        }
        return self;
    }

    public final S handle(final String sep, final List<?> expressions) {
        for (int i = 0; i < expressions.size(); i++) {
            if (i != 0) {
                append(sep);
            }
            handle((Expression<?>) expressions.get(i));
        }
        return self;
    }

    protected void handleTemplate(final Template template, final List<?> args) {
        for (final Template.Element element : template.getElements()) {
            final Object rv = element.convert(args);
            if (rv instanceof Expression) {
                ((Expression) rv).accept(this, null);
            } else if (element.isString()) {
                builder.append(rv.toString());
            } else {
                visitConstant(rv);
            }
        }
    }

//    public final boolean serialize(final QueryFlag.Position position, final Set<QueryFlag> flags) {
//        boolean handled = false;
//        for (final QueryFlag flag : flags) {
//            if (flag.getPosition() == position) {
//                handle(flag.getFlag());
//                handled = true;
//            }
//        }
//        return handled;
//    }


    @Override
    public String toString() {
        return builder.toString();
    }

    @Override
    public final Void visit(Constant<?> expr, Void context) {
        visitConstant(expr.getConstant());
        return null;
    }

    public void visitConstant(Object constant) {

        Method visitMethodForClass = findVisitMethodForClass(constant.getClass());
        if(visitMethodForClass != null)
        {
            try {
                visitMethodForClass.invoke(this, constant);
            } catch (IllegalAccessException e) {}
              catch (InvocationTargetException e) {}
        }
        else {
            append(constant.toString());
        }
    }

    private Method findVisitMethodForClass(Class clazz) {

        Class<? extends SerializerBase> thisClass = this.getClass();

        try {
            return thisClass.getMethod(VISITOBJECTMETHODNAME, clazz);
        } catch (NoSuchMethodException e) {


            for(Method method : thisClass.getDeclaredMethods())
            {
                if(VISITOBJECTMETHODNAME.equals(method.getName()))
                {
                    Class<?>[] parameters = method.getParameterTypes();
                    if(parameters.length == 1)
                    {
                        Class<?> onlyParameterClass = parameters[0];
                        if(onlyParameterClass.isAssignableFrom(clazz))
                            return method;
                    }
                }
            }

        }
        return null;
    }

//    @Override
//    public Void visit(ParamExpression<?> param, Void context) {
//        String paramLabel;
//        if (param.isAnon()) {
//            paramLabel = anonParamPrefix + param.getName();
//        } else {
//            paramLabel = paramPrefix + param.getName();
//        }
//        getConstantToLabel().put(param, paramLabel);
//        append(paramLabel);
//        return null;
//    }
//
//    @Override
//    public Void visit(TemplateExpression<?> expr, Void context) {
//        handleTemplate(expr.getTemplate(), expr.getArgs());
//        return null;
//    }
//
//    @Override
//    public Void visit(FactoryExpression<?> expr, Void context) {
//        handle(", ", expr.getArgs());
//        return null;
//    }

    @Override
    public Void visit(Operation<?> expr, Void context) {
        visitOperation(expr.getType(), expr.getOperator(), expr.getArgs());
        return null;
    }

    @Override
    public Void visit(Path<?> path, Void context) {
        final PathType pathType = path.getMetadata().getPathType();
        final Template template = templates.getTemplate(pathType);
        final Object element = path.getMetadata().getElement();
        List<Object> args;
        if (path.getMetadata().getParent() != null) {
            args = ImmutableList.of(path.getMetadata().getParent(), element);
        } else {
            args = ImmutableList.of(element);
        }
        handleTemplate(template, args);
        return null;
    }

    protected void visitOperation(Class<?> type, Operator<?> operator, final List<? extends Expression<?>> args) {
        final Template template = templates.getTemplate(operator);
        if (template != null) {
            final int precedence = templates.getPrecedence(operator);
            for (final Template.Element element : template.getElements()) {
                final Object rv = element.convert(args);
                if (rv instanceof Expression) {
                    final Expression<?> expr = (Expression<?>) rv;
                    if (precedence > -1 && expr instanceof Operation) {
                        if (precedence < templates.getPrecedence(((Operation<?>) expr).getOperator())) {
                            append("(").handle(expr).append(")");
                        } else {
                            handle(expr);
                        }
                    } else {
                        handle(expr);
                    }
                } else if (element.isString()) {
                    append(rv.toString());
                } else {
                    visitConstant(rv);
                }
            }
        } else {
            append(operator.toString());
            append("(");
            handle(", ", args);
            append(")");
        }
    }

}
