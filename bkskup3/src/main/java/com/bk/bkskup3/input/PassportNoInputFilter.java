package com.bk.bkskup3.input;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;

/**
 * Created by IntelliJ IDEA.
 * User: SG0891787
 * Date: 8/19/12
 * Time: 9:16 PM
 */
public class PassportNoInputFilter implements InputFilter
{
   private Pattern mPattern = Pattern.compile("[A-Z]{2}[0-9]{8}");
   private InputFilter allCapsFilter = new InputFilter.AllCaps();
   @Override
   public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend)
   {
      CharSequence upperCased = allCapsFilter.filter(source,start,end,dest,dstart,dend);
      CharSequence inputResult;

      if(upperCased == null)
         inputResult = change(source,start,end,dest,dstart,dend);
      else
         inputResult = change(upperCased,0,upperCased.length(),dest,dstart,dend);

      Matcher matcher = mPattern.matcher(inputResult);
      if (matcher.matches() || matcher.hitEnd() )
      {
         return upperCased;
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
