package com.xxf.media.previewer.ui


import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.xxf.media.previewer.Config
import com.xxf.media.previewer.databinding.XxfActivityPreviewBinding
import com.xxf.media.previewer.model.PreviewParam

/**
 * @Author: XGod  xuanyouwu@163.com  17611639080  https://github.com/NBXXF     https://blog.csdn.net/axuanqq
 * date createTime：2021/9/13
 * Description ://
 */
class PreviewActivity : AppCompatActivity() {
    private lateinit var params: PreviewParam;
    private lateinit var imageViewPagerAdapter: FragmentStatePagerAdapter
    private val fragmentMap = hashMapOf<Int, Fragment>()
    private val binding by lazy {
        XxfActivityPreviewBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        supportPostponeEnterTransition()
        initView()
    }

    private fun initView() {
        params = intent.getSerializableExtra(Config.PREVIEW_PARAM) as PreviewParam
        //设置全屏
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        imageViewPagerAdapter =
            object : FragmentStatePagerAdapter(
                supportFragmentManager,
                BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
            ) {
                override fun getCount(): Int {
                    return params.urls.size
                }

                override fun getItem(position: Int): Fragment {
                    val fragment = fragmentMap[position]
                    return if (fragment == null) {
                        val imageFragment = PreviewFragment()
                        imageFragment.arguments = Bundle().apply {
                            putSerializable(Config.PREVIEW_PARAM, params.urls.get(position))
                        }
                        fragmentMap[position] = imageFragment
                        imageFragment
                    } else {
                        fragment
                    }
                }
            }

        binding.viewPager.adapter = imageViewPagerAdapter
        if (params.currentIndex >= 0 && params.currentIndex < params.urls.size) {
            binding.viewPager.setCurrentItem(params.currentIndex, false)
            binding.viewPager.transitionName = params.sharedElementName
        }
    }
}