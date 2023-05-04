package com.bk.bkskup3.print.widgets;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.bk.btcommon.model.BluetoothAddress;
import com.google.common.base.Strings;

/**
 * Created by IntelliJ IDEA.
 * User: SG0891787
 * Date: 11/10/12
 * Time: 10:02 PM
 */
public class BluetoothDeviceNameView extends RelativeLayout
{
   private TextView mNameView;
   private BluetoothAdapter mBtAdapter;
   private BluetoothAddress mBtAddress;

   public BluetoothDeviceNameView(Context context)
   {
      this(context,null);
   }

   public BluetoothDeviceNameView(Context context, AttributeSet attrs)
   {
      super(context, attrs);
      init(context);
   }

   private void init(Context context)
   {
      mBtAdapter = BluetoothAdapter.getDefaultAdapter();

      mNameView = new TextView(context);
      LayoutParams nameViewLayoutParams = new LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,
                                             ViewGroup.LayoutParams.WRAP_CONTENT);
      nameViewLayoutParams.addRule(ALIGN_PARENT_LEFT);
      nameViewLayoutParams.addRule(CENTER_VERTICAL);
      mNameView.setLayoutParams(nameViewLayoutParams);
      mNameView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);

      setGravity(Gravity.CENTER_VERTICAL);

      addView(mNameView);


   }

   protected void onDetachedFromWindow()
   {
      super.onDetachedFromWindow();
      unregisterReceiver();
   }

   protected void onAttachedToWindow()
   {
      super.onAttachedToWindow();
      registerReceiver();
      resolveDeviceName();
   }

   private void registerReceiver()
   {

   }

   private void unregisterReceiver()
   {

   }

   public void setDeviceAddress(BluetoothAddress btAddress)
   {
      mBtAddress = btAddress;
      mNameView.setText(mBtAddress.getCanonicalForm());
      resolveDeviceName();
   }

   private void resolveDeviceName()
   {
      if(mBtAdapter != null && mBtAdapter.isEnabled() && mBtAddress != null)
      {
         BluetoothDevice bluetoothDevice = mBtAdapter.getRemoteDevice(mBtAddress.getCanonicalForm());
         String btDeviceName = bluetoothDevice.getName();
         if(Strings.isNullOrEmpty(btDeviceName))
         {
            setDeviceName(mBtAddress.getCanonicalForm());
         }
         else
         {
            setDeviceName(btDeviceName);
         }
      }
   }

   private void setDeviceName(String deviceName)
   {
      mNameView.setText(deviceName);
   }


}
