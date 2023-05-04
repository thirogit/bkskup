package com.bk.bkskup3.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ToggleButton;
import com.bk.bkskup3.R;

/**
 * Created by IntelliJ IDEA.
 * User: SG0891787
 * Date: 3/26/13
 * Time: 7:59 PM
 */
public class ToggleListView extends HorizontalScrollView
{
   LinearLayout mInnerLayout;
   ToggleButton mCheckedBtn;

   public ToggleListView(Context context)
   {
      super(context);
      initLayout();
   }

    public ToggleListView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        initLayout();
    }

   private void initLayout()
   {
      mInnerLayout = new LinearLayout(getContext());
      LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                                                 ViewGroup.LayoutParams.MATCH_PARENT);
      mInnerLayout.setLayoutParams(layoutParams);

      mInnerLayout.setOrientation(LinearLayout.HORIZONTAL);
      addView(mInnerLayout);
   }



   public void addItem(String item)
   {
      ToggleButton btn = new ToggleButton(getContext());

      btn.setTextOff(item);
      btn.setTextOn(item);
      btn.setChecked(false);
      btn.setTextColor(0xFFFFFFFF);

      btn.setSingleLine(true);
      btn.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.btn_toggle));
      btn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
      {
         public void onCheckedChanged(CompoundButton btnView, boolean isChecked)
         {
            ToggleButton toggleButton = (ToggleButton) btnView;


            if(isChecked)
            {
               if (mCheckedBtn != null && mCheckedBtn != btnView)
               {
                  mCheckedBtn.setChecked(false);
               }
               mCheckedBtn = toggleButton;
            }
            else
            {
               mCheckedBtn = null;
            }
         }
      });

      btn.setMinWidth(100);
      mInnerLayout.addView(btn,ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
   }

    public void clearItems()
    {
        mInnerLayout.removeAllViews();
    }

   public String getToggledItem()
   {
      if(mCheckedBtn != null)
      {
         return mCheckedBtn.getText().toString();
      }
      return null;
   }

   public void toggleItem(String item)
   {
      if (mCheckedBtn != null)
      {
         mCheckedBtn.setChecked(false);
      }
      mCheckedBtn = null;

      for(int ichild  = 0;ichild < mInnerLayout.getChildCount();ichild++)
      {
         ToggleButton childToggle = (ToggleButton) mInnerLayout.getChildAt(ichild);
         if(childToggle.getText().equals(item))
         {
            mCheckedBtn = childToggle;
            mCheckedBtn.setChecked(true);

            break;
         }
      }
   }

}
