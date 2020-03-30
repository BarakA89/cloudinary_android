package com.cloudinary.android;

import android.view.View;

import com.cloudinary.Url;

public interface DownloadStrategy {

    void download(CloudinaryRequest request, View view);

    void download(Url url, View view);

    void download(Url url, Configuration configuration, View view);

}
