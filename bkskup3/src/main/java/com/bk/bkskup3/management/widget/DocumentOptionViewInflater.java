package com.bk.bkskup3.management.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bk.bkskup3.R;
import com.bk.bkskup3.input.BKDatePickerDialog;
import com.bk.bkskup3.library.DocumentOptionDefinition;
import com.bk.bkskup3.library.DocumentOptionType;
import com.bk.bkskup3.utils.Dates;
import com.google.common.base.Strings;

import java.text.ParseException;
import java.util.Date;

/**
 * Created by SG0891787 on 9/13/2017.
 */

public class DocumentOptionViewInflater {

    private LayoutInflater mInflater;

    public DocumentOptionViewInflater(LayoutInflater mInflater) {
        this.mInflater = mInflater;
    }

    public DocumentOptionView inflate(DocumentOptionDefinition optionDefinition)
    {
        DocumentOptionView optionView = null;
        switch (optionDefinition.getType())
        {
            case STRING:
                optionView = (DocumentOptionView) mInflater.inflate(R.layout.profile_option_string, null);
                optionView.mOptionType = DocumentOptionType.STRING;
                optionView.mOptionCode = optionDefinition.getCode();
                optionView.setTitle(optionDefinition.getTitle());
                optionView.setValue(optionDefinition.getDefaultValue());

                break;

            case DAYDATE:
                DocumentDayDateOptionView dayDateOptionView = (DocumentDayDateOptionView) mInflater.inflate(R.layout.profile_option_daydate, null);
                dayDateOptionView.mOptionType = DocumentOptionType.DAYDATE;
                dayDateOptionView.mOptionCode = optionDefinition.getCode();
                dayDateOptionView.setTitle(optionDefinition.getTitle());
                dayDateOptionView.setValue(optionDefinition.getDefaultValue());
                dayDateOptionView.setChangeDateClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DocumentDayDateOptionView optionView = (DocumentDayDateOptionView) v;
                        String value = optionView.getValue();
                        Date dt;
                        if(Strings.isNullOrEmpty(value)) {
                            dt = Dates.now();
                        } else {

                            try
                            {
                                dt = Dates.fromDayDate(value);
                            } catch (ParseException e) {
                                dt = Dates.now();
                            }
                        }

                        BKDatePickerDialog dtPickerDlg = new BKDatePickerDialog(optionView.getContext(), dt,
                                dt1 -> optionView.setValue(Dates.toDayDate(dt1)));

                        dtPickerDlg.show();
                    }
                });

                optionView = dayDateOptionView;
                break;
        }
        return optionView;
    }


}
