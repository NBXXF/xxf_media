package com.xxf.media.previewer.demo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.xxf.media.previewer.PreviewBuilder
import com.xxf.media.previewer.demo.databinding.ActivityMainBinding
import com.xxf.media.previewer.model.url.ImageThumbAutoOriginUrl
import com.xxf.media.previewer.model.url.ImageUrl
import com.xxf.media.previewer.model.url.VideoImageUrl

class MainActivity : AppCompatActivity() {
    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)


        val urls = mutableListOf<ImageUrl>()
        urls.add(ImageThumbAutoOriginUrl("https://cdn.allflow.cn/2021/09/13/7757984b-727c-4977-904d-0732f96d90f0/Screenshot_20210504_092135_notion.id.jpg?filename=Screenshot_20210504_092135_notion.id.jpg&original=true&x-oss-process=image/resize,w_100/quality,q_90","https://cdn.allflow.cn/2021/09/13/7757984b-727c-4977-904d-0732f96d90f0/Screenshot_20210504_092135_notion.id.jpg?filename=Screenshot_20210504_092135_notion.id.jpg&original=true"))
        urls.add(ImageUrl("https://hellorfimg.zcool.cn/preview260/1156349101.jpg"))
        urls.add(VideoImageUrl("https://hellorfimg.zcool.cn/preview260/1156349101.jpg","http://lmp4.vjshi.com/2018-06-07/cf673556cce54ab9cf4633fd7d9d0d46.mp4"))
        urls.add(ImageUrl("https://hellorfimg.zcool.cn/preview260/298757792.jpg"))
        urls.add(ImageUrl("https://hellorfimg.zcool.cn/preview260/587590211.jpg"))
        urls.add(ImageUrl("https://hellorfimg.zcool.cn/preview260/407797777.jpg"))
        urls.add(ImageUrl("https://hellorfimg.zcool.cn/preview260/675219493.jpg"))
        urls.add(ImageUrl("https://hellorfimg.zcool.cn/preview260/1045107625.jpg"))
        urls.add(ImageUrl("https://hellorfimg.zcool.cn/preview260/551576716.jpg"))
        urls.add(ImageUrl("https://hellorfimg.zcool.cn/preview260/1016991457.jpg"))
        val adapter = TestImageAdapter()
        adapter.bindData(true, urls)
        binding.recyclerview.adapter = adapter

        adapter.setOnItemClickListener { adapter, holder, itemView, index, item ->
            holder!!.binding!!.image.transitionName = "aaa"
            PreviewBuilder(this).setUrls(urls)
                .setCurrentIndex(index)
                .start(holder!!.binding!!.image, "" + index)
        }
    }
}