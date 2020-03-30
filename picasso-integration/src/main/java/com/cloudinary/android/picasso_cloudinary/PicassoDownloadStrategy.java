package com.cloudinary.android.picasso_cloudinary;

import android.view.View;
import android.widget.ImageView;

import com.cloudinary.Url;
import com.cloudinary.android.CloudinaryRequest;
import com.cloudinary.android.Configuration;
import com.cloudinary.android.DownloadStrategy;
import com.cloudinary.android.MediaManager;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

public class PicassoDownloadStrategy implements DownloadStrategy {

    @Override
    public void download(CloudinaryRequest request, View view) {
        String url = MediaManager.get().url()
                .publicId(request.getPublicId())
                .transformation(request.getTransformation())
                .generate();

        RequestCreator requestCreator = Picasso.get().load(url);

        // todo: abstract this configuration responsibility into an interface
        configureRequest(requestCreator, request.getConfiguration());

        requestCreator.into((ImageView) view);
    }

    private void configureRequest(RequestCreator requestCreator, Configuration configuration) {
        if (configuration.getWidth() != 0 && configuration.getHeight() != 0) {
            requestCreator.resize(configuration.getWidth(), configuration.getHeight());
        }
    }

    @Override
    public void download(Url url, View view) {
        Picasso.get().load(url.generate()).into((ImageView) view);
    }

    @Override
    public void download(Url url, Configuration configuration, View view) {
        RequestCreator requestCreator = Picasso.get().load(url.generate());

        // todo: abstract this configuration responsibility into an interface
        configureRequest(requestCreator, configuration);

        requestCreator.into((ImageView) view);
    }
}
