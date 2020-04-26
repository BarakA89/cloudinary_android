package com.cloudinary.android.downloader.glide;

import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.ViewTarget;
import com.cloudinary.android.downloader.DownloadRequest;

public class GlideDownloadRequest implements DownloadRequest {

    private final ViewTarget<ImageView, Drawable> target;

    public GlideDownloadRequest(ViewTarget<ImageView, Drawable> target) {
        this.target = target;
    }

    @Override
    public void cancel() {
        ImageView view = target.getView();
        Glide.with(view).clear(view);
    }
}
