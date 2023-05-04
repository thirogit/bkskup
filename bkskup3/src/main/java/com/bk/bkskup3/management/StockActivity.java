package com.bk.bkskup3.management;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;

import com.bk.bkskup3.BkActivity;
import com.bk.bkskup3.R;
import com.bk.bkskup3.dao.DefinitionsStore;
import com.bk.bkskup3.dao.q.QStock;
import com.bk.bkskup3.feedback.ErrorToast;
import com.bk.bkskup3.model.Stock;
import com.bk.bkskup3.model.StockObj;
import com.google.common.collect.Iterables;
import com.mysema.query.types.expr.BooleanExpression;

import java.util.Collection;

import javax.inject.Inject;

import static com.mysema.query.support.QueryBuilder.where;

/**
 * Created with IntelliJ IDEA.
 * User: SG0891787
 * Date: 11/29/2014
 * Time: 9:31 PM
 */
public abstract class StockActivity extends BkActivity {

    private static final String STATE_EXTRA_STOCKID = "state_stockid";

    public static final String EXTRA_STOCK_ID = "stock_id";
    public static final String EXTRA_SAVED_STOCK = "saved_stock";

    private EditText mStockNameBox;
    private EditText mStockCodeBox;
    private int mStockId;

    @Inject
    DefinitionsStore mDefinitionsStore;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stock);

        mStockNameBox = (EditText) findViewById(R.id.cowStockNameEditBox);
        mStockCodeBox = (EditText) findViewById(R.id.cowStockCodeEditBox);

        if (savedInstanceState == null) {

            Intent intent = getIntent();
            mStockId = intent.getIntExtra(EXTRA_STOCK_ID, 0);

            if (mStockId != 0) {
                Collection<StockObj> stockObjs = mDefinitionsStore.fetchStocks(where(QStock.id.eq(mStockId)));
                StockObj stockToEdit = Iterables.getFirst(stockObjs, null);

                if (stockToEdit != null) {
                    mStockId = stockToEdit.getId();

                    mStockNameBox.setText(stockToEdit.getStockName());
                    mStockCodeBox.setText(stockToEdit.getStockCode());
                } else {
                    mStockId = 0;
                }
            }

        } else {
            mStockId = savedInstanceState.getInt(EXTRA_STOCK_ID);
        }
    }

    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(EXTRA_STOCK_ID, mStockId);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.save_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuSave:
                saveStock();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;

    }

    private StockObj createStockFromBoxes() {
        StockObj stockObj = new StockObj(mStockId);
        stockObj.setStockName(mStockNameBox.getText().toString());
        stockObj.setStockCode(mStockCodeBox.getText().toString());
        return stockObj;
    }

    private void saveStock() {

        if (validateInput()) {
            StockObj stockObj = createStockFromBoxes();
            if (validate(stockObj)) {
                save(stockObj);
                Intent result = new Intent();
                result.putExtra(EXTRA_SAVED_STOCK, stockObj);
                setResult(RESULT_OK, result);
                finish();
            }
        }
    }

    private void save(StockObj stock) {
        if (stock.getId() != 0) {
            mDefinitionsStore.updateStock(stock);
        } else {
            mDefinitionsStore.insertStock(stock);
        }
    }

    private boolean validate(Stock stock) {
        BooleanExpression predicate = QStock.stockCode.eq(stock.getStockCode());

        if (stock.getId() != 0) {
            predicate = QStock.id.ne(stock.getId()).and(predicate);
        }

        StockObj duplicate = Iterables.getFirst(mDefinitionsStore.fetchStocks(where(predicate)), null);

        if (duplicate != null) {
            displayErrorToast(R.string.errStockAlreadyDefined);
            return false;
        }
        return true;
    }

    private boolean validateInput() {

        if (mStockCodeBox.getText().length() == 0) {
            displayErrorToast(R.string.errEmptyStockCode);
            return false;
        }
        return true;
    }

    private void displayErrorToast(int msgId) {
        new ErrorToast(this).show(msgId);
    }
}
