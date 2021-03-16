package com.mei.radio.view

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageSwitcher
import android.widget.ImageView
import com.mei.wood.R

/**
 *
 * @author Created by lenna on 2020/9/22
 * 图片切换控件，使其我们加载图片时可以让图片平缓的切换
 */
class MeiImageSwitcher @JvmOverloads constructor(context: Context, attr: AttributeSet? = null) : ImageSwitcher(context, attr) {
    init {
        inAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.show_alpha)
        outAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.hide_alpha)
        setFactory {
            ImageView(getContext()).apply {
                scaleType = ImageView.ScaleType.CENTER_CROP
                layoutParams = LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            }
        }
    }
}