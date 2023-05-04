package com.bk.print.service;

import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.Parcelable;
import android.os.RemoteException;

import com.bk.bands.serializer.DocumentBean;
import com.bk.btcommon.model.BluetoothAddress;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
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
import static com.bk.print.service.PrintServiceCommons.MSG_NOTIFY_PRINTER_ADDRESS;
import static com.bk.print.service.PrintServiceCommons.MSG_PRINTER_BTADDR_KEY;
import static com.bk.print.service.PrintServiceCommons.MSG_PROGRESS_KEY;
import static com.bk.print.service.PrintServiceCommons.MSG_REGISTER_LISTENER;
import static com.bk.print.service.PrintServiceCommons.MSG_SET_PRINTER_ADDRESS;
import static com.bk.print.service.PrintServiceCommons.MSG_SUBMIT_JOB;
import static com.bk.print.service.PrintServiceCommons.MSG_UNREGISTER_LISTENER;

public class PrintServiceClient {
    public interface PrintObserver {
        void onPrinterAddress(BluetoothAddress address);

        void onStateChanged(PrintServiceState state);

        void onJobSubmitted(JobDescriptor job);

        void onJobStarted(UUID jobId);

        void onJobProgress(UUID jobId, int progress);

        void onJobCompleted(UUID jobId);

        void onJobAborted(UUID jobId);

        void onJobError(UUID jobId,String errorCode);

        void onJobsList(List<JobDescriptor> jobs);
    }

    private interface ObserverVisitor {
        void visit(PrintObserver observer);
    }


    private Messenger mServiceMessenger = null;
    private List<PrintObserver> mObservers = new ArrayList<PrintObserver>();


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
                        case MSG_NOTIFY_PRINTER_ADDRESS: {
                            Bundle data = msg.getData();
                            data.setClassLoader(BluetoothAddress.class.getClassLoader());
                            BluetoothAddress printerAddress = data.getParcelable(MSG_PRINTER_BTADDR_KEY);
                            onPrinterAddress(printerAddress);
                            break;
                        }
                        case MSG_NOTIFY_JOB_SUBMITTED:
                            JobDescriptor job = (JobDescriptor) msg.getData().getSerializable(MSG_DESCRIPTOR_KEY);
                            onJobSubmitted(job);
                            break;
                        case MSG_NOTIFY_JOB_STARTED:
                            onJobStarted((UUID) msg.getData().getSerializable(MSG_JOBID_KEY));
                            break;
                        case MSG_NOTIFY_JOB_PROGRESS:
                            Bundle data = msg.getData();
                            onJobProgress((UUID) data.getSerializable(MSG_JOBID_KEY), data.getInt(MSG_PROGRESS_KEY));
                            break;

                        case MSG_NOTIFY_JOB_COMPLETED:
                            onJobCompleted((UUID) msg.getData().getSerializable(MSG_JOBID_KEY));
                            break;

                        case MSG_NOTIFY_JOB_ABORTED:
                            onJobAborted((UUID) msg.getData().getSerializable(MSG_JOBID_KEY));
                            break;

                        case MSG_NOTIFY_JOB_ERROR:
                            onJobError((UUID) msg.getData().getSerializable(MSG_JOBID_KEY),msg.getData().getString(MSG_ERROR_CODE));
                            break;

                        case MSG_NOTIFY_JOBS_LIST:
                            onJobsList((List<JobDescriptor>) msg.getData().get(MSG_JOBSLIST_KEY));
                            break;

                        default:
                            super.handleMessage(msg);
                    }
                }
            });

    private void onJobsList(final List<JobDescriptor> jobs) {
        visitObservers(new ObserverVisitor() {
            @Override
            public void visit(PrintObserver observer) {
                observer.onJobsList(jobs);
            }
        });
    }



    private void onJobError(final UUID jobId,final String errorCd) {
        visitObservers(new ObserverVisitor() {
            @Override
            public void visit(PrintObserver observer) {
                observer.onJobError(jobId,errorCd);
            }
        });
    }

    private void onJobAborted(final UUID jobId) {
        visitObservers(new ObserverVisitor() {
            @Override
            public void visit(PrintObserver observer) {
                observer.onJobAborted(jobId);
            }
        });
    }

    private void onJobCompleted(final UUID jobId) {
        visitObservers(new ObserverVisitor() {
            @Override
            public void visit(PrintObserver observer) {
                observer.onJobCompleted(jobId);
            }
        });
    }

    private void onJobProgress(final UUID jobId, final int progress) {
        visitObservers(new ObserverVisitor() {
            @Override
            public void visit(PrintObserver observer) {
                observer.onJobProgress(jobId, progress);
            }
        });
    }

    private void onJobStarted(final UUID jobId) {
        visitObservers(new ObserverVisitor() {
            @Override
            public void visit(PrintObserver observer) {
                observer.onJobStarted(jobId);
            }
        });
    }

    private void onJobSubmitted(final JobDescriptor jobDescriptor) {
        visitObservers(new ObserverVisitor() {
            @Override
            public void visit(PrintObserver observer) {
                observer.onJobSubmitted(jobDescriptor);
            }
        });
    }

    private void onPrinterAddress(final BluetoothAddress address) {
        visitObservers(new ObserverVisitor() {
            @Override
            public void visit(PrintObserver observer) {
                observer.onPrinterAddress(address);
            }
        });
    }

    public PrintServiceClient(IBinder binder) {
        this. mServiceMessenger = new Messenger(binder);
    }

    public void attachObserver(PrintObserver observer) {
        mObservers.add(observer);
    }

    public void removeObserver(PrintObserver observer) {
        mObservers.remove(observer);
    }

    private boolean isConnectedToService() {
        return mServiceMessenger != null;
    }

    public void getPrinterAddress() {
        sendMessageIfConnected(obtainMessage(MSG_GET_PRINTER_ADDRESS));
    }

    public void setPrinterAddress(BluetoothAddress address) {
        Message msg = obtainMessage(MSG_SET_PRINTER_ADDRESS);
        Bundle data = new Bundle();
        data.putParcelable(MSG_PRINTER_BTADDR_KEY,address);
        msg.setData(data);
        sendMessageIfConnected(msg);
    }

    public void getState() {
        sendMessageIfConnected(obtainMessage(MSG_GET_STATE));
    }

    public void submitJob(DocumentBean document) {
        Message msg = obtainMessage(MSG_SUBMIT_JOB);
        Bundle data = new Bundle();
        data.putSerializable(MSG_DOCUMENT_KEY, document);
        msg.setData(data);
        sendMessageIfConnected(msg);
    }

    public void abortJob(UUID jobId) {
        Message msg = obtainMessage(MSG_ABORT_JOB);
        Bundle data = new Bundle();
        data.putSerializable(MSG_JOBID_KEY, jobId);
        msg.setData(data);
        sendMessageIfConnected(msg);
    }

    public void listJobs() {
        Message msg = obtainMessage(MSG_LIST_JOBS);
        sendMessageIfConnected(msg);
    }

    public void listJobs(UUID[] ids) {
        Message msg = obtainMessage(MSG_LIST_JOBS);
        msg.getData().putSerializable(MSG_JOBIDS_KEY,ids);
        sendMessageIfConnected(msg);
    }


    private void afterServiceCrashed() {
        beforeServiceDisconnected();
    }

    private void visitObservers(ObserverVisitor visitor) {
        for (PrintObserver observer : mObservers)
            visitor.visit(observer);
    }

    private void onConnectionLost() {
        onDisconnected();
    }

    private void onConnectionFailed() {
        onDisconnected();
    }

    private void onDisconnected() {
        visitObservers(new ObserverVisitor() {
            @Override
            public void visit(PrintObserver observer) {
                observer.onStateChanged(PrintServiceState.PrinterNotConnected);
            }
        });
    }

    private void onConnecting() {
        visitObservers(new ObserverVisitor() {
            @Override
            public void visit(PrintObserver observer) {
                observer.onStateChanged(PrintServiceState.PrinterConnecting);
            }
        });
    }

    private void onConnected() {
        visitObservers(new ObserverVisitor() {
            @Override
            public void visit(PrintObserver observer) {
                observer.onStateChanged(PrintServiceState.PrinterConnected);
            }
        });
    }

    public void register() {
        try {
            Message msg = Message.obtain(null, MSG_REGISTER_LISTENER);
            msg.replyTo = mServiceListener;
            mServiceMessenger.send(msg);
        } catch (RemoteException e) {
            afterServiceCrashed();
        }
    }

    public void unregister() {
        if (mServiceMessenger != null) {
            beforeServiceDisconnected();
            sendMessage(obtainMessage(MSG_UNREGISTER_LISTENER));
            mServiceMessenger = null;
        }
    }

    private Message obtainMessage(int what) {

        return Message.obtain(null, what);
    }

    private void sendMessageIfConnected(Message msg) {
        if (isConnectedToService()) {
            sendMessage(msg);
        }
    }

    private void sendMessage(Message msg) {
        try {
            msg.replyTo = mServiceListener;
            mServiceMessenger.send(msg);
        } catch (RemoteException e) {
            // There is nothing special we need to do if the service has crashed.
        }
    }


    private void beforeServiceDisconnected() {
        visitObservers(new ObserverVisitor() {
            @Override
            public void visit(PrintObserver observer) {
                observer.onStateChanged(PrintServiceState.NoService);
            }
        });
    }

}
