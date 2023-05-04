package com.bk.bksettings.annotation;

import com.bk.bksettings.runtime.AccessorException;

import java.lang.reflect.Field;

/**
 * Created with IntelliJ IDEA.
 * User: SG0891787
 * Date: 1/6/14
 * Time: 6:48 PM
 */
public class DirectAccessor implements Accessor {
    private Field field;

    public DirectAccessor(Field field) {
        this.field = field;
    }

    @Override
    public Object get(Object thisObj) throws AccessorException {
        try {
            field.setAccessible(true);
            return field.get(thisObj);
        } catch (IllegalAccessException e) {
            throw new AccessorException(e);
        }
    }

    @Override
    public void set(Object thisObj, Object fieldValue) throws AccessorException {
        try {
            field.setAccessible(true);
            field.set(thisObj, fieldValue);
        } catch (IllegalAccessException e) {
            throw new AccessorException(e);
        }
    }
}
