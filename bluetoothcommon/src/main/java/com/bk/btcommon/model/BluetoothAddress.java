package com.bk.btcommon.model;

import java.util.Arrays;

import android.bluetooth.BluetoothAdapter;
import android.os.Parcel;
import android.os.Parcelable;
import com.bk.btcommon.utils.Hex;
import com.bk.btcommon.utils.Split;

/**
 * Created by IntelliJ IDEA.
 * User: SG0891787
 * Date: 11/10/12
 * Time: 10:39 PM
 */
public class BluetoothAddress implements Parcelable
{
   private byte mAddress[];

   public BluetoothAddress(byte address[])
   {
      if (address.length != 6)
      {
         throw new IllegalArgumentException("Bad address size.");
      }
      mAddress = Arrays.copyOf(address, 6);
   }

   public BluetoothAddress(String address)
   {
      if (!BluetoothAdapter.checkBluetoothAddress(address))
      {
         throw new IllegalArgumentException("Invalid address.");
      }

      mAddress = new byte[6];
      String adressParts[] = Split.split(address, ':', true);
      for (int i = 0; i < adressParts.length; i++)
      {
         mAddress[i] = Hex.decodeByte(adressParts[i].toCharArray());
      }
   }


   public byte[] getRawForm()
   {
      return Arrays.copyOf(mAddress, 6);
   }

   public String getCanonicalForm()
   {
      StringBuilder canonicalFrom = new StringBuilder();
      boolean firstTime = true;
      for (byte token : mAddress)
      {
         if (firstTime)
         {
            firstTime = false;
         }
         else
         {
            canonicalFrom.append(':');
         }
         canonicalFrom.append(new String(Hex.encodeByte(token)));
      }
      return canonicalFrom.toString();
   }


   public String toString()
   {
      return getCanonicalForm();
   }

   public static final Parcelable.Creator<BluetoothAddress> CREATOR =
      new Parcelable.Creator<BluetoothAddress>()
      {
         public BluetoothAddress createFromParcel(Parcel in)
         {
            byte btAddr[] = new byte[6];
            in.readByteArray(btAddr);
            return new BluetoothAddress(btAddr);
         }

         public BluetoothAddress[] newArray(int size)
         {
            return new BluetoothAddress[size];
         }
      };

   @Override
   public int describeContents()
   {
      return 0;
   }

   @Override
   public void writeToParcel(Parcel out, int flags)
   {
      out.writeByteArray(mAddress);
   }
}
