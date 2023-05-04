package com.bk.bkskup3.input;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;

/**
 * Created by IntelliJ IDEA.
 * User: SG0891787
 * Date: 8/29/12
 * Time: 3:25 PM
 */
public class RegExInputFilter implements InputFilter
{
   private Pattern mPattern;

   public RegExInputFilter(String regExpression)
   {
      initWithExpression(regExpression);
   }

   protected void initWithExpression(String regExpression)
   {
      mPattern = Pattern.compile(regExpression);
   }


   @Override
   public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend)
   {
      CharSequence inputResult = change(source,start,end,dest,dstart,dend);

      Matcher matcher = mPattern.matcher(inputResult);
      if (matcher.matches() || matcher.hitEnd() )
      {
         return source.subSequence(start,end);
      }
      return "";
   }

   private CharSequence change(CharSequence source, int start, int end, Spanned dest, int dstart, int dend)
   {
      char[] v = new char[end - start];
      TextUtils.getChars(source, start, end, v, 0);
      return TextUtils.concat(dest.subSequence(0, dstart),new String(v),dest.subSequence(dend,dest.length()));
   }
}
