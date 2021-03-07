package com.xxf.media.preview.loader;

import android.content.Context;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

/**
 *
 * @author xxf
 * date 2017/9/4

 * Deprecated: 加载器接口
 */


public interface IZoomMediaLoader {

    /***
     * @param  context 容器
     * @param   path  图片你的路径
     * @param   simpleTarget   图片加载状态回调
     * ***/
    void displayImage(@NonNull Fragment context, @NonNull String path, ImageView imageView, @NonNull MySimpleTarget  simpleTarget);

    /***
     * 加载gif 图
     * @param  context 容器
     * @param   path  图片你的路径
     * @param   simpleTarget   图片加载状态回调
     * ***/
    void displayGifImage(@NonNull Fragment context, @NonNull String path, ImageView imageView, @NonNull MySimpleTarget simpleTarget);
    /**
     * 停止
     *
     * @param context 容器
     **/
    void onStop(@NonNull Fragment context);

    /**
     * 停止
     *
     * @param c 容器
     **/
    void clearMemory(@NonNull Context c);
}
