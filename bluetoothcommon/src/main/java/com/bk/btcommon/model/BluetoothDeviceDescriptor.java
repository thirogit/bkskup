package com.bk.btcommon.model;

import android.bluetooth.BluetoothClass;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by IntelliJ IDEA.
 * User: SG0891787
 * Date: 2/9/13
 * Time: 1:41 PM
 */
public class BluetoothDeviceDescriptor implements Parcelable
{
   private BluetoothAddress address;
   private String name;
   private BluetoothClass btClass;

   public BluetoothAddress getAddress()
   {
      return address;
   }

   public void setAddress(BluetoothAddress address)
   {
      this.address = address;
   }

   public String getName()
   {
      return name;
   }

   public void setName(String name)
   {
      this.name = name;
   }

   public BluetoothClass getBtClass()
   {
      return btClass;
   }

   public void setBtClass(BluetoothClass btClass)
   {
      this.btClass = btClass;
   }

   @Override
   public String toString()
   {
      return
         "address=" + address +
         ", name='" + name + '\'' +
         ", btClass=" + btClass;
   }

   public static final Parcelable.Creator<BluetoothDeviceDescriptor> CREATOR =
         new Parcelable.Creator<BluetoothDeviceDescriptor>()
         {
            public BluetoothDeviceDescriptor createFromParcel(Parcel in)
            {
               BluetoothDeviceDescriptor result = new BluetoothDeviceDescriptor();
               result.name = in.readString();
               result.address = in.readParcelable(BluetoothAddress.class.getClassLoader());
               result.btClass = in.readParcelable(BluetoothClass.class.getClassLoader());
               return result;
            }

            public BluetoothDeviceDescriptor[] newArray(int size)
            {
               return new BluetoothDeviceDescriptor[size];
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
         out.writeString(name);
         out.writeParcelable(address, flags);
         out.writeParcelable(btClass,flags);
      }
}
