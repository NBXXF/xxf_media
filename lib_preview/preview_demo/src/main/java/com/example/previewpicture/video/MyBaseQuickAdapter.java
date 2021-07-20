package com.example.previewpicture.video;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.example.previewpicture.R;
import com.example.previewpicture.bean.UserViewInfo;
import com.example.previewpicture.databinding.ItemImageBinding;
import com.xxf.view.recyclerview.adapter.XXFRecyclerAdapter;
import com.xxf.view.recyclerview.adapter.XXFViewHolder;


/**
 * Created by yangc on 2017/8/29.

 * Deprecated:
 */

public class MyBaseQuickAdapter extends XXFRecyclerAdapter<ItemImageBinding,UserViewInfo> {
    public static final String TAG = "MyBaseQuickAdapter";

  private  Context context;
    public MyBaseQuickAdapter(Context context) {
        this.context=context;
    }


    @Override
    protected ItemImageBinding onCreateBinding(LayoutInflater inflater, ViewGroup viewGroup, int viewType) {
        return ItemImageBinding.inflate(inflater,viewGroup,false);
    }

    @Override
    public void onBindHolder(XXFViewHolder<ItemImageBinding, UserViewInfo> holder, @Nullable UserViewInfo item, int index) {
        final ImageView thumbView = holder.getBinding().iv;
        if (item.getVideoUrl()==null){
            holder.getBinding().btnVideo.setVisibility(View.GONE);
        }else {
            holder.getBinding().btnVideo.setVisibility(View.VISIBLE);
        }

        Glide.with(context)
                .load(item.getUrl())
                .placeholder(R.mipmap.ic_launcher)
                .error(R.mipmap.ic_iamge_zhanwei)
                .into(thumbView);
        thumbView.setTag(R.id.iv,item.getUrl());
    }
}
