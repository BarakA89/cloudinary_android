package com.cloudinary.android;

import android.widget.ImageView;

import com.cloudinary.android.downloader.ActiveDownloadRequest;
import com.cloudinary.android.downloader.DownloadRequest;
import com.cloudinary.android.downloader.DownloadRequestStrategy;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ActiveDownloadRequestTest {

    private static String url = "url";

    @Mock
    private ImageView imageView;
    @Mock
    private DownloadRequestStrategy downloadRequestStrategy;

    private ActiveDownloadRequest sut;

    @Before
    public void setup() {
        sut = new ActiveDownloadRequest(downloadRequestStrategy, imageView);
    }

    @Test
    public void testDownloadStartWithUrl() {
        sut.setUrl(url);
        sut.start();

        verify(downloadRequestStrategy, times(1)).into(imageView);
    }

    @Test
    public void testDownloadStartRightAfterUrlIsSet() {
        sut.start();
        sut.setUrl(url);

        verify(downloadRequestStrategy, times(1)).into(imageView);
    }

    @Test
    public void testDownloadDoesNotStartWithoutUrl() {
        sut.start();

        verify(downloadRequestStrategy, never()).into(imageView);
    }

    @Test
    public void testDownloadDoesNotStartIfCancelled() {
        sut.setUrl(url);
        sut.cancel();
        sut.start();

        verify(downloadRequestStrategy, never()).into(imageView);
    }

    @Test
    public void testDownloadStartOnlyOnce() {
        DownloadRequest downloadRequest = mock(DownloadRequest.class);
        when(downloadRequestStrategy.into(imageView)).thenReturn(downloadRequest);

        sut.start();
        sut.setUrl(url);
        sut.setUrl(url);
        sut.start();

        verify(downloadRequestStrategy, times(1)).into(imageView);
    }

}
