package com.cloudinary.android.downloader;

import android.content.Context;
import android.widget.ImageView;

import com.cloudinary.Transformation;
import com.cloudinary.Url;
import com.cloudinary.android.MediaManager;
import com.cloudinary.android.ResponsiveUrl;

import androidx.annotation.DrawableRes;

public class DownloadRequestBuilder {

    private Context context;
    private DownloadRequestStrategy downloadRequestStrategy;
    private String publicId;
    private Transformation transformation;
    private ResponsiveUrl responsive;
    private int placeholder;

    public DownloadRequestBuilder(Context context, DownloadRequestStrategy downloadRequestStrategy) {
        this.context = context;
        this.downloadRequestStrategy = downloadRequestStrategy;
    }

    public DownloadRequestBuilder load(String publicId) {
        this.publicId = publicId;
        return this;
    }

    public DownloadRequestBuilder transformation(Transformation transformation) {
        this.transformation = transformation;
        return this;
    }

    public DownloadRequestBuilder responsive(ResponsiveUrl responsiveUrl) {
        this.responsive = responsiveUrl;
        return this;
    }

    public DownloadRequestBuilder responsive(ResponsiveUrl.Preset responsivePreset) {
        this.responsive = MediaManager.get().responsiveUrl(responsivePreset);
        return this;
    }

    public DownloadRequestBuilder placeholder(@DrawableRes int placeholder) {
        this.placeholder = placeholder;
        return this;
    }

    public ActiveDownloadRequest into(final ImageView imageView) {
        downloadRequestStrategy.with(context);
        final ActiveDownloadRequest activeDownloadRequest = new ActiveDownloadRequest(downloadRequestStrategy, imageView);

        Url url = MediaManager.get().url().publicId(publicId).transformation(transformation);
        if (responsive != null) {
            responsive.generate(url, imageView, new ResponsiveUrl.Callback() {
                @Override
                public void onUrlReady(Url url) {
                    setUrl(activeDownloadRequest, url);
                }
            });
        } else {
            setUrl(activeDownloadRequest, url);
        }

        if (placeholder != 0) {
            downloadRequestStrategy.placeholder(placeholder);
        }

        return activeDownloadRequest.start();
    }

    private void setUrl(ActiveDownloadRequest activeDownloadRequest, Url url) {
        String generatedUrl = url.generate();
        downloadRequestStrategy.load(generatedUrl);
        activeDownloadRequest.setUrl(generatedUrl);
    }
}
