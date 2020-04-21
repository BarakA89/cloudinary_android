package com.cloudinary.android.downloader.glide;

import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.ViewTarget;
import com.cloudinary.android.downloader.ActiveDownloadRequest;

public class GlideActiveDownloadRequest implements ActiveDownloadRequest {

    private final ViewTarget<ImageView, Drawable> target;

    public GlideActiveDownloadRequest(ViewTarget<ImageView, Drawable> target) {
        this.target = target;
    }

    @Override
    public void cancel() {
        ImageView view = target.getView();
        Glide.with(view).clear(view);
    }
}
