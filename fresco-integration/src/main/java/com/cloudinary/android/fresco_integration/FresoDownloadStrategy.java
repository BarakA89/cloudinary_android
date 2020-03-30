package com.cloudinary.android.fresco_integration;

import android.net.Uri;
import android.view.View;

import com.cloudinary.Url;
import com.cloudinary.android.CloudinaryRequest;
import com.cloudinary.android.Configuration;
import com.cloudinary.android.DownloadStrategy;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.DraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;

public class FresoDownloadStrategy implements DownloadStrategy {

    @Override
    public void download(CloudinaryRequest request, View view) {
        ImageRequestBuilder imageRequestBuilder = new CloudinaryImageRequestBuilder(request.getPublicId())
                .setTransformation(request.getTransformation())
                .newBuilderFromCloudinarySource();

        configureRequest(request.getConfiguration(), imageRequestBuilder);

        loadImageRequest((DraweeView) view, imageRequestBuilder.build());
    }

    @Override
    public void download(Url url, View view) {
        ImageRequest imageRequest = ImageRequestBuilder.newBuilderWithSource(Uri.parse(url.generate())).build();
        loadImageRequest((DraweeView) view, imageRequest);
    }

    @Override
    public void download(Url url, Configuration configuration, View view) {
        ImageRequestBuilder imageRequestBuilder = ImageRequestBuilder.newBuilderWithSource(Uri.parse(url.generate()));

        configureRequest(configuration, imageRequestBuilder);

        loadImageRequest((DraweeView) view, imageRequestBuilder.build());
    }

    private void configureRequest(Configuration configuration, ImageRequestBuilder imageRequestBuilder) {
        // todo: abstract this configuration responsibility into an interface
        if (configuration != null) {
            if (configuration.getWidth() != 0 && configuration.getHeight() != 0) {
                imageRequestBuilder.setResizeOptions(new ResizeOptions(configuration.getWidth(), configuration.getHeight()));
            }
        }
    }

    private void loadImageRequest(DraweeView view, ImageRequest imageRequest) {
        DraweeController draweeController = Fresco.newDraweeControllerBuilder()
                .setImageRequest(imageRequest)
                .build();

        view.setController(draweeController);
    }

}
