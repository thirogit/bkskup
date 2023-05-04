package com.bk.bkskup3.barcode;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.*;

import com.bk.bkskup3.utils.Intents;

import static com.bk.barcode.service.BarcodeServiceCommons.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: SG0891787
 * Date: 3/9/13
 * Time: 11:43 AM
 */
public class BarcodeServiceClient {
    public interface BarcodeClientObserver {
        void onBarcode(String bc);
        void onState(BarcodeServiceState state);
    }

    private interface ObserverVisitor {
        void visit(BarcodeClientObserver observer);
    }

    private Context mContext;
    private Messenger mServiceMessenger = null;
    private List<BarcodeClientObserver> mObservers = new ArrayList<BarcodeClientObserver>();
    private BarcodeServiceState mState;

    final Messenger mServiceListener = new Messenger(
            new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    switch (msg.what) {
                        case MSG_NOTIFY_CONNECTED:
                            onConnected();
                            break;
                        case MSG_NOTIFY_CONNECTING:
                            onConnecting();
                            break;
                        case MSG_NOTIFY_DISCONNECTED:
                            onDisconnected();
                            break;
                        case MSG_NOTIFY_READ:
                            Bundle msgData = msg.getData();
                            onRead(msgData.getString(MSG_READ_BARCODE_DATA_KEY));
                            break;
                        default:
                            super.handleMessage(msg);
                    }
                }
            });

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            mServiceMessenger = new Messenger(service);
            try {
                Message msg = Message.obtain(null, MSG_REGISTER_LISTENER);
                msg.replyTo = mServiceListener;
                mServiceMessenger.send(msg);
            } catch (RemoteException e) {
                // In this case the service has crashed before we could even do anything with it
                afterServiceCrashed();
            }
        }

        public void onServiceDisconnected(ComponentName className) {
            afterServiceCrashed();
        }
    };

    public BarcodeServiceClient(Context mContext) {
        this.mContext = mContext;
    }

    public void attachObserver(BarcodeClientObserver observer) {
        mObservers.add(observer);
    }

    public void removeObserver(BarcodeClientObserver observer) {
        mObservers.remove(observer);
    }

    private void afterServiceCrashed() {
        beforeServiceDisconnected();
    }

    private void visitObservers(ObserverVisitor visitor) {
        for (BarcodeClientObserver observer : mObservers)
            visitor.visit(observer);
    }

    private void onRead(final String bc) {
        visitObservers(new ObserverVisitor() {
            @Override
            public void visit(BarcodeClientObserver observer) {
                observer.onBarcode(bc);
            }
        });
    }

    private void onConnectionLost() {
        onDisconnected();
    }

    private void onConnectionFailed() {
        onDisconnected();
    }

    private void onDisconnected() {
        mState =  BarcodeServiceState.ScannerNotConnected;
        notifyState();
    }

    void notifyState()
    {
        visitObservers(new ObserverVisitor() {
            @Override
            public void visit(BarcodeClientObserver observer) {
                observer.onState(mState);
            }
        });
    }

    private void onConnecting() {
        mState =  BarcodeServiceState.ScannerConnecting;
        notifyState();
    }

    private void onConnected() {
        mState =  BarcodeServiceState.ScannerConnected;
        notifyState();
    }

    public void start() {
        Intent bindServiceIntent = new Intent("com.bk.barcode.service.BarcodeService");
        mContext.bindService(Intents.makeExplicit(mContext,bindServiceIntent), mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    public void stop() {
        if (mServiceMessenger != null) {
            beforeServiceDisconnected();
            try {
                Message msg = Message.obtain(null, MSG_UNREGISTER_LISTENER);
                msg.replyTo = mServiceListener;
                mServiceMessenger.send(msg);
            } catch (RemoteException e) {
                // There is nothing special we need to do if the service has crashed.
            }
            mContext.unbindService(mServiceConnection);
            mServiceMessenger = null;
            mState = null;
        }
    }

    private void beforeServiceDisconnected() {
        visitObservers(new ObserverVisitor() {
            @Override
            public void visit(BarcodeClientObserver observer) {
                observer.onState(BarcodeServiceState.NoService);
            }
        });
    }

    public BarcodeServiceState getState() {
        return mState;
    }
}
