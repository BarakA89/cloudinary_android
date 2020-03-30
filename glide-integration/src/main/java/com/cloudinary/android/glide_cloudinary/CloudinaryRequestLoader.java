package com.cloudinary.android.glide_cloudinary;

import com.bumptech.glide.load.Option;
import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.ModelLoader;
import com.bumptech.glide.load.model.ModelLoaderFactory;
import com.bumptech.glide.load.model.MultiModelLoaderFactory;
import com.cloudinary.Transformation;
import com.cloudinary.android.CloudinaryRequest;
import com.cloudinary.android.MediaManager;

import java.io.InputStream;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class CloudinaryRequestLoader implements ModelLoader<CloudinaryRequest, InputStream> {

    static final Option<Transformation> TRANSFORMATION =
            Option.memory("com.cloudinary.android.glide_cloudinary.CloudinaryRequestLoader.Transformation");

    private ModelLoader<GlideUrl, InputStream> urlLoader;

    private CloudinaryRequestLoader(ModelLoader<GlideUrl, InputStream> urlLoader) {
        this.urlLoader = urlLoader;
    }

    @Nullable
    @Override
    public LoadData<InputStream> buildLoadData(@NonNull CloudinaryRequest model, int width, int height, @NonNull Options options) {
        Transformation transformation = model.getTransformation();
        if (transformation == null) {
            transformation = options.get(TRANSFORMATION);
        }
        String url = MediaManager.get().url().publicId(model.getPublicId()).transformation(transformation).generate();

        return urlLoader.buildLoadData(new GlideUrl(url), width, height, options);
    }

    @Override
    public boolean handles(@NonNull CloudinaryRequest model) {
        return true;
    }

    public static class Factory implements ModelLoaderFactory<CloudinaryRequest, InputStream> {

        @NonNull
        @Override
        public ModelLoader<CloudinaryRequest, InputStream> build(@NonNull MultiModelLoaderFactory multiFactory) {
            return new CloudinaryRequestLoader(multiFactory.build(GlideUrl.class, InputStream.class));
        }

        @Override
        public void teardown() {

        }
    }
}