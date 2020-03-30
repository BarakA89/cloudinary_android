package com.cloudinary.android;

public class Configuration {

    protected int width;
    protected int height;

    private Configuration(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public static class Builder {
        private int width;
        private int height;

        public Builder() { }

        public Builder width(int width) {
            this.width = width;
            return this;
        }

        public Builder height(int height) {
            this.height = height;
            return this;
        }

        public Configuration build() {
            return new Configuration(width, height);
        }
    }
}
