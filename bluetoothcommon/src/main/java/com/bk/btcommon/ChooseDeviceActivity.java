package com.bk.btcommon;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.bk.btcommon.model.BluetoothAddress;
import com.bk.btcommon.model.BluetoothDeviceDescriptor;
import com.bk.widgets.actionbar.ActionBar;

/**
 * Created by IntelliJ IDEA.
 * User: SG0891787
 * Date: 2/24/12
 * Time: 8:41 PM
 */
public class ChooseDeviceActivity extends Activity
{
   public static final String EXTRA_SELECTED_DEVICE_ADDRESS = "selected_device_address";
   public static final String EXTRA_SELECTED_DEVICE = "selected_device";
   public static final String EXTRA_DEVICE_NAME_WILDCARD_FILTER = "device_name_wildcard_filter";
   private static final int REQUEST_PERMISSION_CODE = 1001;

   private Toast noDeviceSelectedToast;
   private BluetoothAdapter mBtAdapter;
   private ArrayAdapter<BluetoothDevice> mDeviceListAdapter;
   private ListView mDevicesList;
   private ImageView mNoBluetoothImgView;
   private TextView mNoDevicesTxt;
   private View mScanningInProgressView;
   private View mEmptyFrame;

   private boolean mScanPending;

   ActionBar.TextAction okAction = new ActionBar.TextAction(R.string.ok,
                                                            new ActionBar.ActionListener()
                                                            {
                                                               @Override
                                                               public void onAction()
                                                               {
                                                                  onOK();
                                                               }
                                                            });

   ActionBar.TextAction cancelAction = new ActionBar.TextAction(  R.string.cancel,
                                                                  new ActionBar.ActionListener()
                                                                  {
                                                                     @Override
                                                                     public void onAction()
                                                                     {
                                                                        onCancel();
                                                                     }
                                                                  });

   ActionBar.TextAction scanAction = new ActionBar.TextAction( R.string.scan,
                                                               new ActionBar.ActionListener()
                                                               {
                                                                  @Override
                                                                  public void onAction()
                                                                  {
                                                                     onScan();
                                                                  }
                                                               });


   ActionBar.TextAction stopAction = new ActionBar.TextAction(R.string.stop,new ActionBar.ActionListener()
   {
      @Override
      public void onAction()
      {
         onStopScan();
      }
   });


   private final BroadcastReceiver mReceiver = new BroadcastReceiver()
   {
      @Override
      public void onReceive(Context context, Intent intent)
      {
         String action = intent.getAction();

         if (BluetoothDevice.ACTION_FOUND.equals(action))
         {
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            onFoundDevice(device);
         }
         else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action))
         {
            onScanningFinished();
         }
         else if(BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action))
         {
            onScanningStarted();
         }
         else if(BluetoothAdapter.ACTION_STATE_CHANGED.equals(action))
         {
            int newBtAdapterState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
//            int oldBtAdapterState = intent.getIntExtra(BluetoothAdapter.EXTRA_PREVIOUS_STATE, BluetoothAdapter.ERROR);

            if(newBtAdapterState == BluetoothAdapter.STATE_ON)
            {
               onAfterBluetoothEnabled();
            }
            if(newBtAdapterState == BluetoothAdapter.STATE_TURNING_OFF)
            {
               onBeforeBluetoothDisabled();
            }

         }
      }
   };

   private void onBeforeBluetoothDisabled()
   {
      cancelDiscovery();
      showNoBluetoothAvailable();
   }

   private void onAfterBluetoothEnabled()
   {
      showBtDevicesList();
   }


   private void showNoBluetoothAvailable()
   {
      mDevicesList.setVisibility(View.GONE);
      mNoDevicesTxt.setVisibility(View.GONE);
      mScanningInProgressView.setVisibility(View.GONE);
      mNoBluetoothImgView.setVisibility(View.VISIBLE);
      rebuildActionBar();
   }

   private void showBtDevicesList()
   {
      mDevicesList.setVisibility(View.VISIBLE);
      mDeviceListAdapter.notifyDataSetChanged();
      mNoBluetoothImgView.setVisibility(View.GONE);
      rebuildActionBar();

   }


   @Override
   public void onCreate(Bundle savedInstanceState)
   {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.device_discovery);
      setResult(Activity.RESULT_CANCELED);

      mBtAdapter = BluetoothAdapter.getDefaultAdapter();


      mDeviceListAdapter = new ArrayAdapter<BluetoothDevice>(this, R.layout.bt_device_list_item, R.id.btDeviceNameLbl)
      {
         public View getView(int position, View convertView, ViewGroup parent)
         {
            View row;
            if (null == convertView)
            {
               LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
               row = inflater.inflate(R.layout.bt_device_list_item, null);
            }
            else
            {
               row = convertView;
            }

            TextView btDeviceNameLbl = (TextView) row.findViewById(R.id.btDeviceNameLbl);
            TextView btDeviceAddrLbl = (TextView) row.findViewById(R.id.btDeviceAddrLbl);
            BluetoothDevice btDeviceItem = getItem(position);

            btDeviceNameLbl.setText(btDeviceItem.getName());

            btDeviceAddrLbl.setText(btDeviceItem.getAddress());

            return row;
         }
      };

      ActionBar actionBar = getBar();
      actionBar.setTitle(getTitle());

      mDevicesList = (ListView) findViewById(R.id.devicesList);
      mNoBluetoothImgView = (ImageView) findViewById(R.id.noBluetoohImg);
      mEmptyFrame = findViewById(R.id.emptyFrame);
      mNoDevicesTxt = (TextView) findViewById(R.id.noDevicesTxt);
      mScanningInProgressView = findViewById(R.id.scanningInProgress);

      Resources r = getResources();
      Drawable[] layers = new Drawable[2];
      layers[0] = r.getDrawable(R.drawable.bluetooth_icon);
      layers[1] = r.getDrawable(R.drawable.forbidden_icon);
      LayerDrawable layerDrawable = new LayerDrawable(layers);
      mNoBluetoothImgView.setImageDrawable(layerDrawable);

      mDevicesList.setAdapter(mDeviceListAdapter);

      mDevicesList.setOnItemClickListener(new AdapterView.OnItemClickListener()
      {
         @Override
         public void onItemClick(AdapterView<?> parent, View view, int position, long id)
         {
            rebuildActionBar();
         }
      });

      mDevicesList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

      Intent intent = getIntent();
      BluetoothAddress selectedBtDeviceAddr = intent.getParcelableExtra(EXTRA_SELECTED_DEVICE_ADDRESS);
      if(selectedBtDeviceAddr != null)
      {
         BluetoothDevice selectedBtDevice = mBtAdapter.getRemoteDevice(selectedBtDeviceAddr.getCanonicalForm());
         if(selectedBtDevice != null)
         {
            mDeviceListAdapter.add(selectedBtDevice);
            mDevicesList.setItemChecked(0, true);
         }
      }

       mDevicesList.setEmptyView(mEmptyFrame);
       mScanningInProgressView.setVisibility(View.GONE);
       mNoDevicesTxt.setVisibility(View.VISIBLE);

      if (mBtAdapter == null || !mBtAdapter.isEnabled())
      {
         showNoBluetoothAvailable();
      }
      else
      {
         showBtDevicesList();
      }

      noDeviceSelectedToast = Toast.makeText(this, getString(R.string.errNoDeviceSelected), Toast.LENGTH_SHORT);
      noDeviceSelectedToast.getView().setBackgroundColor(Color.RED);

      IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
      this.registerReceiver(mReceiver, filter);

      filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
      this.registerReceiver(mReceiver, filter);

      filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
      this.registerReceiver(mReceiver,filter);

      filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
      this.registerReceiver(mReceiver,filter);

      mScanPending = false;
      rebuildActionBar();

   }

   private ActionBar getBar()
   {
      return (ActionBar) findViewById(R.id.actionBar);
   }

   private FrameLayout getFrame()
   {
      return (FrameLayout) findViewById(R.id.frame);
   }

   @Override
   protected void onDestroy()
   {
      super.onDestroy();

      cancelDiscovery();

      this.unregisterReceiver(mReceiver);
   }

   private void rebuildActionBar()
   {
      ActionBar actionBar = getBar();
      actionBar.removeAllActions();

      if(!mScanPending)
      {
         if(isDiscovering())
         {
            actionBar.addAction(stopAction);
         }
         else
         {
            if(isBluetoothAvailable())
            {
               actionBar.addAction(scanAction);
            }
         }
      }

      if(mDevicesList.getCheckedItemCount() > 0)
      {
         actionBar.addAction(okAction);
      }

      actionBar.addAction(cancelAction);

   }

   private boolean isBluetoothAvailable()
   {
      return mBtAdapter != null && mBtAdapter.isEnabled();
   }

   private void onScan()
   {
      doDiscovery();
      mScanPending = true;
      rebuildActionBar();

   }

   private void onScanningStarted()
   {
      mScanPending = false;
      mScanningInProgressView.setVisibility(View.VISIBLE);
      mNoDevicesTxt.setVisibility(View.GONE);
      rebuildActionBar();
   }

   private void onStopScan()
   {
      mScanningInProgressView.setVisibility(View.GONE);
      mNoDevicesTxt.setVisibility(View.VISIBLE);

      cancelDiscovery();
      rebuildActionBar();
   }

   private void onFoundDevice(BluetoothDevice btDevice)
   {
      mDeviceListAdapter.remove(btDevice);
      mDeviceListAdapter.add(btDevice);
   }

   private void onScanningFinished()
   {
      mScanningInProgressView.setVisibility(View.GONE);
      mNoDevicesTxt.setVisibility(View.VISIBLE);
      rebuildActionBar();
   }


   private void onCancel()
   {
      setResult(RESULT_CANCELED);
      noDeviceSelectedToast.cancel();
      finish();
   }

   private void onOK()
   {

      if (mDevicesList.getCheckedItemCount() > 0)
      {
         BluetoothDevice selectedDevice = mDeviceListAdapter.getItem(mDevicesList.getCheckedItemPosition());
         Intent result = new Intent();
         BluetoothAddress selectedDevAddr =  new BluetoothAddress(selectedDevice.getAddress());
         result.putExtra(EXTRA_SELECTED_DEVICE_ADDRESS,selectedDevAddr);

         BluetoothDeviceDescriptor btDescriptor = new BluetoothDeviceDescriptor();
         btDescriptor.setAddress(selectedDevAddr);
         btDescriptor.setName(selectedDevice.getName());
         btDescriptor.setBtClass(selectedDevice.getBluetoothClass());
         result.putExtra(EXTRA_SELECTED_DEVICE,btDescriptor);
         setResult(RESULT_OK, result);
         noDeviceSelectedToast.cancel();
         finish();
      }
      else
      {
         displayNoDeviceSelectedToast();
      }
   }

   private void displayNoDeviceSelectedToast()
   {
      noDeviceSelectedToast.show();
   }

   private boolean isDiscovering()
   {
      return mBtAdapter != null && mBtAdapter.isDiscovering();
   }


   private void doDiscovery()
   {
      if(mBtAdapter != null)
      {
         if(mBtAdapter.isDiscovering())
         {
            mBtAdapter.cancelDiscovery();
         }


         if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {  // Only ask for these permissions on runtime when running Android 6.0 or higher
            switch (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)) {

               case PackageManager.PERMISSION_DENIED:
                   askForDiscoveryPermission();
                  break;
               case PackageManager.PERMISSION_GRANTED:
                  mBtAdapter.startDiscovery();
                  break;
            }
         }
         else
         {
            mBtAdapter.startDiscovery();
         }




      }
   }

   @Override
   public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
      super.onRequestPermissionsResult(requestCode, permissions, grantResults);

      switch (requestCode) {
         case REQUEST_PERMISSION_CODE:
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
               mBtAdapter.startDiscovery();
            }
            break;
      }
   }

   private void askForDiscoveryPermission() {
      ActivityCompat.requestPermissions(this,
              new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
              REQUEST_PERMISSION_CODE);
   }

   private void cancelDiscovery()
   {
      if (mBtAdapter != null)
      {
         mBtAdapter.cancelDiscovery();
      }
   }




}
