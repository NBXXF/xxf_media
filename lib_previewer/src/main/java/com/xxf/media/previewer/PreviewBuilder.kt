package com.xxf.media.previewer

import android.app.ActivityOptions
import android.content.Intent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.xxf.media.previewer.model.PreviewParam
import com.xxf.media.previewer.model.url.ImageUrl
import com.xxf.media.previewer.ui.PreviewActivity

/**
 * @Author: XGod  xuanyouwu@163.com  17611639080  https://github.com/NBXXF     https://blog.csdn.net/axuanqq
 * date createTime：2021/9/13
 * Description ://预览图片和视频
 */
class PreviewBuilder(private val activity: AppCompatActivity) {
    private var urls: List<ImageUrl> = listOf<ImageUrl>()
    private var currentIndex = 0;

    /**
     * 单个多媒体源  支持id string 自定义模型等
     */
    fun setUrl(url: ImageUrl): PreviewBuilder {
        this.urls = listOf(url);
        return this
    }

    /**
     * 单个多媒体源  支持id string 自定义模型等
     */
    fun setUrls(urls: List<ImageUrl>): PreviewBuilder {
        this.urls = urls;
        return this
    }

    /**
     * 当前位置
     */
    fun setCurrentIndex(index: Int): PreviewBuilder {
        this.currentIndex = index
        return this
    }

    fun start() {
        activity.startActivity(Intent(activity, PreviewActivity::class.java).apply {
            putExtra(
                Config.PREVIEW_PARAM,
                PreviewParam(urls, currentIndex, null)
            )
        })
    }

    /**
     * 共享元素动画
     */
    fun start(sharedElement: View, sharedElementName: String) {
        activity.startActivity(
            Intent(activity, PreviewActivity::class.java).apply {
                putExtra(
                    Config.PREVIEW_PARAM,
                    PreviewParam(urls, currentIndex, sharedElementName)
                )
            },
            ActivityOptions.makeSceneTransitionAnimation(activity, sharedElement, sharedElementName)
                .toBundle()
        )
    }
}