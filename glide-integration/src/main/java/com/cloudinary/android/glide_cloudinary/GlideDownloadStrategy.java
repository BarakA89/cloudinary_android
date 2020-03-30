package com.cloudinary.android.glide_cloudinary;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.cloudinary.Url;
import com.cloudinary.android.CloudinaryRequest;
import com.cloudinary.android.Configuration;
import com.cloudinary.android.DownloadStrategy;
import com.cloudinary.android.MediaManager;

public class GlideDownloadStrategy implements DownloadStrategy {

    @SuppressLint("CheckResult")
    @Override
    public void download(CloudinaryRequest cloudinaryRequest, View view) {
        String url = MediaManager.get().url()
                .publicId(cloudinaryRequest.getPublicId())
                .transformation(cloudinaryRequest.getTransformation())
                .generate();

        RequestBuilder<Drawable> request = Glide.with(view).load(url);

        // todo: abstract this configuration responsibility into an interface
        configureRequest(request, cloudinaryRequest.getConfiguration());

        request.into((ImageView) view);
    }

    @Override
    public void download(Url url, View view) {
        Glide.with(view).load(url.generate()).into((ImageView) view);
    }

    @Override
    public void download(Url url, Configuration configuration, View view) {
        RequestBuilder<Drawable> request = Glide.with(view).load(url.generate());

        // todo: abstract this configuration responsibility into an interface
        configureRequest(request, configuration);

        request.into((ImageView) view);
    }

    private void configureRequest(RequestBuilder<Drawable> request, Configuration configuration) {
        if (configuration != null) {
            if (configuration.getWidth() != 0 && configuration.getHeight() != 0) {
                request.override(configuration.getWidth(), configuration.getHeight());
            }
        }
    }

}
