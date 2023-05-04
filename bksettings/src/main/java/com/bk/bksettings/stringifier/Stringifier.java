package com.bk.bksettings.stringifier;

/**
 * Created by IntelliJ IDEA.
 * User: SG0891787
 * Date: 4/12/12
 * Time: 1:41 PM
 */
public interface Stringifier
{
   Object unstringify(String value);
   String stringify(Object obj);
   boolean isApplicable(Object obj);
}
