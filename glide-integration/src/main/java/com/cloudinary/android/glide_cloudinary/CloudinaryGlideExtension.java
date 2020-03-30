package com.cloudinary.android.glide_cloudinary;

import com.bumptech.glide.annotation.GlideExtension;
import com.bumptech.glide.annotation.GlideOption;
import com.bumptech.glide.request.BaseRequestOptions;
import com.bumptech.glide.request.RequestOptions;
import com.cloudinary.Transformation;

import androidx.annotation.NonNull;

@GlideExtension
public class CloudinaryGlideExtension {

    private CloudinaryGlideExtension() { }

    @NonNull
    @GlideOption
    public static BaseRequestOptions<?> transformation(BaseRequestOptions<?> requestOptions, Transformation transformation) {
        RequestOptions options = new RequestOptions().set(CloudinaryRequestLoader.TRANSFORMATION, transformation);
        return requestOptions.apply(options);
    }
}
