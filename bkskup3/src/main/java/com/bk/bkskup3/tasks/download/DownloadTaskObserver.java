package com.bk.bkskup3.tasks.download;

public interface DownloadTaskObserver<T> {
    void onLoadStarted();

    void onLoadSuccessful(T result);

    void onLoadError(Exception e);
}
