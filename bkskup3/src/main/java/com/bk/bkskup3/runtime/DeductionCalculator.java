package com.bk.bkskup3.runtime;

import com.bk.bkskup3.model.InvoiceDeduction;

/**
 * Created with IntelliJ IDEA.
 * User: SG0891787
 * Date: 4/26/2015
 * Time: 9:39 PM
 */
public interface DeductionCalculator {

    InvoiceDeduction getDeduction();
    double getDeductedAmount();
}
