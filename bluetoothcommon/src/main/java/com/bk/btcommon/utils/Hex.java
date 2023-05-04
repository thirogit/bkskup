package com.bk.btcommon.utils;


/**
 * Created by IntelliJ IDEA.
 * User: SG0891787
 * Date: 11/11/12
 * Time: 10:25 AM
 */
public class Hex
{
   public static byte[] decodeHex(char[] data)
   {

      int len = data.length;

      if ((len & 0x01) != 0)
      {
         throw new IllegalArgumentException("Odd number of characters.");
      }

      byte[] out = new byte[len >> 1];

      // two characters form the hex value.
      for (int i = 0, j = 0; j < len; i++)
      {
         int f = toDigit(data[j], j) << 4;
         j++;
         f = f | toDigit(data[j], j);
         j++;
         out[i] = (byte) (f & 0xFF);
      }

      return out;
   }

   public static byte decodeByte(char[] hexByte)
   {
      if(hexByte.length != 2)
      {
         throw new IllegalArgumentException("Expecting 2 characters.");
      }

      return decodeHex(hexByte)[0];
   }

   protected static int toDigit(char ch, int index)
   {
      int digit = Character.digit(ch, 16);
      if (digit == -1)
      {
         throw new IllegalArgumentException("Illegal hexadecimal character " + ch + " at index " + index);
      }
      return digit;
   }
   private static final char[] DIGITS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

   public static char[] encodeHex(byte[] data) {
           int l = data.length;
           char[] out = new char[l << 1];
           // two characters form the hex value.
           for (int i = 0, j = 0; i < l; i++) {
               out[j++] = DIGITS[(0xF0 & data[i]) >>> 4];
               out[j++] = DIGITS[0x0F & data[i]];
           }
           return out;
       }

   public static char[] encodeByte(byte data)
   {
      byte[] dataArray = {data};
      return encodeHex(dataArray);
    }
}
