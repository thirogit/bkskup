package com.bk.barcode.service;

/**
 * Created by IntelliJ IDEA.
 * User: SG0891787
 * Date: 9/5/12
 * Time: 11:33 AM
 */

import java.util.ArrayList;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.preference.PreferenceManager;
import android.util.Log;
import com.bk.barcode.R;
import com.bk.btcommon.model.BluetoothAddress;

import static com.bk.barcode.service.BarcodeServiceCommons.*;

public class BarcodeService extends Service {
    private static final String TAG = "BarcodeService";

    enum ConnectionState {
        CONNECTING,
        CONNECTED,
        DISCONNECTED
    }

    ArrayList<Messenger> mListeners = new ArrayList<Messenger>();

    final Messenger mMessenger = new Messenger(new IncomingHandler());
    private NotificationManager mNM;
    private static int BARCODE_NOTIFICATIONS_ID = 1001;
    private ConnectionState mConnectionState;

    private BarcodeServiceWorker mService = new BarcodeServiceWorker();

    public static final String ACTION_SERVICE_STARTED = "com.bk.barcodeservice.action.SERVICE_STARTED";
    public static final String ACTION_SERVICE_STOPPED = "com.bk.barcodeservice.action.SERVICE_STOPPED";
    private static final String KEY_SCANNER_BT_ADDR = "scanner_bt_address";
    private static final int CONNECTION_DISPOSAL_DELAY_SEC = 10 * 1000;
    private static final int RECONNECT_DELAY_SEC = 5 * 1000;

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
                int newBtAdapterState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);

                if (newBtAdapterState == BluetoothAdapter.STATE_ON) {
                    onAfterBluetoothTurnedOn();
                }
                if (newBtAdapterState == BluetoothAdapter.STATE_TURNING_OFF) {
                    onBeforeBluetoothTurnedOff();
                }

            }
        }
    };

    private void onBeforeBluetoothTurnedOff() {
        Log.d(TAG, "disconnecting before bluetooth turn off");
        disconnect();
    }

    private void onAfterBluetoothTurnedOn() {
        ensureConnection();
    }


    @Override
    public IBinder onBind(Intent intent) {
        return mMessenger.getBinder();
    }

    class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_REGISTER_LISTENER:
                    Log.d(TAG, "register listener: " + msg.replyTo.toString());
                    mListeners.add(msg.replyTo);
                    onGetState(msg.replyTo);
                    ensureConnection();
                    break;
                case MSG_UNREGISTER_LISTENER:
                    Log.d(TAG, "unregister listener: " + msg.replyTo.toString());
                    mListeners.remove(msg.replyTo);
                    disposeConnectionIfNeeded();
                    break;
                case MSG_GET_CONFIGURATION:
                    onGetConfiguration(msg.replyTo);
                    break;
                case MSG_SET_CONFIGURATION:
                    Bundle msgData = msg.getData();
                    if(msgData != null) {
                        msgData.setClassLoader(BluetoothAddress.class.getClassLoader());
                        BluetoothAddress address = msgData.getParcelable(MSG_BT_ADDRESS_DATA_KEY);
                        if(address != null) {
                            onSetConfiguration(address);
                        }
                    }
                    break;
                case MSG_GET_STATE:
                    onGetState(msg.replyTo);
                    break;
                case MSG_GET_BATTERY_STATUS:
                    onGetBatteryStatus(msg.replyTo);
                default:
                    super.handleMessage(msg);
            }
        }
    }

    private static final int MSG_CONNECTION_DISPOSE = 1;
    private Handler mConnectionDisposeHandler = new Handler() {
        public void handleMessage(Message msg) {
            Log.d(TAG, "disposing connection");
            disconnect();
        }
    };


    private static final int MSG_RECONNECT = 1;
    private Handler mReconnectHandler = new Handler() {
        public void handleMessage(Message msg) {
            Log.d(TAG, "reconnecting");
            connect();
        }
    };

    private void onGetBatteryStatus(Messenger replyTo) {
        //notifyBatteryStatus(mService.getBatteryStatus(), replyTo);
    }

    private void disposeConnectionIfNeeded() {

        if (mListeners.size() == 0) {
            if (mConnectionState != ConnectionState.DISCONNECTED) {
                Log.d(TAG, "scheduling connection disposal in " + CONNECTION_DISPOSAL_DELAY_SEC + "ms");
                mConnectionDisposeHandler.sendEmptyMessageDelayed(MSG_CONNECTION_DISPOSE, CONNECTION_DISPOSAL_DELAY_SEC);
            }
        }
    }

    private void ensureConnection() {
        if (mListeners.size() > 0) {
            if (mConnectionState == ConnectionState.DISCONNECTED) {
                Log.e(TAG, "connecting");
                connect();
            } else {
                Log.d(TAG, "canceling connection disposal");
                mConnectionDisposeHandler.removeMessages(MSG_CONNECTION_DISPOSE);
            }
        }
    }

    private void onGetState(Messenger replyTo) {
        switch (mConnectionState) {
            case CONNECTED:
                onNotifyConnected(replyTo);
                break;
            case CONNECTING:
                onNotifyConnecting(replyTo);
                break;
            case DISCONNECTED:
                onNotifyDisconnected(replyTo);
                break;
        }
    }

    private void notifyBatteryStatus(BatteryStatus batteryStatus, Messenger replyTo) {
        Message message = Message.obtain(null, MSG_NOTIFY_BATTERY);
        message.arg1 = batteryStatus.getBatteryLevel().ordinal();
        message.arg2 = batteryStatus.isACOn() ? 1 : 0;
        try {
            if (replyTo != null) {
                replyTo.send(message);
            }
        } catch (RemoteException e) {

        }
    }

    private void onGetConfiguration(Messenger replyTo) {
        Message message = Message.obtain();
        Bundle msgData = new Bundle();
        msgData.putParcelable(MSG_READ_BARCODE_DATA_KEY,readScannerAddress());
        message.setData(msgData);
        message.what = MSG_NOTIFY_CONFIGURATION;
        try {
            if (replyTo != null) {
                replyTo.send(message);
            }
        } catch (RemoteException e) {

        }
    }

    private void onSetConfiguration(BluetoothAddress address) {
        saveScannerAddress(address);
        if (mConnectionState != ConnectionState.DISCONNECTED) {
            disconnect();
        }
        connect();
    }

    private void disconnect() {
        mService.requestDisconnect();
        mConnectionState = ConnectionState.DISCONNECTED;
    }

    private void tryDelayedReconnect() {
        if (mConnectionState == ConnectionState.DISCONNECTED) {
            mReconnectHandler.sendEmptyMessageDelayed(MSG_RECONNECT, RECONNECT_DELAY_SEC);
        }
    }

    private void connect() {
        mReconnectHandler.removeMessages(MSG_RECONNECT);
        BluetoothAddress btScannerAddress = readScannerAddress();
        if (btScannerAddress != null) {
            mService.requestConnect(btScannerAddress);
        }
    }

    private ServiceObserver mObserver = new ForwardServiceObserver() {
        @Override
        public void notifyConnecting() {
            mConnectionState = ConnectionState.CONNECTING;
            onNotifyConnecting(null);
        }

        @Override
        public void notifyConnected() {
            mConnectionState = ConnectionState.CONNECTED;
            onNotifyConnected(null);
        }

        @Override
        public void notifyConnectionFailed() {
            notifyDisconnected();
            tryDelayedReconnect();
        }

        @Override
        public void notifyDisconnected() {
            mConnectionState = ConnectionState.DISCONNECTED;
            onNotifyDisconnected(null);
        }

        @Override
        public void notifyBarcode(String barcode) {
            onNotifyBarcodeRead(barcode);
        }

        @Override
        public void notifyConnectionLost() {
            notifyDisconnected();
            tryDelayedReconnect();
        }


    };

    private void onNotifyDisconnected(Messenger dest) {
        Message msg = Message.obtain(null, MSG_NOTIFY_DISCONNECTED);
        try {
            if (dest != null) {
                dest.send(msg);
            } else {
                sendMsgToListeners(msg);
            }
        } catch (RemoteException e) {
        }
    }

    private void onNotifyConnecting(Messenger dest) {
        Message msg = Message.obtain(null, MSG_NOTIFY_CONNECTING);
        try {
            if (dest != null) {
                dest.send(msg);
            } else {
                sendMsgToListeners(msg);
            }
        } catch (RemoteException e) {
        }
    }

    private void onNotifyConnected(Messenger dest) {
        Message msg = Message.obtain(null, MSG_NOTIFY_CONNECTED);
        try {
            if (dest != null) {
                dest.send(msg);
            } else {
                sendMsgToListeners(msg);
            }
        } catch (RemoteException e) {
        }
    }

    private void onNotifyBarcodeRead(String bc) {
        Message msg = Message.obtain(null, MSG_NOTIFY_READ);
        Bundle msgData = new Bundle();
        msgData.putString(MSG_READ_BARCODE_DATA_KEY, bc);
        msg.setData(msgData);
        sendMsgToListeners(msg);
    }

    private void sendMsgToListeners(Message msg) {
        for (Messenger listener : mListeners) {
            try {
                listener.send(Message.obtain(msg));
            } catch (RemoteException e) {
            }
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mConnectionState = ConnectionState.DISCONNECTED;
        mNM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        mService.startAsync();
        mService.setObserver(mObserver);

        updateNotification();
        broadcastStart();

        IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        this.registerReceiver(mReceiver, filter);

    }

    private String readPreference(String preferenceKey) {
        SharedPreferences preferences = getPreferences();
        return preferences.getString(preferenceKey, null);
    }

    private SharedPreferences getPreferences() {
        return PreferenceManager.getDefaultSharedPreferences(getApplication());
    }

    private void savePreference(String preferenceKey, String prefernceValue) {
        SharedPreferences.Editor editor = getPreferences().edit();
        editor.putString(preferenceKey, prefernceValue);
        editor.commit();
    }

    private BluetoothAddress readScannerAddress() {
        String scannerBtAddr = readPreference(KEY_SCANNER_BT_ADDR);
        if (scannerBtAddr != null && BluetoothAdapter.checkBluetoothAddress(scannerBtAddr)) {
            return new BluetoothAddress(scannerBtAddr);
        }
        return null;
    }

    private void saveScannerAddress(BluetoothAddress scannerBtAddress) {
        String preferenceValue = null;
        if (scannerBtAddress != null) {
            preferenceValue = scannerBtAddress.getCanonicalForm();
        }
        savePreference(KEY_SCANNER_BT_ADDR, preferenceValue);
    }


    void broadcastStart() {
        Intent startIntent = new Intent(ACTION_SERVICE_STARTED);
        sendBroadcast(startIntent);
    }

    void broadcastStop() {
        Intent stopIntent = new Intent(ACTION_SERVICE_STOPPED);
        sendBroadcast(stopIntent);
    }

    private void updateNotification() {
        Notification.Builder builder = new Notification.Builder(this);
        builder.setTicker(getString(R.string.serviceTickerText));
        Resources r = getResources();
        builder.setLargeIcon(BitmapFactory.decodeResource(r, R.drawable.good_barcode_icon));
        builder.setSmallIcon(R.drawable.good_barcode_icon);
        builder.setWhen(System.currentTimeMillis());
        builder.setOngoing(true);
//       builder.setAutoCancel(false);
        builder.setContentText(r.getString(R.string.ntfnContentText));
//       builder.setContentInfo("content info");
        builder.setContentTitle(r.getString(R.string.ntfnContentTitle));


        Context appContext = getApplicationContext();
        PendingIntent contentIntent = PendingIntent.getActivity(appContext, 0, new Intent("com.bk.barcodeservice.action.SHOW_BC_SETTINGS"), 0);
        builder.setContentIntent(contentIntent);

        Notification notification = builder.build();
        mNM.notify(BARCODE_NOTIFICATIONS_ID, notification);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY; // run until explicitly stopped.
    }


    @Override
    public void onDestroy() {
        super.onDestroy();

        mService.stopAsync();
        mService.awaitTerminated();

        mNM.cancel(BARCODE_NOTIFICATIONS_ID);
        unregisterReceiver(mReceiver);
        broadcastStop();
    }
}
