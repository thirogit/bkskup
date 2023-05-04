package com.bk.bksettings.annotation;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
* Created by IntelliJ IDEA.
* User: SG0891787
* Date: 7/30/12
* Time: 9:51 PM
*/
public abstract class TypeVisitor<T,P> {
    public final T visit( Type t, P param ) {
        assert t!=null;

        if (t instanceof Class)
            return onClass((Class)t,param);
        if (t instanceof ParameterizedType)
            return onParameterizdType( (ParameterizedType)t,param);
        throw new IllegalArgumentException();
    }

    protected abstract T onClass(Class c, P param);
    protected abstract T onParameterizdType(ParameterizedType p, P param);

}
