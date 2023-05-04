package com.bk.bkskup3.utils;

import com.bk.bkskup3.model.Idable;

/**
 * Created by IntelliJ IDEA.
 * User: SG0891787
 * Date: 3/29/13
 * Time: 7:53 PM
 */
public class IdableUtils
{
   public static int getSafeId(Idable idable)
   {
      if(idable == null)
         return 0;

      return idable.getId();
   }
}
