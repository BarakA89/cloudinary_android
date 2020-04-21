package com.cloudinary.android.downloader.glide;

import android.content.Context;

import com.cloudinary.android.downloader.DownloadRequestBuilder;
import com.cloudinary.android.downloader.DownloadRequestFactory;

public class GlideDownloadRequestFactory implements DownloadRequestFactory {

    @Override
    public DownloadRequestBuilder createNewRequest(Context context) {
        return new DownloadRequestBuilder(context, new GlideDownloadRequestStrategy());
    }
}
