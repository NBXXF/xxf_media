package com.xxf.media.preview;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import androidx.annotation.CallSuper;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.xxf.media.preview.R;
import com.xxf.media.preview.view.BasePhotoFragment;
import com.xxf.media.preview.wight.BezierBannerView;
import com.xxf.media.preview.wight.PhotoViewPager;
import com.xxf.media.preview.wight.SmoothImageView;
import com.xxf.media.preview.enitity.IThumbViewInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yangc on 2017/4/26.
 * <p>
 * Deprecated:图片预览页面
 */
public class GPreviewActivity extends FragmentActivity {
    private static final String TAG = GPreviewActivity.class.getName();
    protected boolean isTransformOut = false;
    /*** 图片的地址***/
    private List<IThumbViewInfo> imgUrls;
    /*** 当前图片的位置 ***/
    protected int currentIndex;
    /*** 图片的展示的Fragment***/
    private List<BasePhotoFragment> fragments = new ArrayList<>();
    /*** 展示图片的viewPager ***/
    private PhotoViewPager viewPager;
    /*** 显示图片数**/
    private TextView ltAddDot;
    /***指示器控件**/
    private BezierBannerView bezierBannerView;
    /***指示器类型枚举***/
    private GPreviewBuilder.IndicatorType type;
    /***默认显示***/
    private boolean isShow = true;

    @CallSuper
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData();
        if (setContentLayout() == 0) {
            setContentView(R.layout.activity_image_preview_photo);
        } else {
            setContentView(setContentLayout());
        }
        initView();
    }

    @Override
    protected void onDestroy() {
        ZoomMediaLoader.getInstance().getLoader().clearMemory(this);
        if (viewPager != null) {
            viewPager.setAdapter(null);
            viewPager.clearOnPageChangeListeners();
            viewPager.removeAllViews();
            viewPager = null;
        }
        if (fragments != null) {
            fragments.clear();
            fragments = null;
        }
        if (imgUrls != null) {
            imgUrls.clear();
            imgUrls = null;
        }
        super.onDestroy();
    }

    /**
     * 初始化数据
     */
    private void initData() {
        imgUrls = getIntent().getParcelableArrayListExtra("imagePaths");
        currentIndex = getIntent().getIntExtra("position", -1);
        type = (GPreviewBuilder.IndicatorType) getIntent().getSerializableExtra("type");
        isShow = getIntent().getBooleanExtra("isShow", true);
        int duration = getIntent().getIntExtra("duration", 300);
        boolean isFullscreen = getIntent().getBooleanExtra("isFullscreen", false);
        boolean isScale = getIntent().getBooleanExtra("isScale", false);
        SmoothImageView.setFullscreen(isFullscreen);
        SmoothImageView.setIsScale(isScale);
        if (isFullscreen) {
            setTheme(android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
        }
        try {
            SmoothImageView.setDuration(duration);
            Class<? extends BasePhotoFragment> sss;
            sss = (Class<? extends BasePhotoFragment>) getIntent().getSerializableExtra("className");
            iniFragment(imgUrls, currentIndex, sss);
        } catch (Exception e) {
            iniFragment(imgUrls, currentIndex, BasePhotoFragment.class);
        }

    }

    /**
     * 初始化
     *
     * @param imgUrls      集合
     * @param currentIndex 选中索引
     * @param className    显示Fragment
     **/
    protected void iniFragment(List<IThumbViewInfo> imgUrls, int currentIndex, Class<? extends BasePhotoFragment> className) {
        if (imgUrls != null) {
            int size = imgUrls.size();
            for (int i = 0; i < size; i++) {
                fragments.add(BasePhotoFragment.
                        getInstance(className, imgUrls.get(i),
                                currentIndex == i,
                                getIntent().getBooleanExtra("isSingleFling", false),
                                getIntent().getBooleanExtra("isDrag", false),
                                getIntent().getFloatExtra("sensitivity", 0.5f))
                );
            }
        } else {
            finish();
        }
    }

    /**
     * 初始化控件
     */
    @SuppressLint("StringFormatMatches")
    private void initView() {
        viewPager = findViewById(R.id.viewPager);
        //viewPager的适配器
        PhotoPagerAdapter adapter = new PhotoPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(currentIndex);
        viewPager.setOffscreenPageLimit(3);
        bezierBannerView = findViewById(R.id.bezierBannerView);
        ltAddDot = findViewById(R.id.ltAddDot);
        if (type == GPreviewBuilder.IndicatorType.Dot) {
            bezierBannerView.setVisibility(View.VISIBLE);
            bezierBannerView.attachToViewpager(viewPager);
        } else {
            ltAddDot.setVisibility(View.VISIBLE);
            ltAddDot.setText(getString(R.string.string_count, (currentIndex + 1), imgUrls.size()));
            viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
                    //当被选中的时候设置小圆点和当前位置
                    if (ltAddDot != null) {
                        ltAddDot.setText(getString(R.string.string_count, (position + 1), imgUrls.size()));
                    }
                    currentIndex = position;
                    viewPager.setCurrentItem(currentIndex, true);
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });
        }
        if (fragments.size() == 1) {
            if (!isShow) {
                bezierBannerView.setVisibility(View.GONE);
                ltAddDot.setVisibility(View.GONE);
            }
        }
        viewPager.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                viewPager.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                BasePhotoFragment fragment = fragments.get(currentIndex);
                fragment.transformIn();
            }
        });


    }

    /***退出预览的动画***/
    public void transformOut() {
        if (isTransformOut) {
            return;
        }
        getViewPager().setEnabled(false);
        isTransformOut = true;
        int currentItem = viewPager.getCurrentItem();
        if (currentItem < imgUrls.size()) {
            BasePhotoFragment fragment = fragments.get(currentItem);
            if (ltAddDot != null) {
                ltAddDot.setVisibility(View.GONE);
            } else {
                bezierBannerView.setVisibility(View.GONE);
            }
            fragment.changeBg(Color.TRANSPARENT);
            fragment.transformOut(new SmoothImageView.onTransformListener() {
                @Override
                public void onTransformCompleted(SmoothImageView.Status status) {
                    getViewPager().setEnabled(true);
                    exit();
                }
            });
        } else {
            exit();
        }
    }

    @Override
    public void finish() {
        BasePhotoFragment.listener = null;
        super.finish();
    }

    /***
     * 得到PhotoFragment集合
     * @return List
     * **/
    public List<BasePhotoFragment> getFragments() {
        return fragments;
    }

    /**
     * 关闭页面
     */
    private void exit() {
        finish();
        overridePendingTransition(0, 0);
    }

    /***
     * 得到PhotoViewPager
     * @return PhotoViewPager
     * **/
    public PhotoViewPager getViewPager() {
        return viewPager;
    }

    /***
     * 自定义布局内容
     * @return int
     ***/
    public int setContentLayout() {
        return 0;
    }

    @Override
    public void onBackPressed() {
        isTransformOut = false;
        transformOut();
    }

    /**
     * pager的适配器
     */
    private class PhotoPagerAdapter extends FragmentStatePagerAdapter {

        PhotoPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments == null ? 0 : fragments.size();
        }

    }


}
