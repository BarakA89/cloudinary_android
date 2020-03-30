package com.cloudinary.android.fresco_integration;

import android.net.Uri;

import com.cloudinary.Transformation;
import com.cloudinary.android.MediaManager;
import com.facebook.imagepipeline.request.ImageRequestBuilder;

public class CloudinaryImageRequestBuilder  {

    private String publicId;
    private Transformation transformation;

    public CloudinaryImageRequestBuilder(String publicId) {
        this.publicId = publicId;
    }

    public CloudinaryImageRequestBuilder(String publicId, Transformation transformation) {
        this.publicId = publicId;
        this.transformation = transformation;
    }

    public CloudinaryImageRequestBuilder setTransformation(Transformation transformation) {
        this.transformation = transformation;
        return this;
    }

    public ImageRequestBuilder newBuilderFromCloudinarySource() {
        String url = MediaManager.get().url().publicId(publicId).transformation(transformation).generate();
        return ImageRequestBuilder.newBuilderWithSource(Uri.parse(url));
    }
}
