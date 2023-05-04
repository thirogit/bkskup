package com.bk.bkskup3.model;

/**
 * Created by IntelliJ IDEA.
 * User: SG0891787
 * Date: 2/15/12
 * Time: 7:38 PM
 */
public interface CowClass extends Idable
{
   String getClassName();
   String getClassCode();
   CowSex getPredefSex();
   Double getPricePerKg();
}
