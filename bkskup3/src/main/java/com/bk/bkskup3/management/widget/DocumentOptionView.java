package com.bk.bkskup3.management.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bk.bkskup3.R;
import com.bk.bkskup3.library.DocumentOptionDefinition;
import com.bk.bkskup3.library.DocumentOptionType;

/**
 * Created by SG0891787 on 9/13/2017.
 */

public class DocumentOptionView extends LinearLayout {

//    public static DocumentOptionView inflate(LayoutInflater inflater,DocumentOptionDefinition optionDefinition)
//    {
//        DocumentOptionView optionView = null;
//        switch (optionDefinition.getType())
//        {
//            case STRING:
//                optionView = (DocumentOptionView) inflater.inflate(R.layout.profile_option_string, null);
//                optionView.mOptionType = DocumentOptionType.STRING;
//                optionView.mOptionCode = optionDefinition.getCode();
//                optionView.setTitle(optionDefinition.getTitle());
//                optionView.setValue(optionDefinition.getDefaultValue());
//
//                break;
//
//            case DAYDATE:
//                optionView = (DocumentOptionView) inflater.inflate(R.layout.profile_option_daydate, null);
//                optionView.mOptionType = DocumentOptionType.DAYDATE;
//                optionView.mOptionCode = optionDefinition.getCode();
//                optionView.setTitle(optionDefinition.getTitle());
//                optionView.setValue(optionDefinition.getDefaultValue());
//                break;
//        }
//        return optionView;
//    }

    DocumentOptionType mOptionType;
    String mOptionCode;

    public DocumentOptionView(Context context) {
        super(context);
    }

    public DocumentOptionView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public DocumentOptionView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public DocumentOptionView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void setTitle(String title)
    {
        TextView optionTitleLabel = getTitleBox();
        optionTitleLabel.setText(title);
    }

    private TextView getTitleBox() {
        return (TextView) findViewById(R.id.optionTitle);
    }

    public void setValue(String value)
    {
        EditText optionValueBox = getValueBox();
        optionValueBox.setText(value);
    }

    private EditText getValueBox() {
        return (EditText) findViewById(R.id.optionValueBox);
    }

    public String getValue()
    {
        EditText valueBox = getValueBox();
        return valueBox.getText().toString();
    }

    public String getTitle()
    {
        return getTitleBox().getText().toString();
    }

    public DocumentOptionType getOptionType() {
        return mOptionType;
    }

    public String getOptionCode() {
        return mOptionCode;
    }

}
