package com.bk.bkskup3.barcode;

import android.app.Activity;
import android.app.ActivityManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.*;
import android.graphics.drawable.AnimationDrawable;
import android.os.*;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.bk.barcode.service.BarcodeService;
import com.bk.barcode.service.BarcodeServiceCommons;
import com.bk.bkskup3.R;
import com.bk.bkskup3.utils.Intents;
import com.bk.bkskup3.work.choice.ChooseScannerActivity;
import com.bk.widgets.LaserView;
import com.bk.btcommon.model.BluetoothAddress;
import com.bk.widgets.actionbar.ActionBar;
import com.google.common.base.Strings;


/**
 * Created by IntelliJ IDEA.
 * User: SG0891787
 * Date: 9/17/12
 * Time: 6:04 PM
 */
public class BarcodeServiceSettingsActivity extends Activity {
    private static final int CHOOSE_SCANNER_RQ_CODE = 1001;
    private static final String CHOOSE_DEV_TAG = "choose_dev";

    private static final String TAG = "BCSrvcSettingsActivity";

    private class StartStopReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BarcodeService.ACTION_SERVICE_STARTED.equals(action)) {
                afterServiceStarted();
            } else if (BarcodeService.ACTION_SERVICE_STOPPED.equals(action)) {
                afterServiceStopped();
            }
        }
    }


    private StartStopReceiver mServiceReceiver = new StartStopReceiver();
    private BluetoothAddress mScannerAddress;
    Messenger mServiceMessenger = null;

    final Messenger mServiceListener = new Messenger(
            new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    switch (msg.what) {
                        case BarcodeServiceCommons.MSG_NOTIFY_CONNECTED:
                            onNotifyConnected();
                            break;
                        case BarcodeServiceCommons.MSG_NOTIFY_CONNECTING:
                            onNotifyConnecting();
                            break;
                        case BarcodeServiceCommons.MSG_NOTIFY_DISCONNECTED:
                            onNotifyDisconnected();
                            break;
                        case BarcodeServiceCommons.MSG_NOTIFY_READ:
                            Bundle msgData = msg.getData();
                            onNotifyRead(msgData.getString(BarcodeServiceCommons.MSG_READ_BARCODE_DATA_KEY));
                            break;
                        case BarcodeServiceCommons.MSG_NOTIFY_CONFIGURATION:
                            onNotifyConfiguration((BluetoothAddress) msg.obj);
                            break;
                        case BarcodeServiceCommons.MSG_NOTIFY_BATTERY:
                            onBattery(msg.arg1, msg.arg2 != 0);
                            break;
                        default:
                            super.handleMessage(msg);
                    }
                }
            });


    private LaserView getLaserView() {
        return (LaserView) findViewById(R.id.laser);
    }

    private TextView getDataView() {
        return (TextView) findViewById(R.id.data);
    }

    private void setScannerOutputVisibility(int visibility) {
        getDataView().setVisibility(visibility);
        getLaserView().setVisibility(visibility);
    }

    private void onNotifyRead(String bc) {
        getDataView().setText(bc);
    }

    private static final int MSG_WHAT_BATTERY_REFRESH = 10001;
    private Handler stateRequestLoop = new Handler() {

    };

    private void onBattery(int batteryLevel, boolean acConnected) {
        showBatteryIndicator(batteryLevel, acConnected);
    }

    private void onNotifyDisconnected() {
        hideBatteryIndicator();
        hideConnectingIndicator();
        setGreyDevice();
        setScannerOutputVisibility(View.INVISIBLE);

    }


    private void hideConnectingIndicator() {
        ImageView statusImg = getStatusImg();
        ((AnimationDrawable) statusImg.getBackground()).stop();
        statusImg.setVisibility(View.INVISIBLE);
    }

    public ImageView getStatusImg() {
        return (ImageView) findViewById(R.id.statusImg);
    }

    private void onNotifyConnected() {
        hideConnectingIndicator();
        setColorDevice();
        requestBatteryStatus();
        setScannerOutputVisibility(View.VISIBLE);
    }

    private void requestBatteryStatus() {
        Message msg = Message.obtain(null, BarcodeServiceCommons.MSG_GET_BATTERY_STATUS);
        msg.replyTo = mServiceListener;
        try {
            mServiceMessenger.send(msg);
        } catch (RemoteException e) {
            Log.e(TAG, "battery status request", e);
        }
    }


    private void showBatteryIndicator(int batteryLevel, boolean acConnected) {
        int batteryImgResId = R.drawable.battery_bad;
        if (batteryLevel == 0) {
            batteryImgResId = R.drawable.battery_good;
        } else if (batteryLevel == 1) {
            batteryImgResId = R.drawable.battery_half;
        }
        ImageView batteryStatusImg = getBatteryStatusImg();
        batteryStatusImg.setImageResource(batteryImgResId);
        batteryStatusImg.setVisibility(View.VISIBLE);
    }

    private void hideBatteryIndicator() {
        getBatteryStatusImg().setVisibility(View.INVISIBLE);
    }

    private ImageView getBatteryStatusImg() {
        return (ImageView) findViewById(R.id.batteryStatusImg);
    }

    private void showConnectionIndicator() {
        ImageView statusImg = getStatusImg();
        statusImg.setBackgroundResource(R.drawable.connecting);
        ((AnimationDrawable) statusImg.getBackground()).start();
        statusImg.setVisibility(View.VISIBLE);
    }

    private void onNotifyConnecting() {
        hideBatteryIndicator();
        showConnectionIndicator();
    }

    private void setGreyDevice() {
        getDeviceImg().setImageResource(R.drawable.bc_scanner_device_disabled);
    }

    private void setColorDevice() {
        getDeviceImg().setImageResource(R.drawable.bc_scanner_device_enabled);
    }

    private TextView getDeviceAddressBox() {
        return (TextView) findViewById(R.id.deviceAddressBox);
    }

    private String getBtDeviceName(BluetoothAddress btDevAddr) {
        BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
        if (btAdapter != null && btAdapter.isEnabled()) {
            BluetoothDevice device = btAdapter.getRemoteDevice(btDevAddr.getCanonicalForm());
            return device.getName();
        }
        return null;
    }

    private void onNotifyConfiguration(BluetoothAddress scannerAddr) {
        if (scannerAddr != null) {
            getDeviceNameBox().setText(Strings.nullToEmpty(getBtDeviceName(scannerAddr)));
            getDeviceAddressBox().setText(scannerAddr.getCanonicalForm());
        } else {

        }
    }

    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            mServiceMessenger = new Messenger(service);
            try {
                Message msg = Message.obtain(null, BarcodeServiceCommons.MSG_REGISTER_LISTENER);
                msg.replyTo = mServiceListener;
                mServiceMessenger.send(msg);
                Log.d(TAG, "listener register msg sent");
                afterServiceConnected();
            } catch (RemoteException e) {
                Log.e(TAG, "on service connected", e);
            }
        }

        public void onServiceDisconnected(ComponentName className) {
            // This is called when the connection with the service has been unexpectedly disconnected - process crashed.
        }
    };


    private void afterServiceConnected() {
        Message msg = Message.obtain(null, BarcodeServiceCommons.MSG_GET_CONFIGURATION);
        msg.replyTo = mServiceListener;
        sendMessageToService(msg);
    }

    private void afterServiceStopped() {
        enableChooseDevice(false);
        showServiceDisabled();
    }

    private void afterServiceStarted() {
        enableChooseDevice(true);
        doBindService();
        showServiceEnabled();
    }

    private void enableChooseDevice(boolean enabled) {
        getBar().enableAction(CHOOSE_DEV_TAG, enabled);
    }

    TextView getDeviceNameBox() {
        return (TextView) findViewById(R.id.deviceNameBox);
    }

    protected void onStart() {
        super.onStart();

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BarcodeService.ACTION_SERVICE_STARTED);
        intentFilter.addAction(BarcodeService.ACTION_SERVICE_STOPPED);
        registerReceiver(mServiceReceiver, intentFilter);

        if (isServiceRunning()) {
            doBindService();
            showServiceEnabled();
        } else {
            showServiceDisabled();
        }
    }

    protected void onStop() {
        doUnbindService();
        unregisterReceiver(mServiceReceiver);
        super.onStop();
    }


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.service_settings);
        ActionBar actionBar = getBar();
        actionBar.setTitle(getTitle());

        ActionBar.TextAction finishAction = new ActionBar.TextAction(R.string.menuFinishBtnCaption,
                new ActionBar.ActionListener() {
                    @Override
                    public void onAction() {
                        finish();
                    }
                });

        ActionBar.ImageAction selectDeviceAction = new ActionBar.ImageAction(CHOOSE_DEV_TAG, R.drawable.ic_bt_config, new ActionBar.ActionListener() {
            @Override
            public void onAction() {
                doSelectDevice();
            }
        });

        actionBar.addAction(selectDeviceAction);
        actionBar.addAction(finishAction);

        enableChooseDevice(isServiceRunning());

    }

    private ActionBar getBar() {
        return (ActionBar) findViewById(com.bk.btcommon.R.id.actionBar);
    }

    private ImageView getDeviceImg() {
        return (ImageView) findViewById(R.id.deviceImg);
    }

    private void doSelectDevice() {
        Intent changePrinter = new Intent(this, ChooseScannerActivity.class);
        if (mScannerAddress != null) {
            changePrinter.putExtra(ChooseScannerActivity.EXTRA_SELECTED_DEVICE_ADDRESS, mScannerAddress);
        }
        startActivityForResult(changePrinter, CHOOSE_SCANNER_RQ_CODE);
    }

    private void showServiceEnabled() {
        findViewById(R.id.serviceEnabled).setVisibility(View.VISIBLE);
        findViewById(R.id.serviceDisabled).setVisibility(View.INVISIBLE);
    }

    private void showServiceDisabled() {
        findViewById(R.id.serviceDisabled).setVisibility(View.VISIBLE);
        findViewById(R.id.serviceEnabled).setVisibility(View.INVISIBLE);
    }

    private boolean isServiceRunning() {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo runningService : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (BarcodeService.class.getName().equals(runningService.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CHOOSE_SCANNER_RQ_CODE) {
            if (resultCode == RESULT_OK) {
                onScannerSelected((BluetoothAddress) data.getParcelableExtra(ChooseScannerActivity.EXTRA_SELECTED_DEVICE_ADDRESS));
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void onScannerSelected(BluetoothAddress address) {
        Message msgSetCfg = Message.obtain(null, BarcodeServiceCommons.MSG_SET_CONFIGURATION);
        Bundle data = new Bundle();
        data.putParcelable(BarcodeServiceCommons.MSG_BT_ADDRESS_DATA_KEY,address);
        msgSetCfg.setData(data);
        sendMessageToService(msgSetCfg);

        Message msgGetCfg = Message.obtain(null, BarcodeServiceCommons.MSG_GET_CONFIGURATION);
        msgGetCfg.replyTo = mServiceListener;
        sendMessageToService(msgGetCfg);
    }


    private void sendMessageToService(Message msg) {
        if (mServiceMessenger != null) {
            try {
                mServiceMessenger.send(msg);
            } catch (RemoteException e) {
                Log.e(TAG, "send message, what=" + msg.what, e);
            }
        }
    }

    void doBindService() {
        bindService(Intents.makeExplicit(this,new Intent(BarcodeService.class.getName())), mConnection, Context.BIND_AUTO_CREATE);
    }

    void doUnbindService() {
        if (mServiceMessenger != null) {
            setGreyDevice();
            hideConnectingIndicator();
            hideBatteryIndicator();
            try {
                Message msg = Message.obtain(null, BarcodeServiceCommons.MSG_UNREGISTER_LISTENER);
                msg.replyTo = mServiceListener;
                mServiceMessenger.send(msg);
                Log.d(TAG, "listener unregister msg sent");
            } catch (RemoteException e) {
                Log.e(TAG, "unbind service", e);
            }
            unbindService(mConnection);
            mServiceMessenger = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        doUnbindService();
    }


}
