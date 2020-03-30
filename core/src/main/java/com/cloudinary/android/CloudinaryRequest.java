package com.cloudinary.android;

import com.cloudinary.Transformation;

public class CloudinaryRequest {

    private String publicId;
    private Transformation transformation;
    private Configuration configuration;

    private CloudinaryRequest(String publicId, Transformation transformation, Configuration configuration) {
        this.publicId = publicId;
        this.transformation = transformation;
        this.configuration = configuration;
    }

    public String getPublicId() {
        return publicId;
    }

    public Transformation getTransformation() {
        return transformation;
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public static class Builder {

        private final String publicId;
        private Transformation transformation;
        private Configuration configuration;

        public Builder(String publicId) {
            this.publicId = publicId;
        }

        public Builder transformation(Transformation transformation) {
            this.transformation = transformation;
            return this;
        }

        public Builder configuration(Configuration configuration) {
            this.configuration = configuration;
            return this;
        }

        public CloudinaryRequest build() {
            return new CloudinaryRequest(publicId, transformation, configuration);
        }
    }
}