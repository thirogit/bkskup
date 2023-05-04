package com.bk.print.service;

import java.util.UUID;

/**
 * Created by IntelliJ IDEA.
 * User: SG0891787
 * Date: 3/13/13
 * Time: 9:32 AM
 */
public interface ServiceObserver {
    void onConnecting();

    void onConnected();

    void onConnectionFailed();

    void onDisconnected();

    void onConnectionLost();

    void onJobSubmitted(JobDescriptor descriptor);

    void onJobProgress(UUID jobId, int progress);

    void onJobError(UUID jobId, String errorCode);

    void onJobStarted(UUID jobId);

    void onJobAborted(UUID jobId);

    void onJobCompleted(UUID jobId);
}
