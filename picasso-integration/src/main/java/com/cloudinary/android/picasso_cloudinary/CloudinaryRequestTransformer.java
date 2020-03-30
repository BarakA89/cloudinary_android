package com.cloudinary.android.picasso_cloudinary;

import android.net.Uri;

import com.cloudinary.Transformation;
import com.cloudinary.Url;
import com.cloudinary.android.MediaManager;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Request;

import java.util.regex.Pattern;

public class CloudinaryRequestTransformer implements Picasso.RequestTransformer {

    private String publicId;
    private Transformation transformation;

    public CloudinaryRequestTransformer(String publicId) {
        this.publicId = publicId;
    }

    public CloudinaryRequestTransformer(String publicId, Transformation transformation) {
        this.publicId = publicId;
        this.transformation = transformation;
    }

    @Override
    public Request transformRequest(Request request) {
        Uri uri = request.uri;
        if (uri == null) {
            throw new IllegalArgumentException("Null uri passed to " + getClass().getCanonicalName());
        }
        if (!isValidCloudinaryRequest(uri)) {
            return request;
        }

        Url url = MediaManager.get().url().publicId(publicId).transformation(transformation);

        return new Request.Builder(Uri.parse(url.generate())).build();
    }

    private boolean isValidCloudinaryRequest(Uri uri) {
        String scheme = uri.getScheme();
        String host = uri.getHost();

        return ("https".equals(scheme) || "http".equals(scheme)) &&
                host != null && host.contains("res.cloudinary.com");
    }

    private String getCloudinaryPublicId(Uri uri) {
        String publicId = null;

        if (uri.getLastPathSegment() != null) {
            publicId = uri.getLastPathSegment().split(Pattern.quote("."))[0];
        }

        return publicId;
    }
}
