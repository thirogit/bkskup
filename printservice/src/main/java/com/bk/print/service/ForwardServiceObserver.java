package com.bk.print.service;


import java.util.UUID;

/**
 * Created by IntelliJ IDEA.
 * User: SG0891787
 * Date: 3/13/13
 * Time: 5:16 PM
 */
public class ForwardServiceObserver implements ServiceObserver
{
   @Override
   public void onConnecting() {}

   @Override
   public void onConnected() {}

   @Override
   public void onConnectionFailed() {}

   @Override
   public void onDisconnected() {}

   @Override
   public void onConnectionLost() {}

    @Override
    public void onJobSubmitted(JobDescriptor descriptor) {

    }

    @Override
    public void onJobProgress(UUID jobId, int progress) {

    }

    @Override
    public void onJobError(UUID jobId, String errorCode) {

    }

    @Override
    public void onJobStarted(UUID jobId) {

    }

    @Override
    public void onJobAborted(UUID jobId) {

    }

    @Override
    public void onJobCompleted(UUID jobId) {

    }

}
