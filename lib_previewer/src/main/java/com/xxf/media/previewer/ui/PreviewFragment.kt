package com.xxf.media.previewer.ui

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.DrawableImageViewTarget
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory
import com.bumptech.glide.request.transition.Transition
import com.xxf.media.photoview.PhotoViewAttacher
import com.xxf.media.previewer.Config
import com.xxf.media.previewer.databinding.XxfFragmentPreviewBinding
import com.xxf.media.previewer.model.url.ImageThumbAutoOriginUrl
import com.xxf.media.previewer.model.url.ImageUrl
import com.xxf.media.previewer.model.url.VideoImageUrl

/**
 * @Author: XGod  xuanyouwu@163.com  17611639080  https://github.com/NBXXF     https://blog.csdn.net/axuanqq
 * date createTime：2021/9/13
 * Description ://
 */
open class PreviewFragment : Fragment() {
    protected lateinit var url: ImageUrl;
    protected lateinit var binding: XxfFragmentPreviewBinding;
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = XxfFragmentPreviewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        url = arguments!!.getSerializable(Config.PREVIEW_PARAM) as ImageUrl
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.imageView.setOnViewTapListener { view, x, y ->
            requireActivity().onBackPressed()
        }
        binding.imageView.setOnPhotoTapListener(object : PhotoViewAttacher.OnPhotoTapListener {
            override fun onPhotoTap(view: View?, x: Float, y: Float) {
                requireActivity().onBackPressed()
            }

            override fun onOutsidePhotoTap() {
                requireActivity().onBackPressed()
            }
        })
        if (url is ImageThumbAutoOriginUrl) {
            binding.imageView.visibility = View.VISIBLE
            binding.videoView.visibility = View.GONE
            Glide.with(this)
                .load((url as ImageThumbAutoOriginUrl).originUrl)
                .priority(Priority.IMMEDIATE)
                .thumbnail(
                    Glide.with(this)
                        .load(url.url)
                )
                .dontAnimate()
                .into(binding.imageView)
        } else if (url is VideoImageUrl) {
            binding.imageView.visibility = View.GONE
            binding.videoView.visibility = View.VISIBLE
            binding.videoView.setVideoPath((url as VideoImageUrl).sourceUrl)
            binding.videoView.start()
        } else {
            binding.imageView.visibility = View.VISIBLE
            binding.videoView.visibility = View.GONE
            Glide.with(this)
                .load(url.url)
                .priority(Priority.IMMEDIATE)
                .dontAnimate()
                .into(object : DrawableImageViewTarget(binding.imageView) {
                    override fun onResourceReady(
                        resource: Drawable,
                        transition: Transition<in Drawable>?
                    ) {
                        super.onResourceReady(resource, transition)
                        requireActivity().supportStartPostponedEnterTransition()
                    }

                    override fun onLoadFailed(errorDrawable: Drawable?) {
                        super.onLoadFailed(errorDrawable)
                        requireActivity().supportStartPostponedEnterTransition()
                    }
                })
        }
    }

    override fun onResume() {
        super.onResume()
        if (url is ImageThumbAutoOriginUrl) {
            binding.videoView.resume()
        }
    }

    override fun onPause() {
        super.onPause()
        if (url is ImageThumbAutoOriginUrl) {
            binding.videoView.pause()
        }
    }
}