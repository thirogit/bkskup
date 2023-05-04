package com.bk.bkskup3.widgets;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.InputType;
import android.util.AttributeSet;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import com.bk.bkskup3.model.IBAN;
import com.bk.countries.Countries;
import com.bk.countries.Country;

public class AccountNoEditText extends EditText {

	public AccountNoEditText(Context context, AttributeSet attrs) {

        super(context, attrs);
        setCustomSelectionActionModeCallback(new NoTextSelectionMode());
        setInputType(InputType.TYPE_NULL);
	}

    public void setIBAN(IBAN iban)
    {
        if(iban != null)
        {
            Country country = iban.getCountry();
            int flagDrawableId = Countries.getCountries().getCountryFlag(country.getIsoNumber());
            final Drawable flag = getResources().getDrawable(flagDrawableId);
            flag.setBounds(0,0,flag.getIntrinsicWidth(),flag.getIntrinsicHeight());
            setCompoundDrawables(flag, null, null, null);
            setText(iban.toString());
        }
        else
        {
            setCompoundDrawables(null, null,  null, null);
            getText().clear();
        }
    }

    class NoTextSelectionMode implements ActionMode.Callback {
        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            return false;
        }

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            // Prevents the selection action mode on double tap.
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {}

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }
    }
}
