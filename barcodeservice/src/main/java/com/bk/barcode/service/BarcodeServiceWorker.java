package com.bk.barcode.service;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import com.bk.btcommon.model.BluetoothAddress;
import com.google.common.base.Strings;
import com.google.common.util.concurrent.AbstractExecutionThreadService;

/**
 * Created by IntelliJ IDEA.
 * User: SG0891787
 * Date: 3/11/13
 * Time: 3:31 PM
 */
public class BarcodeServiceWorker extends AbstractExecutionThreadService
{
   private static final String TAG = "BarcodeServiceWorker";
   private static final UUID SPP_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
   private static final int READ_DELAY_MS = 50;
   private static final String BARCODEREAD_SIGNAL_BUNDLE_BARCODE_KEY = "barcode";


   private static final int SIGNAL_CONNECTED =        1;
   private static final int SIGNAL_CONNECTIONFAILED = 2;
   private static final int SIGNAL_DISCONNECTED =     3;
   private static final int SIGNAL_BARCODEREAD =      4;
   private static final int SIGNAL_CONNECTIONLOST =   5;
   private static final int SIGNAL_CONNECTING =       7;

   enum RequestType
   {
      Request_Connect,
      Request_Disconnect,
      Request_Read,
   }

   private interface ConnectionSignal
   {
     void fireConnecting();
     void fireConnected();
     void fireConnectionFailed();
     void fireDisconnected();
     void fireBytesRead(byte[] buffer,int length);
     void fireConnectionLost();
   }

   private interface Request
   {
      void execute(ConnectionSignal signal);
   }

   public class RequestWrapper implements Delayed, Request
   {
      private long mEndOfDelay;
      private Request mRq;
      private RequestType mType;

      public RequestWrapper(RequestType type, Request rq, long delayMs)
      {
         this.mEndOfDelay = System.currentTimeMillis() + delayMs;
         this.mRq = rq;
         this.mType = type;
      }

      @Override
      public long getDelay(TimeUnit unit)
      {
         return unit.convert(mEndOfDelay - System.currentTimeMillis(),TimeUnit.MILLISECONDS);

      }

      @Override
      public int compareTo(Delayed o)
      {
         int ret = 0;
         RequestWrapper rq = (RequestWrapper) o;

         if (this.mEndOfDelay < rq.mEndOfDelay)
         {
            ret = -1;
         }
         else if (this.mEndOfDelay > rq.mEndOfDelay)
         {
            ret = 1;
         }
         return ret;
      }

      @Override
      public void execute(ConnectionSignal signal)
      {
         mRq.execute(signal);
      }

      @Override
      public boolean equals(Object o)
      {
         if (this == o) return true;
         if (o == null || getClass() != o.getClass()) return false;

         RequestWrapper wrapper = (RequestWrapper) o;

         return mType == wrapper.mType;

      }
   }
   private class ConnectRequest implements Request
   {
      BluetoothDevice mDeviceToConnectTo;

      private ConnectRequest(BluetoothDevice deviceToConnectTo)
      {
         this.mDeviceToConnectTo = deviceToConnectTo;
      }

      @Override
      public void execute(ConnectionSignal signal)
      {
         if (mBtSocket == null)
         {
            try
            {
               signal.fireConnecting();
               mBtSocket = mDeviceToConnectTo.createInsecureRfcommSocketToServiceRecord(SPP_UUID);
               mBtSocket.connect();
               signal.fireConnected();
            }
            catch (IOException e)
            {
               Log.e(TAG,"connection failure",e);
               signal.fireConnectionFailed();
               mBtSocket = null;
            }
         }
         else
         {
            Log.e(TAG,"already connected");
         }
      }
   }

   private class DisconnectRequest implements Request
   {
      @Override
      public void execute(ConnectionSignal signal)
      {
         if (mBtSocket != null)
         {
            try
            {
               mBtSocket.close();
               signal.fireDisconnected();
            }
            catch (IOException e)
            {
               Log.e(TAG,"proper disconnect failed",e);
            }
            finally
            {
               mBtSocket = null;
            }
         }
      }
   }

   private class ReadRequest implements Request
   {
      @Override
      public void execute(ConnectionSignal signal)
      {
         if (mBtSocket != null && mBtSocket.isConnected())
         {
            try
            {
               InputStream is = mBtSocket.getInputStream();
               int bytesAvailable = is.available();
               if (bytesAvailable > 0)
               {
                  byte buffer[] = new byte[bytesAvailable];
                  int bytesRead = is.read(buffer, 0, bytesAvailable);
                  signal.fireBytesRead(buffer, bytesRead);
               }
               else
               {
                  signal.fireBytesRead(null,0);
               }
            }
            catch (IOException e)
            {
               Log.e(TAG,"read error",e);
               mBtSocket = null;
               signal.fireConnectionLost();
            }
         }
      }
   }


   DelayQueue<RequestWrapper> mJobQueue = new DelayQueue<RequestWrapper>();
   private BluetoothSocket mBtSocket;
   private BluetoothAdapter mBtAdapter;
   private ConnectionStatus mConnectionStatus;
   private ForwardServiceObserver mDummyObserver = new ForwardServiceObserver();
   private ServiceObserver mObserver;

   private Handler mSignalHandler = new Handler()
   {
      public void handleMessage(Message msg)
      {
         switch (msg.what)
         {
            case SIGNAL_CONNECTED:
               onConnected();
               break;
            case SIGNAL_CONNECTIONFAILED:
               onConnectionFailed();
               break;
            case SIGNAL_DISCONNECTED:
               onDisconnected();
               break;
            case SIGNAL_BARCODEREAD:
               Bundle msgData = msg.getData();
               if(msgData != null)
               {
                  byte[] bytes = msgData.getByteArray(BARCODEREAD_SIGNAL_BUNDLE_BARCODE_KEY);
                  if(bytes != null)
                  {
                     String bc = new String(bytes, 0, bytes.length, StandardCharsets.US_ASCII);
                     onRead(bc);
                     break;
                  }
               }
               onEmptyRead();
               break;
            case SIGNAL_CONNECTIONLOST:
               onConnectionLost();
               break;
            case SIGNAL_CONNECTING:
               onConnecting();
               break;

         }
      }
   };

   private ConnectionSignal mSignaller = new ConnectionSignal()
   {
      @Override
      public void fireConnecting()
      {
         mSignalHandler.sendEmptyMessage(SIGNAL_CONNECTING);
      }

      @Override
      public void fireConnected()
      {
         mSignalHandler.sendEmptyMessage(SIGNAL_CONNECTED);
      }

      @Override
      public void fireConnectionFailed()
      {
         mSignalHandler.sendEmptyMessage(SIGNAL_CONNECTIONFAILED);
      }

      @Override
      public void fireDisconnected()
      {
         mSignalHandler.sendEmptyMessage(SIGNAL_DISCONNECTED);
      }

      @Override
      public void fireBytesRead(byte[] buffer, int length)
      {
         Message readMsg = Message.obtain(mSignalHandler, SIGNAL_BARCODEREAD);
         if(buffer != null && length > 0)
         {
            Bundle msgData = new Bundle();
            msgData.putByteArray(BARCODEREAD_SIGNAL_BUNDLE_BARCODE_KEY, Arrays.copyOfRange(buffer,0,length));
            readMsg.setData(msgData);
         }
         mSignalHandler.sendMessage(readMsg);
      }

      @Override
      public void fireConnectionLost()
      {
         mSignalHandler.sendEmptyMessage(SIGNAL_CONNECTIONLOST);
      }

   };

   public enum ConnectionStatus
   {
      DISCONNECTED,
      CONNECTED,
      CONNECTING
   }

   @Override
   protected void startUp() throws Exception
   {
      Log.d(TAG,"starting up");
      mBtAdapter = BluetoothAdapter.getDefaultAdapter();
      mConnectionStatus = ConnectionStatus.DISCONNECTED;
   }

   @Override
   protected void run() throws Exception
   {
      while (isRunning())
      {
         RequestWrapper request = mJobQueue.poll(20, TimeUnit.MILLISECONDS);
         if (request != null)
         {
            request.execute(mSignaller);
         }
      }
   }

   protected void shutDown() throws java.lang.Exception
   {
      Log.d(TAG,"shutting down");
      cancelAllRequests();
      if (mBtSocket != null && mBtSocket.isConnected())
      {
         mBtSocket.close();
         mBtSocket = null;
      }
   }

   private void cancelAllRequests()
   {
      Log.d(TAG,"cancelling all requests");
      mJobQueue.clear();
   }

   public void requestDisconnect()
   {
      Log.d(TAG,"disconnect requested");
      if (mConnectionStatus != ConnectionStatus.DISCONNECTED)
      {
         cancelAllRequests();
         mJobQueue.add(new RequestWrapper(RequestType.Request_Disconnect,new DisconnectRequest(), 0));
      }
      else
      {
         Log.d(TAG,"already disconnected");
      }
   }


   public void requestConnect(BluetoothAddress btAddr)
   {
      Log.d(TAG,"connect to " + btAddr.getCanonicalForm() + " requested");
      BluetoothDevice btDevice = null;
      if (mBtAdapter != null && mBtAdapter.isEnabled())
      {
         btDevice = mBtAdapter.getRemoteDevice(btAddr.getRawForm());
      }
      else
      {
         Log.d(TAG,"no bluetooth");
      }

      if (btDevice != null)
      {
         if (mConnectionStatus != ConnectionStatus.DISCONNECTED)
         {
            requestDisconnect();
         }

         mJobQueue.add(new RequestWrapper(RequestType.Request_Connect,new ConnectRequest(btDevice), 0));
      }
   }


   private void onConnecting()
   {
      mConnectionStatus = ConnectionStatus.CONNECTING;
      notifyConnecting();
   }

   private void onConnected()
   {
      mConnectionStatus = ConnectionStatus.CONNECTED;
      notifyConnected();
      requestRead();
   }

   private void requestRead()
   {
      mJobQueue.add(new RequestWrapper(RequestType.Request_Read,new ReadRequest(),READ_DELAY_MS));
   }

   private void onEmptyRead()
   {
      requestRead();
   }

   private void onRead(String bc)
   {
      notifyBarcode(bc);
      requestRead();
   }

   private void onConnectionLost()
   {
      cancelAllRequests();
      mConnectionStatus = ConnectionStatus.DISCONNECTED;
      notifyConnectionLost();
   }

   private void onConnectionFailed()
   {
      mConnectionStatus = ConnectionStatus.DISCONNECTED;
      notifyConnectionFailed();
   }

   private void onDisconnected()
   {
      mConnectionStatus = ConnectionStatus.DISCONNECTED;
      notifyDisconnected();
   }

   public ServiceObserver getObserver()
   {
      return mObserver;
   }

   private ServiceObserver getSafeObserver()
   {
      if(mObserver != null)
         return mObserver;

      return mDummyObserver;
   }

   public void setObserver(ServiceObserver observer)
   {
      this.mObserver = observer;
   }

   private void notifyConnecting()
   {
      Log.d(TAG,"notify connecting");
      getSafeObserver().notifyConnecting();
   }

    public static String substringBefore(String str, String separator) {
        if (Strings.isNullOrEmpty(str) || separator == null) {
            return str;
        }
        int pos = str.indexOf(separator);
        if (pos == -1) {
            return str;
        }
        return str.substring(0, pos);
    }

   private void notifyBarcode(String bc)
   {
      bc = substringBefore(bc,"\r");
      Log.d(TAG,"notify barcode: " + bc);
      getSafeObserver().notifyBarcode(bc);
   }

   private void notifyConnectionFailed()
   {
      Log.d(TAG,"notify connection failed");
      getSafeObserver().notifyConnectionFailed();
   }

   private void notifyConnectionLost()
   {
      Log.d(TAG,"notify connection lost");
      getSafeObserver().notifyConnectionLost();
   }

   private void notifyDisconnected()
   {
      Log.d(TAG,"notify disconnected");
      getSafeObserver().notifyDisconnected();
   }

   private void notifyConnected()
   {
      Log.d(TAG,"notify connected");
      getSafeObserver().notifyConnected();
   }

}
