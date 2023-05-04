package com.bk.bkskup3.work;

import com.bk.bkskup3.model.CowClass;
import com.bk.bkskup3.model.CowClassObj;
import com.bk.bkskup3.model.Stock;
import com.bk.bkskup3.model.StockObj;
import com.bk.bkskup3.settings.InputDefaultsSettings;
import com.google.common.collect.ImmutableList;

import java.io.Serializable;
import java.util.Collection;
import java.util.LinkedList;

/**
 * Created with IntelliJ IDEA.
 * User: SG0891787
 * Date: 12/7/2014
 * Time: 2:45 PM
 */
public class DependenciesForCow  implements Serializable{

    private Collection<CowClassObj> mClasses = new LinkedList<CowClassObj>();
    private Collection<StockObj> mStocks = new LinkedList<StockObj>();

    private InputDefaultsSettings mInputDefaults;

    public Collection<CowClass> getClasses()
    {
        return ImmutableList.<CowClass>copyOf(mClasses);
    }

    public Collection<Stock> getStocks()
    {
        return ImmutableList.<Stock>copyOf(mStocks);
    }

    public void addStock(StockObj stock)
    {
        mStocks.add(stock);
    }

    public void addClass(CowClassObj classObj)
    {
        mClasses.add(classObj);
    }

    public void setInputDefaults(InputDefaultsSettings inputDefaults) {
        this.mInputDefaults = inputDefaults;
    }

    public InputDefaultsSettings getInputDefaults() {
        return mInputDefaults;
    }
}
