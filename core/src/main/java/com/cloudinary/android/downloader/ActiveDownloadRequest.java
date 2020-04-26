package com.cloudinary.android.downloader;

import android.widget.ImageView;

import java.lang.ref.WeakReference;

import androidx.annotation.VisibleForTesting;

/**
 * A wrapper for the {@link DownloadRequest} which basically doesn't start when a request does not have a url prior starting.
 * Once the url is set, the download will start if {@link #start()} was called beforehand.
 *
 * <p>Note: This class is intended for a one time use. Therefore, subsequent calls to {@link #setUrl(String)} and {@link #start()}
 * will not alter the request's state nor it will not be restarted.</p>
 */
public class ActiveDownloadRequest {

    private DownloadRequestStrategy downloadRequestStrategy;
    private WeakReference<ImageView> imageViewRef;
    private DownloadRequest downloadRequest;
    private String url;
    private boolean shouldStart;
    private boolean isCancelled;

    @VisibleForTesting(otherwise = VisibleForTesting.PACKAGE_PRIVATE)
    public ActiveDownloadRequest(DownloadRequestStrategy downloadRequestStrategy, ImageView imageView) {
        this.downloadRequestStrategy = downloadRequestStrategy;
        imageViewRef = new WeakReference<>(imageView);
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PACKAGE_PRIVATE)
    public void setUrl(String url) {
        if (this.url == null) {
            this.url = url;

            if (shouldStart) {
                start();
            }
        }
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PACKAGE_PRIVATE)
    public ActiveDownloadRequest start() {
        if (!isCancelled && downloadRequest == null) {
            shouldStart = true;

            if (url != null) {
                downloadRequest = downloadRequestStrategy.into(imageViewRef.get());
            }
        }

        return this;
    }

    public void cancel() {
        if (downloadRequest != null) {
            downloadRequest.cancel();
        }
        isCancelled = true;
    }

    public boolean isCancelled() {
        return isCancelled;
    }
}
