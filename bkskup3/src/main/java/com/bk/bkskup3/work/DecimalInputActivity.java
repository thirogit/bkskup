package com.bk.bkskup3.work;

import android.app.Activity;
import android.content.Intent;
import android.text.InputFilter;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.bk.bkskup3.R;
import com.bk.bkskup3.feedback.ErrorToast;
import com.bk.bkskup3.input.DecimalInputFilter;
import com.bk.bkskup3.utils.Numbers;
import com.bk.widgets.actionbar.ActionBar;

/**
 * Created by IntelliJ IDEA.
 * User: SG0891787
 * Date: 12/2/12
 * Time: 11:56 AM
 */
public class DecimalInputActivity extends Activity
{
   public static final String EXTRA_VALUE = "value";
   public static final String EXTRA_PRECISION = "precision";
   public static final String EXTRA_TITLE = "title";

   private static final int DEFAULT_PRECISION = 7;

   class DigitClickListener implements View.OnClickListener
   {
      private int digitKeyCode;

      DigitClickListener(int digitKeyCode)
      {
         this.digitKeyCode = digitKeyCode;
      }

      @Override
      public void onClick(View v)
      {
         onDigitClick(digitKeyCode);
      }
   }

   protected void onCreate(android.os.Bundle savedInstanceState)
   {
      super.onCreate(savedInstanceState);

      setContentView(R.layout.decimal_input);

      Button clearBtn = (Button) findViewById(R.id.clear);
      clearBtn.setOnClickListener(new View.OnClickListener()
      {
         @Override
         public void onClick(View v)
         {
            onClear();
         }
      });

      Button backspaceBtn = (Button) findViewById(R.id.backspace);
      backspaceBtn.setOnClickListener(new DigitClickListener(KeyEvent.KEYCODE_DEL));

      Button digit0Btn = (Button) findViewById(R.id.digit0);
      digit0Btn.setOnClickListener(new DigitClickListener(KeyEvent.KEYCODE_0));

      Button digit1Btn = (Button) findViewById(R.id.digit1);
      digit1Btn.setOnClickListener(new DigitClickListener(KeyEvent.KEYCODE_1));

      Button digit2Btn = (Button) findViewById(R.id.digit2);
      digit2Btn.setOnClickListener(new DigitClickListener(KeyEvent.KEYCODE_2));

      Button digit3Btn = (Button) findViewById(R.id.digit3);
      digit3Btn.setOnClickListener(new DigitClickListener(KeyEvent.KEYCODE_3));

      Button digit4Btn = (Button) findViewById(R.id.digit4);
      digit4Btn.setOnClickListener(new DigitClickListener(KeyEvent.KEYCODE_4));

      Button digit5Btn = (Button) findViewById(R.id.digit5);
      digit5Btn.setOnClickListener(new DigitClickListener(KeyEvent.KEYCODE_5));

      Button digit6Btn = (Button) findViewById(R.id.digit6);
      digit6Btn.setOnClickListener(new DigitClickListener(KeyEvent.KEYCODE_6));

      Button digit7Btn = (Button) findViewById(R.id.digit7);
      digit7Btn.setOnClickListener(new DigitClickListener(KeyEvent.KEYCODE_7));

      Button digit8Btn = (Button) findViewById(R.id.digit8);
      digit8Btn.setOnClickListener(new DigitClickListener(KeyEvent.KEYCODE_8));

      Button digit9Btn = (Button) findViewById(R.id.digit9);
      digit9Btn.setOnClickListener(new DigitClickListener(KeyEvent.KEYCODE_9));

      Button comaBtn = (Button) findViewById(R.id.coma);
      comaBtn.setOnClickListener(new DigitClickListener(KeyEvent.KEYCODE_PERIOD));

      ActionBar actionBar = (ActionBar) findViewById(R.id.actionBar);
      ActionBar.TextAction okAction = new ActionBar.TextAction(R.string.ok);
      okAction.setListener(new ActionBar.ActionListener()
      {
         @Override
         public void onAction()
         {
            onOk();
         }
      });

      ActionBar.TextAction cancelAction = new ActionBar.TextAction(R.string.cancel);
      cancelAction.setListener(new ActionBar.ActionListener()
      {
         @Override
         public void onAction()
         {
            onCancel();
         }
      });

      actionBar.addAction(okAction);
      actionBar.addAction(cancelAction);
      Intent intent = getIntent();

      CharSequence title = intent.getStringExtra(EXTRA_TITLE);
      if(title == null)
         title = getTitle();

      actionBar.setTitle(title);

      int precision = intent.getIntExtra(EXTRA_PRECISION,-1);

      EditText inputBox = getInputBox();
      if(precision <= 0)
      {
         precision = DEFAULT_PRECISION;
      }

      inputBox.setFilters(new InputFilter[]{new DecimalInputFilter(precision)});

      if(intent.hasExtra(EXTRA_VALUE))
      {
         double value = intent.getDoubleExtra(EXTRA_VALUE,0.0);
         String text = Numbers.formatWithPrecision(value, precision);
         inputBox.setText(text);
         inputBox.setSelection(0,text.length());

      }
   }

   private void onClear()
   {
      getInputBox().getText().clear();
   }

   EditText getInputBox()
   {
      return (EditText)findViewById(R.id.inputBox);
   }

   void onDigitClick(int digitKeyCode)
   {
      EditText inputBox = getInputBox();
      inputBox.onKeyDown(digitKeyCode, new KeyEvent(KeyEvent.ACTION_DOWN, digitKeyCode));
      inputBox.onKeyUp(digitKeyCode,new KeyEvent(KeyEvent.ACTION_UP,digitKeyCode));
   }

   Double getValue()
   {
      EditText inputBox = getInputBox();
      String valueText = inputBox.getText().toString();

      if(valueText.length() > 0)
      {
         return Double.parseDouble(valueText);
      }
      return null;
   }

   void onOk()
   {
      Double value = getValue();

      if(value != null)
      {
         finishWithValue(value);
      }
      else
      {
           new ErrorToast(this).show(R.string.errNoValueProvided);
      }

   }

   void finishWithValue(double value)
   {
      Intent resultIntent = new Intent();
      resultIntent.putExtra(EXTRA_VALUE, value);
      setResult(RESULT_OK,resultIntent);
      finish();
   }

   void onCancel()
   {
      setResult(RESULT_CANCELED);
      finish();
   }


}

