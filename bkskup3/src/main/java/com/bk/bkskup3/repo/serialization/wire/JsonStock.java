package com.bk.bkskup3.repo.serialization.wire;

import com.bk.bkskup3.model.Stock;
import com.bk.bkskup3.model.StockObj;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by IntelliJ IDEA.
 * User: SG0891787
 * Date: 2/29/12
 * Time: 2:53 PM
 */

public class JsonStock {
    private StockObj mStock;

    public JsonStock(Stock stock)
    {
        this.mStock = new StockObj(stock.getId());
        this.mStock.copyFrom(stock);
    }

    public JsonStock() {
        this.mStock = new StockObj();
    }

    @JsonIgnore
    public StockObj getStock() {
        return mStock;
    }

    @JsonProperty("name")
    public String getStockName() {
        return mStock.getStockName();
    }

    @JsonProperty("code")
    public String getStockCode() {
        return mStock.getStockName();
    }

    public void setStockName(String stockName) {
        mStock.setStockName(stockName);
    }

    public void setStockCode(String stockCode) {
        mStock.setStockCode(stockCode);
    }

}
