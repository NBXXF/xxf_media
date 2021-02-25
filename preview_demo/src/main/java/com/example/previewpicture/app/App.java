package com.example.previewpicture.app;

import android.app.Application;

import com.xxf.media.preview.ZoomMediaLoader;

/**
 * Created by yangc on 2017/9/4.

 * Deprecated:
 */

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        ZoomMediaLoader.getInstance().init(new TestImageLoader());
    }
}
