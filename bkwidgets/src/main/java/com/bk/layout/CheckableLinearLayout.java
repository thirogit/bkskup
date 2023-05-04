package com.bk.layout;

import android.R;
import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.Checkable;
import android.widget.LinearLayout;

public class CheckableLinearLayout extends LinearLayout implements Checkable
{
   private boolean  mCheck = false;
   private static final int[] STATE_CHECKED = {R.attr.state_checked};

   public CheckableLinearLayout(Context context, AttributeSet attrs)
   {
      super(context, attrs);
   }

   @Override
   protected void onFinishInflate()
   {
      super.onFinishInflate();
      setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
   }

   @Override
   public boolean isChecked()
   {
      return mCheck;
   }

   @Override
   public void setChecked(boolean checked)
   {
      mCheck = checked;
      refreshDrawableState();
   }


   @Override
   protected int[] onCreateDrawableState(int extraSpace)
   {
      if (isChecked())
      {
         // We are going to add 1 extra state.
         final int[] drawableState = super.onCreateDrawableState(extraSpace + 1);

         mergeDrawableStates(drawableState, STATE_CHECKED);

         return drawableState;
      }
      else
      {
         return super.onCreateDrawableState(extraSpace);
      }
   }

   @Override
   public void toggle()
   {
      mCheck = !mCheck;
   }
}