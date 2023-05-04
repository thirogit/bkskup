package com.bk.bkskup3.print.context;

import com.bk.bkskup3.model.Agent;
import com.bk.bkskup3.model.CowClass;
import com.bk.bkskup3.model.Stock;

import java.util.Collection;

/**
 * Created by SG0891787 on 2/17/2017.
 */

public interface Definitions {

    Collection<? extends CowClass> getCowClasses();
    CowClass getCowClass(String classCd);
    Collection<? extends Stock> getStocks();
    Agent getAgent();
}
