package com.xxf.media.previewer.model.url;

import java.io.Serializable;

/**
 * @Author: XGod  xuanyouwu@163.com  17611639080  https://github.com/NBXXF     https://blog.csdn.net/axuanqq
 * date createTime：2021/9/13
 * Description :
 */
public class ImageUrl implements Serializable {
    private String url;

    public String getUrl() {
        return url;
    }

    public ImageUrl(String url) {
        this.url = url;
    }
}
