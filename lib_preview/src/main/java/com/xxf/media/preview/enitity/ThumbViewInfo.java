package com.xxf.media.preview.enitity;

import android.graphics.Rect;
import android.os.Parcel;
import android.view.View;

import androidx.annotation.Nullable;

import com.xxf.media.preview.enitity.IThumbViewInfo;

import java.io.Serializable;

/**
 * @Author: XGod  xuanyouwu@163.com  17611639080
 * Date: 2/25/21 5:27 PM
 * Description: 缩略图预览
 */
public class ThumbViewInfo implements IThumbViewInfo, Serializable {
    private String url;
    private String videoUrl;
    private Rect bounds; // 记录坐标

    public ThumbViewInfo(String url, String videoUrl, Rect bounds) {
        this.url = url;
        this.videoUrl = videoUrl;
        this.bounds = bounds;
    }

    /**
     *
     * @param url
     * @param videoUrl
     * @param transitionView  过渡的view
     */
    public ThumbViewInfo(String url, String videoUrl, @Nullable View transitionView) {
        Rect bounds=new Rect();
        if(transitionView!=null&&transitionView.getParent()!=null) {
            transitionView.getGlobalVisibleRect(bounds);
        }
        this.url = url;
        this.videoUrl = videoUrl;
        this.bounds = bounds;
    }


    public void setUrl(String url) {
        this.url = url;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }


    public void setBounds(Rect mBounds) {
        this.bounds = mBounds;
    }

    @Override
    public String getUrl() {
        return this.url;
    }

    @Override
    public Rect getBounds() {
        return bounds;
    }

    @Nullable
    @Override
    public String getVideoUrl() {
        return videoUrl;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.url);
        dest.writeString(this.videoUrl);
        dest.writeParcelable(this.bounds, flags);
    }

    public void readFromParcel(Parcel source) {
        this.url = source.readString();
        this.videoUrl = source.readString();
        this.bounds = source.readParcelable(Rect.class.getClassLoader());
    }

    public ThumbViewInfo() {
    }

    protected ThumbViewInfo(Parcel in) {
        this.url = in.readString();
        this.videoUrl = in.readString();
        this.bounds = in.readParcelable(Rect.class.getClassLoader());
    }

    public static final Creator<ThumbViewInfo> CREATOR = new Creator<ThumbViewInfo>() {
        @Override
        public ThumbViewInfo createFromParcel(Parcel source) {
            return new ThumbViewInfo(source);
        }

        @Override
        public ThumbViewInfo[] newArray(int size) {
            return new ThumbViewInfo[size];
        }
    };
}
