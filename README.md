# Deprecated Deprecated Deprecated
最新 参考 https://github.com/NBXXF/xxf_android
# 相册
```
   AlbumLauncher.from(SampleActivity.this)
                        .choose(MimeType.ofImage(), false)
                        .countable(true)
                        .capture(true)
                        .captureStrategy(
                                new CaptureStrategy(true, "com.zhihu.matisse.sample.fileprovider", "test"))
                        .maxSelectable(9)
                        .addFilter(new GifSizeFilter(320, 320, 5 * Filter.K * Filter.K))
                        .gridExpectedSize(
                                getResources().getDimensionPixelSize(R.dimen.grid_expected_size))
                        .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
                        .thumbnailScale(0.85f)
                        .imageEngine(new GlideEngine())
                        .setOnSelectedListener((uriList, pathList) -> {
                            Log.e("onSelected", "onSelected: pathList=" + pathList);
                        })
                        .showSingleMediaType(true)
                        .originalEnable(true)
                        .maxOriginalSize(10)
                        .autoHideToolbarOnSingleTap(true)
                        .setOnCheckedListener(isChecked -> {
                            Log.e("isChecked", "onCheck: isChecked=" + isChecked);
                        })
                        .forResult(REQUEST_CODE_CHOOSE)
                        .subscribe(new Consumer<AlbumResult>() {
                            @Override
                            public void accept(AlbumResult albumResult) throws Throwable {
                                mAdapter.setData(albumResult.getUris(), albumResult.getPaths());
                            }
                        });
```
# 微信拍摄
```
 CameraLauncher.instance
                    //.openPreCamera()// 是否打开为前置摄像头
                    .allowPhoto(true)// 是否允许拍照 默认允许
                    .allowRecord(true)// 是否允许录像 默认允许
                    .setMaxRecordTime(3)//最长录像时间 秒
                    .forResult(this, PHOTO_OR_VIDEO_FOR_CAMERA)
                    .subscribe {
                        if (it.isImage) {
                            text.text = "Image Path：\n${it.path}"
                        } else {
                            text.text = "Video Path：\n${it.path}"
                        }
                    }
```
