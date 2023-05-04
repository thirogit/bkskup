package com.bk.bkskup3.model;

import java.io.Serializable;
import java.util.Arrays;

/**
 * Created by IntelliJ IDEA.
 * User: SG0891787
 * Date: 12/16/11
 * Time: 8:34 PM
 */
public class EAN implements Serializable
{
   private char[] countryCode;
   private char[] number;

   private EAN(char[] countryCode, char[] number)
   {
      this.countryCode = countryCode;
      this.number = number;
   }

   public String getCountryCode()
   {
      return new String(countryCode);
   }

   public String getNumber()
   {
      return new String (number);
   }

   public String toString()
   {
      return getCountryCode() + getNumber();
   }

   public int length()
   {
      return countryCode.length + number.length;
   }

   @Override
   public boolean equals(Object o)
   {
      if (this == o) return true;
      if (o == null || this.getClass() != o.getClass()) return false;

      EAN ean = (EAN) o;
      if (!Arrays.equals(countryCode, ean.countryCode)) return false;
      if (!Arrays.equals(number, ean.number)) return false;

      return true;
   }

   @Override
   public int hashCode()
   {
      int result = countryCode != null ? Arrays.hashCode(countryCode) : 0;
      result = 31 * result + (number != null ? Arrays.hashCode(number) : 0);
      return result;
   }

   public static EAN fromString(String EANStr)
   {
      if(EANStr.length()  > 2)
      {
         char[] EANchars = EANStr.toCharArray();

         if(Character.isLetter(EANchars[0]) && Character.isLetter(EANchars[1]))
         {
            for(int c = 2; c < EANchars.length;c++)
            {
               if(!Character.isDigit(EANchars[c]))
               {
                  return null;
               }
            }
            return new EAN(Arrays.copyOf(EANchars,2),Arrays.copyOfRange(EANchars,2,EANchars.length));
         }

      }
      return null;
   }

}
