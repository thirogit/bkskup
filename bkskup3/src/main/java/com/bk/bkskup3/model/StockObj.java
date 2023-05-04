package com.bk.bkskup3.model;


import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: root
 * Date: 14.06.11
 * Time: 17:14
 */


public class StockObj extends IdableObj implements Stock,Serializable {
    protected String stockName;
    protected String stockCode;

    public StockObj(int id) {
        super(id);
    }

    public StockObj() {
        super(0);
    }

    public String getStockName() {
        return stockName;
    }

    public String getStockCode() {
        return stockCode;
    }

    public void setStockName(String stockName) {
        this.stockName = stockName;
    }

    public void setStockCode(String stockCode) {
        this.stockCode = stockCode;
    }

    public void copyFrom(Stock stock) {
        setStockName(stock.getStockName());
        setStockCode(stock.getStockCode());
    }
}
