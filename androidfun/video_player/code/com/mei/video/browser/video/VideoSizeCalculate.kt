package com.mei.video.browser.video

import com.mei.orc.ext.dp
import com.mei.orc.ext.screenHeight

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2020/9/14
 */
enum class IjkAspectRatio {
    FIT_PARENT, FIT_WIDTH, FIT_HEIGHT, FULL_PARENT, WRAP_CONTENT, MATCH_PRENT,

    // 抄抖音的缩放模式
    // 1，如果高度为目标时，宽度计算比屏幕还大1/4，则宽度铺满，高度适配，大概率是横屏视频
    // 2，如果高度为目标时，宽度计算比屏幕还小时，则宽度适配，高度铺满，大概率过长的竖屏视频
    // 3，其它情况进行尽可能的铺满缩放，还有种特别的是不在首页，全屏显示的短视频详情是基本很难铺满
    DOUYIN_TYPE
}

fun IjkAspectRatio.doMeasure(viewWidth: Int, viewHeight: Int, videoWidth: Int, videoHeight: Int): Pair<Int, Int> {
    var wSpecSize = viewWidth
    var hSpecSize = viewHeight
    if (videoWidth <= 0 || videoHeight <= 0) {
        return Pair(wSpecSize, hSpecSize)
    }
    val scaleVideoRatio = videoWidth.toFloat() / videoHeight
    val scaleViewRatio = wSpecSize.toFloat() / hSpecSize
    when (this) {
        IjkAspectRatio.FIT_PARENT -> {
            if (scaleVideoRatio > scaleViewRatio) {
                hSpecSize = wSpecSize * videoHeight / videoWidth
            } else {
                wSpecSize = hSpecSize * videoWidth / videoHeight
            }
        }
        IjkAspectRatio.FIT_WIDTH -> {
            hSpecSize = wSpecSize * videoHeight / videoWidth
        }
        IjkAspectRatio.FIT_HEIGHT -> {
            wSpecSize = hSpecSize * videoWidth / videoHeight
        }
        IjkAspectRatio.FULL_PARENT -> {
            if (scaleVideoRatio > scaleViewRatio) {
                wSpecSize = hSpecSize * videoWidth / videoHeight
            } else {
                hSpecSize = wSpecSize * videoHeight / videoWidth

            }
        }
        IjkAspectRatio.WRAP_CONTENT -> {
            if (videoWidth < hSpecSize.toFloat() && videoHeight < hSpecSize.toFloat()) {
                wSpecSize = videoWidth
                hSpecSize = videoHeight
            } else if (scaleVideoRatio > scaleViewRatio) {
                hSpecSize = wSpecSize * videoHeight / videoWidth
            } else {
                wSpecSize = hSpecSize * videoWidth / videoHeight
            }
        }
        IjkAspectRatio.MATCH_PRENT -> {

        }
        IjkAspectRatio.DOUYIN_TYPE -> {
            val maxHeight = screenHeight - 45.dp
            val calWidth = maxHeight * videoWidth / videoHeight
            if (calWidth - wSpecSize > wSpecSize / 4) {
                // 1，如果高度为目标时，宽度计算比屏幕还大1/4，则宽度铺满，高度适配，大概率是横屏视频
                hSpecSize = wSpecSize * videoHeight / videoWidth
            } else {
                // 2，如果高度为目标时，宽度计算比屏幕还小时，则宽度适配，高度铺满，大概率过长的竖屏视频
                // 3，其它情况进行尽可能的铺满缩放，还有种特别的是不在首页，全屏显示的短视频详情是基本很难铺满
                wSpecSize = hSpecSize * videoWidth / videoHeight
                if (wSpecSize > viewWidth) wSpecSize = (maxHeight * videoWidth / videoHeight).toInt()
                hSpecSize = wSpecSize * videoHeight / videoWidth
            }

        }
    }
    return Pair(wSpecSize, hSpecSize)
}