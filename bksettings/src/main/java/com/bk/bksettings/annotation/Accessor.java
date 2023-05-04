package com.bk.bksettings.annotation;

import com.bk.bksettings.runtime.AccessorException;

/**
 * Created with IntelliJ IDEA.
 * User: SG0891787
 * Date: 1/6/14
 * Time: 6:47 PM
 */
public interface Accessor {
    Object get(Object thisObj) throws AccessorException;

    void set(Object thisObj, Object fieldValue) throws AccessorException;
}
