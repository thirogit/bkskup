package com.bk.bkskup3.utils.check;

import com.bk.bkskup3.model.EAN;

/**
* Created by IntelliJ IDEA.
* User: SG0891787
* Date: 9/20/12
* Time: 3:34 PM
*/
public interface EANCheck extends Check<EAN>
{
   boolean isValid(EAN ean);
}
