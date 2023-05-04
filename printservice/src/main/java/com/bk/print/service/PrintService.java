package com.bk.print.service;

/**
 * Created by IntelliJ IDEA.
 * User: SG0891787
 * Date: 9/5/12
 * Time: 11:33 AM
 */

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.preference.PreferenceManager;
import android.util.Log;

import com.bk.bands.serializer.DocumentBean;
import com.bk.btcommon.model.BluetoothAddress;
import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.DatabaseOptions;
import com.couchbase.lite.Document;
import com.couchbase.lite.Manager;
import com.couchbase.lite.android.AndroidContext;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.bk.print.service.PrintServiceCommons.MSG_ABORT_JOB;
import static com.bk.print.service.PrintServiceCommons.MSG_DESCRIPTOR_KEY;
import static com.bk.print.service.PrintServiceCommons.MSG_DOCUMENT_KEY;
import static com.bk.print.service.PrintServiceCommons.MSG_ERROR_CODE;
import static com.bk.print.service.PrintServiceCommons.MSG_GET_PRINTER_ADDRESS;
import static com.bk.print.service.PrintServiceCommons.MSG_GET_STATE;
import static com.bk.print.service.PrintServiceCommons.MSG_JOBIDS_KEY;
import static com.bk.print.service.PrintServiceCommons.MSG_JOBID_KEY;
import static com.bk.print.service.PrintServiceCommons.MSG_JOBSLIST_KEY;
import static com.bk.print.service.PrintServiceCommons.MSG_LIST_JOBS;
import static com.bk.print.service.PrintServiceCommons.MSG_NOTIFY_CONNECTED;
import static com.bk.print.service.PrintServiceCommons.MSG_NOTIFY_CONNECTING;
import static com.bk.print.service.PrintServiceCommons.MSG_NOTIFY_DISCONNECTED;
import static com.bk.print.service.PrintServiceCommons.MSG_NOTIFY_JOBS_LIST;
import static com.bk.print.service.PrintServiceCommons.MSG_NOTIFY_JOB_ABORTED;
import static com.bk.print.service.PrintServiceCommons.MSG_NOTIFY_JOB_COMPLETED;
import static com.bk.print.service.PrintServiceCommons.MSG_NOTIFY_JOB_ERROR;
import static com.bk.print.service.PrintServiceCommons.MSG_NOTIFY_JOB_PROGRESS;
import static com.bk.print.service.PrintServiceCommons.MSG_NOTIFY_JOB_STARTED;
import static com.bk.print.service.PrintServiceCommons.MSG_NOTIFY_JOB_SUBMITTED;
import static com.bk.print.service.PrintServiceCommons.MSG_PRINTER_BTADDR_KEY;
import static com.bk.print.service.PrintServiceCommons.MSG_PROGRESS_KEY;
import static com.bk.print.service.PrintServiceCommons.MSG_REGISTER_LISTENER;
import static com.bk.print.service.PrintServiceCommons.MSG_SET_PRINTER_ADDRESS;
import static com.bk.print.service.PrintServiceCommons.MSG_SUBMIT_JOB;
import static com.bk.print.service.PrintServiceCommons.MSG_UNREGISTER_LISTENER;

public class PrintService extends Service {
    private static final String TAG = "PrintService";
    private static final boolean LOGGING_ENABLED = false;

    enum ConnectionState {
        CONNECTING,
        CONNECTED,
        DISCONNECTED
    }

    class PrintJobState {
        PrintJob job;
        JobStatus status;
        int progress;
    }


    ArrayList<Messenger> mListeners = new ArrayList<Messenger>();
    Map<UUID, PrintJobState> mJobs = new HashMap<UUID, PrintJobState>();
    ObjectMapper mMapper = new ObjectMapper();

    final Messenger mMessenger = new Messenger(new IncomingHandler());
    private ConnectionState mConnectionState;

    private PrintWorker mWorker = new PrintWorker();

    private Manager mManager;
    private Database mDatabase;

    public static final String ACTION_SERVICE_STARTED = "com.bk.print.service.action.SERVICE_STARTED";
    public static final String ACTION_SERVICE_STOPPED = "com.bk.print.service.action.SERVICE_STOPPED";

    private static final String KEY_PRINTER_BT_ADDR = "printer_bt_address";

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
                case MSG_GET_PRINTER_ADDRESS:
                    onGetConfiguration(msg.replyTo);
                    break;
                case MSG_SET_PRINTER_ADDRESS:
                    Bundle msgData = msg.getData();
                    if (msgData != null) {
                        msgData.setClassLoader(BluetoothAddress.class.getClassLoader());
                        BluetoothAddress address = msgData.getParcelable(MSG_PRINTER_BTADDR_KEY);
                        if (address != null) {
                            onSetConfiguration(address);
                        }
                    }
                    break;
                case MSG_GET_STATE:
                    onGetState(msg.replyTo);
                    break;
                case MSG_SUBMIT_JOB:
                    Bundle data = msg.getData();
                    onSubmitJob((DocumentBean) data.getSerializable(MSG_DOCUMENT_KEY));
                    break;
                case MSG_LIST_JOBS:
                    UUID[] ids = (UUID[]) msg.getData().getSerializable(MSG_JOBIDS_KEY);
                    onListJobs(msg.replyTo, ids);
                    break;
                case MSG_ABORT_JOB:
                    UUID id = (UUID) msg.getData().getSerializable(MSG_JOBID_KEY);
                    onAbortJob(id);
                default:
                    super.handleMessage(msg);
            }
        }
    }




    private static final int MSG_CONNECTION_DISPOSE = 1;
    private Handler mConnectionDisposeHandler = new Handler() {
        public void handleMessage(Message msg) {

            Log.d(TAG, "connection disposal check");
            if (mConnectionState != ConnectionState.DISCONNECTED) {
                for(PrintJobState state : mJobs.values()) {
                    if(state.status == JobStatus.Printing || state.status == JobStatus.Waiting ) {
                        Log.d(TAG, "there are active jobs in job queue, deferring connection disposal");
                        scheduleConnectionDisposal();
                        return;
                    }

                    Log.d(TAG, "disposing connection");
                    disconnect();
                }
            }
            else
            {
                Log.d(TAG, "connection already disposed");
            }
        }
    };


    private static final int MSG_RECONNECT = 1;
    private Handler mReconnectHandler = new Handler() {
        public void handleMessage(Message msg) {
            Log.d(TAG, "reconnecting");
            connect();
        }
    };

    private void disposeConnectionIfNeeded() {

        if (mListeners.size() == 0) {
            if (mConnectionState != ConnectionState.DISCONNECTED) {
                scheduleConnectionDisposal();
            }
        }
    }

    private void scheduleConnectionDisposal()
    {
        Log.d(TAG, "scheduling connection disposal in " + CONNECTION_DISPOSAL_DELAY_SEC + "ms");
        mConnectionDisposeHandler.sendEmptyMessageDelayed(MSG_CONNECTION_DISPOSE, CONNECTION_DISPOSAL_DELAY_SEC);
    }

    private void ensureConnection() {
        if (mListeners.size() > 0) {
            if (mConnectionState == ConnectionState.DISCONNECTED) {
                Log.e(TAG, "connecting");
                connect();
            } else {
                cancelConnectionDisposal();
            }
        }
    }

    private void cancelConnectionDisposal() {
        Log.d(TAG, "canceling connection disposal");
        mConnectionDisposeHandler.removeMessages(MSG_CONNECTION_DISPOSE);
    }

    private void onGetState(Messenger replyTo) {
        switch (mConnectionState) {
            case CONNECTED:
                notifyConnected(replyTo);
                break;
            case CONNECTING:
                notifyConnecting(replyTo);
                break;
            case DISCONNECTED:
                notifyDisconnected(replyTo);
                break;
        }
    }

    private void onGetConfiguration(Messenger replyTo) {
        Message message = Message.obtain();

        Bundle data = new Bundle();
        data.putParcelable(MSG_PRINTER_BTADDR_KEY,readPrinterAddress());
        message.setData(data);
        message.what = PrintServiceCommons.MSG_NOTIFY_PRINTER_ADDRESS;
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
        mWorker.requestDisconnect();
        mConnectionState = ConnectionState.DISCONNECTED;
    }

    private void tryDelayedReconnect() {
        if (mConnectionState == ConnectionState.DISCONNECTED) {
            mReconnectHandler.sendEmptyMessageDelayed(MSG_RECONNECT, RECONNECT_DELAY_SEC);
        }
    }

    private void connect() {
        mReconnectHandler.removeMessages(MSG_RECONNECT);
        BluetoothAddress btScannerAddress = readPrinterAddress();
        if (btScannerAddress != null) {
            mWorker.requestConnect(btScannerAddress);
        }
    }

    private ServiceObserver mWorkerObserver = new ForwardServiceObserver() {
        @Override
        public void onConnecting() {
            mConnectionState = ConnectionState.CONNECTING;
            notifyConnecting(null);
        }

        @Override
        public void onConnected() {
            mConnectionState = ConnectionState.CONNECTED;
            notifyConnected(null);
        }

        @Override
        public void onConnectionFailed() {
            onDisconnected();
            tryDelayedReconnect();
        }

        @Override
        public void onDisconnected() {
            mConnectionState = ConnectionState.DISCONNECTED;
            notifyDisconnected(null);
        }


        @Override
        public void onConnectionLost() {
            onDisconnected();
            tryDelayedReconnect();
        }

        @Override
        public void onJobSubmitted(JobDescriptor descriptor) {
            PrintJobState jobState = mJobs.get(descriptor.getJobId());
            logPrintJob(jobState);
            notifyJobSubmitted(descriptor);
        }

        @Override
        public void onJobProgress(UUID jobId, int progress) {
            PrintJobState jobState = mJobs.get(jobId);
            jobState.progress = progress;
            jobState.status = JobStatus.Printing;
            updatePrintJob(jobState,null);
            notifyJobProgress(jobId,progress);
        }

        @Override
        public void onJobError(UUID jobId, String errorCode) {
            PrintJobState jobState = mJobs.get(jobId);
            jobState.status = JobStatus.Error;
            updatePrintJob(jobState,errorCode);
            notifyJobEror(jobId,errorCode);
            mJobs.remove(jobId);
        }

        @Override
        public void onJobStarted(UUID jobId) {
            PrintJobState jobState = mJobs.get(jobId);
            jobState.status = JobStatus.Printing;
            updatePrintJob(jobState,null);
            notifyJobStarted(jobId);
        }


        @Override
        public void onJobAborted(UUID jobId) {
            PrintJobState jobState = mJobs.get(jobId);
            jobState.status = JobStatus.Aborted;
            updatePrintJob(jobState,null);
            notifyJobAborted(jobId);
            mJobs.remove(jobId);
        }

        @Override
        public void onJobCompleted(UUID jobId) {
            PrintJobState jobState = mJobs.get(jobId);
            jobState.status = JobStatus.Completed;
            updatePrintJob(jobState,null);
            notifyJobCompleted(jobId);
            mJobs.remove(jobId);
        }
    };

    private void notifyJobCompleted(UUID jobId) {
        Message msg = Message.obtain(null, MSG_NOTIFY_JOB_COMPLETED);
        Bundle data = msg.getData();
        data.putSerializable(MSG_JOBID_KEY,jobId);
        sendMsgToListeners(msg);
    }

    private void notifyJobAborted(UUID jobId) {
        Message msg = Message.obtain(null, MSG_NOTIFY_JOB_ABORTED);
        Bundle data = msg.getData();
        data.putSerializable(MSG_JOBID_KEY,jobId);
        sendMsgToListeners(msg);
    }

    private void notifyJobStarted(UUID jobId) {
        Message msg = Message.obtain(null, MSG_NOTIFY_JOB_STARTED);
        Bundle data = msg.getData();
        data.putSerializable(MSG_JOBID_KEY,jobId);
        sendMsgToListeners(msg);
    }


    private void notifyJobEror(UUID jobId, String errorCode) {
        Message msg = Message.obtain(null, MSG_NOTIFY_JOB_ERROR);
        Bundle data = msg.getData();
        data.putString(MSG_ERROR_CODE,errorCode);
        data.putSerializable(MSG_JOBID_KEY,jobId);
        sendMsgToListeners(msg);
    }

    private void notifyJobProgress(UUID jobId, int progress) {
        Message msg = Message.obtain(null, MSG_NOTIFY_JOB_PROGRESS);
        Bundle data = msg.getData();
        data.putInt(MSG_PROGRESS_KEY,progress);
        data.putSerializable(MSG_JOBID_KEY,jobId);
        sendMsgToListeners(msg);
    }

    private void notifyDisconnected(Messenger dest) {
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

    private void notifyConnecting(Messenger dest) {
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

    private void notifyConnected(Messenger dest) {
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


    private void sendMsgToListeners(Message msg) {
        for (Messenger listener : mListeners) {
            try {
                listener.send(Message.obtain(msg));
            } catch (RemoteException e) {
            }
        }
    }

    private void onAbortJob(UUID id) {
        mWorker.requestAbort(id);
    }

    private void onSubmitJob(DocumentBean document) {

        UUID jobId = UUID.randomUUID();
        PrintJob job = new PrintJob(jobId, document);
        PrintJobState jobState = new PrintJobState();
        jobState.job = job;
        jobState.progress = 0;
        jobState.status = JobStatus.Waiting;

        mJobs.put(jobId, jobState);
        mWorker.requestPrint(job);
        mWorkerObserver.onJobSubmitted(new JobDescriptor(jobId, job.getDocument().getName(), jobState.status));
    }

    private void logPrintJob(PrintJobState jobState) {
        PrintJob job = jobState.job;
        UUID jobId = job.getJobId();
        Document document = new Document(mDatabase, jobId.toString());

        PrintJobLogEntry logEntry = createLogEntry(jobState);

        Map<String, Object> mapOfProperties = mMapper.convertValue(logEntry, new TypeReference<Map<String, Object>>() {
        });

        try {
            document.putProperties(mapOfProperties);
        } catch (CouchbaseLiteException e) {
            Log.e(TAG, "failed to create print job log entry - " + jobId, e);
        }
    }

    private void updatePrintJob(PrintJobState jobState,String errorCd) {
        PrintJob job = jobState.job;
        UUID jobId = job.getJobId();
        Document retrievedDocument = mDatabase.getDocument(jobId.toString());

        try {

            if (retrievedDocument != null) {

                PrintJobLogEntry logEntry = createLogEntry(jobState);
                logEntry.setErrorCode(errorCd);
                Map<String, Object> mapOfProperties = mMapper.convertValue(logEntry, new TypeReference<Map<String, Object>>() {
                });
                mapOfProperties.put("_rev",retrievedDocument.getCurrentRevisionId());
                retrievedDocument.putProperties(mapOfProperties);
            } else {
                Document document = new Document(mDatabase, jobId.toString());
                PrintJobLogEntry logEntry = createLogEntry(jobState);
                logEntry.setErrorCode(errorCd);
                Map<String, Object> mapOfProperties = mMapper.convertValue(logEntry, new TypeReference<Map<String, Object>>() {
                });
                document.putProperties(mapOfProperties);
            }
        } catch (CouchbaseLiteException e) {
            Log.e(TAG, "failed to update print job log entry - " + jobId, e);
        }
    }

    private PrintJobLogEntry createLogEntry(PrintJobState jobState) {

        PrintJob job = jobState.job;
        UUID jobId = job.getJobId();
        PrintJobLogEntry logEntry = new PrintJobLogEntry();
        logEntry.setDocumentName(job.getDocument().getName());
        logEntry.setId(jobId);
        logEntry.setStatus(jobState.status);
        return logEntry;
    }

    private void onListJobs(Messenger replyTo, UUID[] ids) {
        Message msg = Message.obtain(null, MSG_NOTIFY_JOBS_LIST);

        Collection<UUID> idsToQuery = new ArrayList<>();
        if (ids == null || ids.length == 0) {
            idsToQuery.addAll(mJobs.keySet());
        } else {
            for (UUID id : ids) {
                idsToQuery.add(id);
            }
        }

        ArrayList<JobDescriptor> jobDescriptors = new ArrayList<JobDescriptor>(mJobs.size());
        for (UUID jobId : idsToQuery) {

            PrintJobState jobState = mJobs.get(jobId);

            if (jobState != null) {
                PrintJob job = jobState.job;
                JobDescriptor jobDesc = new JobDescriptor(job.getJobId(), job.getDocument().getName(), jobState.status);
                jobDesc.setProgress(jobState.progress);
                jobDesc.setErrorCode(null);
                jobDescriptors.add(jobDesc);
            } else {

                Document retrievedDocument = mDatabase.getDocument(jobId.toString());
                if (retrievedDocument != null) {
                    Map<String, Object> mapOfProperties = retrievedDocument.getProperties();
                    PrintJobLogEntry logEntry = mMapper.convertValue(mapOfProperties, PrintJobLogEntry.class);
                    JobDescriptor jobDesc = new JobDescriptor(logEntry.getId(), logEntry.getDocumentName(), logEntry.getStatus());
                    jobDesc.setProgress(100);
                    jobDesc.setErrorCode(logEntry.getErrorCode());
                    jobDescriptors.add(jobDesc);
                }
            }
        }


        Bundle data = new Bundle();
        data.putSerializable(MSG_JOBSLIST_KEY, jobDescriptors);
        msg.setData(data);
        try {
            replyTo.send(msg);
        } catch (RemoteException ignored) {

        }

    }


    private void notifyJobSubmitted(JobDescriptor jobDesc) {

        Message msg = Message.obtain(null, MSG_NOTIFY_JOB_SUBMITTED);
        Bundle data = new Bundle();
        data.putSerializable(MSG_DESCRIPTOR_KEY, jobDesc);
        msg.setData(data);
        sendMsgToListeners(msg);
    }


    @Override
    public void onCreate() {
        super.onCreate();

        mConnectionState = ConnectionState.DISCONNECTED;

        mWorker.startAsync();
        mWorker.setObserver(mWorkerObserver);
        enableLogging();
        mDatabase = openDatabase();
        mMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

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

    private BluetoothAddress readPrinterAddress() {
        String scannerBtAddr = readPreference(KEY_PRINTER_BT_ADDR);
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
        savePreference(KEY_PRINTER_BT_ADDR, preferenceValue);
    }


    void broadcastStart() {
        Intent startIntent = new Intent(ACTION_SERVICE_STARTED);
        sendBroadcast(startIntent);
    }

    void broadcastStop() {
        Intent stopIntent = new Intent(ACTION_SERVICE_STOPPED);
        sendBroadcast(stopIntent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY; // run until explicitly stopped.
    }


    @Override
    public void onDestroy() {
        super.onDestroy();

        mWorker.stopAsync();
        mWorker.awaitTerminated();

        unregisterReceiver(mReceiver);
        broadcastStop();
    }

    private Manager getManager() {
        if (mManager == null) {
            try {
                AndroidContext context = new AndroidContext(getApplicationContext());
                mManager = new Manager(context, Manager.DEFAULT_OPTIONS);
            } catch (Exception e) {
                Log.e(TAG, "cannot create Manager object", e);
            }
        }
        return mManager;
    }

    private Database openDatabase() {
        try {
            DatabaseOptions options = new DatabaseOptions();
            options.setCreate(true);
            options.setStorageType(Manager.SQLITE_STORAGE);
            options.setEncryptionKey(null);
            return getManager().openDatabase("printjobslog", options);
        } catch (CouchbaseLiteException e) {
            Log.e(TAG, "cannot create database", e);
        }
        return null;
    }

    private void enableLogging() {
        if (LOGGING_ENABLED) {
            Manager.enableLogging(TAG, Log.VERBOSE);
            Manager.enableLogging(com.couchbase.lite.util.Log.TAG, Log.VERBOSE);
            Manager.enableLogging(com.couchbase.lite.util.Log.TAG_SYNC_ASYNC_TASK, Log.VERBOSE);
            Manager.enableLogging(com.couchbase.lite.util.Log.TAG_SYNC, Log.VERBOSE);
            Manager.enableLogging(com.couchbase.lite.util.Log.TAG_QUERY, Log.VERBOSE);
            Manager.enableLogging(com.couchbase.lite.util.Log.TAG_VIEW, Log.VERBOSE);
            Manager.enableLogging(com.couchbase.lite.util.Log.TAG_DATABASE, Log.VERBOSE);
        }
    }

}
