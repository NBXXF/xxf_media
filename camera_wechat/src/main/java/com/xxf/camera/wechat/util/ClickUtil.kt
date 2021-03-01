package com.xxf.camera.wechat.util


class ClickUtil {
    companion object {
        const val MIN_DELAY_TIME = 500L
        var lastClickTime:Long = 0
        fun isFastClick():Boolean{
            var flag = true
            var currentClickTime = System.currentTimeMillis()
            if ((currentClickTime - lastClickTime) >= MIN_DELAY_TIME) {
                flag = false
            }
            lastClickTime = currentClickTime
            return flag
        }
    }
}