package com.bk.print.service;


import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.bk.bands.paper.BitmapPaper;
import com.bk.bands.serializer.DocumentBean;
import com.bk.btcommon.model.BluetoothAddress;
import com.bk.print.service.drivers.ApexPrinterDriver;
import com.bk.print.service.drivers.SeikoPrinterDriver;
import com.google.common.base.Strings;
import com.google.common.util.concurrent.AbstractExecutionThreadService;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.UUID;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

/**
 * Created by IntelliJ IDEA.
 * User: SG0891787
 * Date: 3/11/13
 * Time: 3:31 PM
 */
public class PrintWorker extends AbstractExecutionThreadService {
    private static final String TAG = PrintWorker.class.getName();

    private static final UUID SPP_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private static final int SIGNAL_CONNECTING = 1;
    private static final int SIGNAL_CONNECTED = 2;
    private static final int SIGNAL_CONNECTIONFAILED = 3;
    private static final int SIGNAL_DISCONNECTED = 4;
    private static final int SIGNAL_CONNECTIONLOST = 5;
    private static final int SIGNAL_JOBPROGRESS = 6;
    private static final int SIGNAL_JOBERROR = 7;
    private static final int SIGNAL_JOBSTARTED = 8;
    private static final int SIGNAL_JOBABORTED = 9;
    private static final int SIGNAL_JOBCOMPLETED = 10;

    private static final String SIGNAL_DATA_JOBID = "job_id";
    private static final String SIGNAL_DATA_ERRORCODE = "error_code";
    private static final String SIGNAL_DATA_PROGRESS = "progress";


    enum RequestType {
        Request_Connect,
        Request_Disconnect,
        Request_Print,
        Request_Abort,
        Request_AbortAll
    }

    private interface PrintSignal {
        void fireConnecting();

        void fireConnected();

        void fireConnectionFailed();

        void fireDisconnected();

        void fireConnectionLost();

        void fireJobStarted(UUID jobId);

        void fireJobAborted(UUID jobId);

        void fireJobCompleted(UUID jobId);

        void fireJobError(UUID jobId, String errorCode);

        boolean jobProgress(UUID jobId, int progress);
    }

    private interface Request {
        void execute(PrintSignal signal);
    }


    public class RequestWrapper implements Delayed, Request {
        private long mEndOfDelay;
        private Request mRq;
        private RequestType mType;

        public RequestWrapper(RequestType type, Request rq, long delayMs) {
            this.mEndOfDelay = System.currentTimeMillis() + delayMs;
            this.mRq = rq;
            this.mType = type;
        }

        @Override
        public long getDelay(TimeUnit unit) {
            return unit.convert(mEndOfDelay - System.currentTimeMillis(), TimeUnit.MILLISECONDS);

        }

        @Override
        public int compareTo(Delayed o) {
            int ret = 0;
            RequestWrapper rq = (RequestWrapper) o;

            if (this.mEndOfDelay < rq.mEndOfDelay) {
                ret = -1;
            } else if (this.mEndOfDelay > rq.mEndOfDelay) {
                ret = 1;
            }
            return ret;
        }

        @Override
        public void execute(PrintSignal signal) {
            mRq.execute(signal);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            RequestWrapper wrapper = (RequestWrapper) o;

            return mType == wrapper.mType;

        }
    }


    private class ConnectRequest implements Request {
        BluetoothDevice mDeviceToConnectTo;

        private ConnectRequest(BluetoothDevice deviceToConnectTo) {
            this.mDeviceToConnectTo = deviceToConnectTo;
        }

        @Override
        public void execute(PrintSignal signal) {
            if (mBtSocket == null) {
                try {

                    signal.fireConnecting();
                    Log.d(TAG, "creating socket");
                    mBtSocket = mDeviceToConnectTo.createInsecureRfcommSocketToServiceRecord(SPP_UUID);
                    Log.d(TAG, "connecting");
                    mBtSocket.connect();
                    BluetoothDevice remoteDevice = mBtSocket.getRemoteDevice();
                    String name = remoteDevice.getName();
                    Log.d(TAG, "connected to:" + name + " @ " + remoteDevice.getAddress());
                    if (!Strings.isNullOrEmpty(name)) {
                        if (name.startsWith("DPU-S445")) {
                            Log.d(TAG, "setting Seiko DPU-S445 driver");
                            mDriver = new SeikoPrinterDriver(mBtSocket.getOutputStream());
                        } else if (name.startsWith("APEX4")) {
                            Log.d(TAG, "setting APEX 4 driver");
                            mDriver = new ApexPrinterDriver(mBtSocket.getOutputStream());
                        } else {
                            Log.e(TAG, "unsupported device");
                            throw new IOException("unknown device: " + name);
                        }
                    }
                    signal.fireConnected();

                } catch (IOException e) {
                    Log.e(TAG, "connection failure", e);
                    signal.fireConnectionFailed();
                    mBtSocket = null;
                }
            } else {
                Log.e(TAG, "already connected");
            }
        }
    }

    private class DisconnectRequest implements Request {
        @Override
        public void execute(PrintSignal signal) {
            if (mBtSocket != null) {
                try {
                    mBtSocket.close();
                    signal.fireDisconnected();
                } catch (IOException e) {
                    Log.e(TAG, "proper disconnect failed", e);
                } finally {
                    mBtSocket = null;
                }
            }
        }
    }

    private class PrintRequest implements Request {
        private PrintJob job;

        private PrintRequest(PrintJob job) {
            this.job = job;
        }

        @Override
        public void execute(PrintSignal signal) {

            ScheduledWork newWork = new ScheduledWork();
            newWork.job = job;
            mPrintQueue.addLast(newWork);
        }
    }

    private class AbortRequest implements Request {
        private UUID jobId;

        public AbortRequest(UUID jobId) {
            this.jobId = jobId;
        }

        @Override
        public void execute(PrintSignal signal) {

            for (Iterator<ScheduledWork> iterator = mPrintQueue.iterator(); iterator.hasNext(); ) {
                ScheduledWork work = iterator.next();
                if (work.job.getJobId().equals(jobId)) {
                    iterator.remove();
                    signal.fireJobAborted(jobId);
                    break;
                }
            }
        }
    }


    class ScheduledWork {
        PrintJob job;
        Bitmap image;
        int currentLine;
    }

    private LinkedList<ScheduledWork> mPrintQueue = new LinkedList<ScheduledWork>();
    private DelayQueue<RequestWrapper> mJobQueue = new DelayQueue<RequestWrapper>();
    private BluetoothSocket mBtSocket;
    private BluetoothAdapter mBtAdapter;
    private ConnectionStatus mConnectionStatus;
    private ForwardServiceObserver mDummyObserver = new ForwardServiceObserver();
    private ServiceObserver mObserver;
    private PrinterDriver mDriver;

    private Handler mSignalHandler = new Handler() {

        private UUID peelJobId(Message msg) {
            return (UUID) msg.getData().getSerializable(SIGNAL_DATA_JOBID);
        }

        private String peelErrorCode(Message msg) {
            return msg.getData().getString(SIGNAL_DATA_ERRORCODE);
        }

        private int peelProgress(Message msg) {
            return msg.getData().getInt(SIGNAL_DATA_PROGRESS);
        }

        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SIGNAL_CONNECTED:
                    onConnected();
                    break;
                case SIGNAL_CONNECTIONFAILED:
                    onConnectionFailed();
                    break;
                case SIGNAL_DISCONNECTED:
                    onDisconnected();
                    break;

                case SIGNAL_CONNECTIONLOST:
                    onConnectionLost();
                    break;
                case SIGNAL_CONNECTING:
                    onConnecting();
                    break;

                case SIGNAL_JOBPROGRESS:
                    onJobProgress(peelJobId(msg), peelProgress(msg));
                    break;
                case SIGNAL_JOBERROR:
                    onJobError(peelJobId(msg), peelErrorCode(msg));
                    break;
                case SIGNAL_JOBSTARTED:
                    onJobStarted(peelJobId(msg));
                    break;
                case SIGNAL_JOBABORTED:
                    onJobAborted(peelJobId(msg));
                    break;
                case SIGNAL_JOBCOMPLETED:
                    onJobCompleted(peelJobId(msg));
                    break;
            }

        }
    };

    private PrintSignal mSignaller = new PrintSignal() {
        @Override
        public void fireConnecting() {
            mSignalHandler.sendEmptyMessage(SIGNAL_CONNECTING);
        }

        @Override
        public void fireConnected() {
            mSignalHandler.sendEmptyMessage(SIGNAL_CONNECTED);
        }

        @Override
        public void fireConnectionFailed() {
            mSignalHandler.sendEmptyMessage(SIGNAL_CONNECTIONFAILED);
        }

        @Override
        public void fireDisconnected() {
            mSignalHandler.sendEmptyMessage(SIGNAL_DISCONNECTED);
        }

        @Override
        public void fireConnectionLost() {
            mSignalHandler.sendEmptyMessage(SIGNAL_CONNECTIONLOST);
        }

        @Override
        public void fireJobStarted(UUID jobId) {
            sendJobMessage(SIGNAL_JOBSTARTED, jobId);
        }

        private void sendJobMessage(int what, UUID jobId) {
            Message msg = mSignalHandler.obtainMessage(what);
            Bundle data = new Bundle();
            data.putSerializable(SIGNAL_DATA_JOBID, jobId);
            msg.setData(data);
            mSignalHandler.sendMessage(msg);
        }

        @Override
        public void fireJobAborted(UUID jobId) {
            sendJobMessage(SIGNAL_JOBABORTED, jobId);
        }

        @Override
        public void fireJobCompleted(UUID jobId) {
            sendJobMessage(SIGNAL_JOBCOMPLETED, jobId);
        }

        @Override
        public void fireJobError(UUID jobId, String errorCode) {
            Message msg = mSignalHandler.obtainMessage(SIGNAL_JOBERROR);
            Bundle data = new Bundle();
            data.putSerializable(SIGNAL_DATA_JOBID, jobId);
            data.putString(SIGNAL_DATA_ERRORCODE, errorCode);
            msg.setData(data);
            mSignalHandler.sendMessage(msg);
        }

        @Override
        public boolean jobProgress(UUID jobId, int progress) {

            Message msg = mSignalHandler.obtainMessage(SIGNAL_JOBPROGRESS);
            Bundle data = new Bundle();
            data.putSerializable(SIGNAL_DATA_JOBID, jobId);
            data.putInt(SIGNAL_DATA_PROGRESS, progress);
            msg.setData(data);
            mSignalHandler.sendMessage(msg);
            return true;
        }
    };

    public enum ConnectionStatus {
        DISCONNECTED,
        CONNECTED,
        CONNECTING
    }

    @Override
    protected void startUp() throws Exception {
        Log.d(TAG, "starting up");
        mBtAdapter = BluetoothAdapter.getDefaultAdapter();
        mConnectionStatus = ConnectionStatus.DISCONNECTED;
    }

    @Override
    protected void run() throws Exception {
        Log.d(TAG, "worker running");

        try {
            while (isRunning()) {
                RequestWrapper request = mJobQueue.poll(20, TimeUnit.MILLISECONDS);
                if (request != null) {
                    Log.d(TAG, "executing " + request.mType);
                    request.execute(mSignaller);
                }


                try {
                    printLine();
                } catch (IOException e) {

                    ScheduledWork work = getFirstScheduledWork();

                    if (work != null) {
                        PrintJob job = work.job;
                        UUID jobId = job.getJobId();
                        mSignaller.fireJobError(jobId, "connection-lost");
                        mPrintQueue.pollFirst();
                    }

                    if (mBtSocket != null) {
                        mBtSocket.close();
                        mBtSocket = null;
                    }
                    mSignaller.fireConnectionLost();
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "worker loop unhandled error", e);
            throw e;
        }
    }

    private ScheduledWork getFirstScheduledWork() {
        if (mPrintQueue.size() == 0)
            return null;
        return mPrintQueue.getFirst();
    }

    private void printLine() throws IOException {

        final int stripLines = 16;

        ScheduledWork work = getFirstScheduledWork();

        if (work != null) {
            PrintJob job = work.job;
            UUID jobId = job.getJobId();
            if (work.image == null) {
                DocumentBean document = job.getDocument();
                BitmapPaper paper = new BitmapPaper(208, document.getSize());
                document.print(paper);
                work.image = paper.getBitmap();
                work.currentLine = 0;
                mSignaller.fireJobStarted(jobId);
            }

            int xDots = work.image.getWidth();
            int yDots = work.image.getHeight();

            int linesLeft = yDots - work.currentLine;
            int nextStripHeight = Math.min(stripLines,linesLeft);

            int[] bitmapRows = new int[xDots*nextStripHeight];


            work.image.getPixels(bitmapRows, 0, xDots, 0, work.currentLine, xDots, nextStripHeight);

            ImageStrip strip = new ImageStrip(xDots,nextStripHeight,bitmapRows);
            sendStrip(strip);

            work.currentLine+=nextStripHeight;

            if (!mSignaller.jobProgress(jobId, (work.currentLine * 100) / yDots)) {
                mSignaller.fireJobAborted(jobId);
                mPrintQueue.pollFirst();
            }

            if (work.currentLine >= yDots) {
                feedPaper();
                mPrintQueue.pollFirst();
                mSignaller.fireJobCompleted(jobId);
            }
        }
    }

    private void sendStrip(ImageStrip strip) throws IOException {

        mDriver.writeStrip(strip);

    }

    private void feedPaper() throws IOException {
        mDriver.feedPaper(100);
    }

    protected void shutDown() throws java.lang.Exception {
        Log.d(TAG, "shutting down");
        cancelAllRequests();
        if (mBtSocket != null && mBtSocket.isConnected()) {
            mBtSocket.close();
            mBtSocket = null;
        }
    }

    private void cancelAllRequests() {
        Log.d(TAG, "cancelling all requests");
        mJobQueue.clear();
    }

    public void requestDisconnect() {
        Log.d(TAG, "disconnect requested");
        if (mConnectionStatus != ConnectionStatus.DISCONNECTED) {
//            cancelAllRequests();
            mJobQueue.add(new RequestWrapper(RequestType.Request_Disconnect, new DisconnectRequest(), 0));
        } else {
            Log.d(TAG, "already disconnected");
        }
    }

    public void requestPrint(PrintJob job) {
        Log.d(TAG, "print job request; jobid = " + job.getJobId());
        mJobQueue.add(new RequestWrapper(RequestType.Request_Print, new PrintRequest(job), 0));
    }

    public void requestAbort(UUID jobId) {
        Log.d(TAG, "print job abort request; jobid = " + jobId);
        mJobQueue.add(new RequestWrapper(RequestType.Request_Abort, new AbortRequest(jobId), 0));
    }



    public void requestConnect(BluetoothAddress btAddr) {
        Log.d(TAG, "connect to " + btAddr.getCanonicalForm() + " requested");
        BluetoothDevice btDevice = null;
        if (mBtAdapter != null && mBtAdapter.isEnabled()) {
            btDevice = mBtAdapter.getRemoteDevice(btAddr.getRawForm());
        } else {
            Log.d(TAG, "no bluetooth");
        }

        if (btDevice != null) {
            if (mConnectionStatus != ConnectionStatus.DISCONNECTED) {
                Log.d(TAG, "disconnecting before new connection");
                requestDisconnect();
            }

            mJobQueue.add(new RequestWrapper(RequestType.Request_Connect, new ConnectRequest(btDevice), 0));
        }
    }


    private void onJobProgress(UUID jobId, int progress) {
        getSafeObserver().onJobProgress(jobId, progress);
    }

    private void onJobError(UUID jobId, String errorCode) {
        getSafeObserver().onJobError(jobId, errorCode);
    }

    private void onJobStarted(UUID jobId) {
        getSafeObserver().onJobStarted(jobId);
    }

    private void onJobAborted(UUID jobId) {
        getSafeObserver().onJobAborted(jobId);
    }

    private void onJobCompleted(UUID jobId) {
        getSafeObserver().onJobCompleted(jobId);
    }


    private void onConnecting() {
        mConnectionStatus = ConnectionStatus.CONNECTING;
        notifyConnecting();
    }

    private void onConnected() {
        mConnectionStatus = ConnectionStatus.CONNECTED;
        notifyConnected();
        Log.d(TAG, "requesting bat. status first time");
    }

    private void onConnectionLost() {
        cancelAllRequests();
        mConnectionStatus = ConnectionStatus.DISCONNECTED;
        notifyConnectionLost();
    }

    private void onConnectionFailed() {
        mConnectionStatus = ConnectionStatus.DISCONNECTED;
        notifyConnectionFailed();
    }

    private void onDisconnected() {
        mConnectionStatus = ConnectionStatus.DISCONNECTED;
        notifyDisconnected();
    }

    public ServiceObserver getObserver() {
        return mObserver;
    }

    private ServiceObserver getSafeObserver() {
        if (mObserver != null)
            return mObserver;

        return mDummyObserver;
    }

    public void setObserver(ServiceObserver observer) {
        this.mObserver = observer;
    }

    private void notifyConnecting() {
        Log.d(TAG, "notify connecting");
        getSafeObserver().onConnecting();
    }

    private void notifyConnectionFailed() {
        Log.d(TAG, "notify connection failed");
        getSafeObserver().onConnectionFailed();
    }

    private void notifyConnectionLost() {
        Log.d(TAG, "notify connection lost");
        getSafeObserver().onConnectionLost();
    }

    private void notifyDisconnected() {
        Log.d(TAG, "notify disconnected");
        getSafeObserver().onDisconnected();
    }

    private void notifyConnected() {
        Log.d(TAG, "notify connected");
        getSafeObserver().onConnected();
    }

}
