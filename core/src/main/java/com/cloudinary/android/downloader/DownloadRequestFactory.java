package com.cloudinary.android.downloader;

import android.content.Context;

public interface DownloadRequestFactory {
    DownloadRequestBuilder createNewRequest(Context context);
}
