package com.cloudinary.android.downloader;

import android.content.Context;
import android.widget.ImageView;

public interface DownloadRequestStrategy {

    DownloadRequestStrategy with(Context context);

    DownloadRequestStrategy load(String url);

    DownloadRequestStrategy placeholder(int placeholder);

    DownloadRequest into(ImageView imageView);
}
