package com.cloudinary.android.downloader.glide;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.request.target.ViewTarget;
import com.cloudinary.android.downloader.DownloadRequest;
import com.cloudinary.android.downloader.DownloadRequestStrategy;

public class GlideDownloadRequestStrategy implements DownloadRequestStrategy {

    private RequestBuilder<Drawable> requestBuilder;

    @Override
    public DownloadRequestStrategy with(Context context) {
        requestBuilder = Glide.with(context).asDrawable();
        return this;
    }

    @Override
    public DownloadRequestStrategy load(String url) {
        requestBuilder.load(url);
        return this;
    }

    @Override
    public DownloadRequestStrategy placeholder(int placeholder) {
        requestBuilder.placeholder(placeholder);
        return this;
    }

    @Override
    public DownloadRequest into(ImageView imageView) {
        ViewTarget<ImageView, Drawable> target = requestBuilder.into(imageView);

        return new GlideDownloadRequest(target);
    }
}
