package com.mei.splash.view

import android.annotation.SuppressLint
import android.content.Context
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import com.mei.orc.ext.dip
import com.mei.wood.R

/**
 *  Created by zzw on 2019-10-21
 *  Des:  splash_layout.xml
 */

@SuppressLint("RtlHardcoded")
class SplashView(context: Context)
    : FrameLayout(context) {

    val pictureView: ImageView by lazy {
        ImageView(context).apply {
            layoutParams = MarginLayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT).apply {
                bottomMargin = dip(120)
            }
            maxWidth = ViewGroup.LayoutParams.MATCH_PARENT
            adjustViewBounds = true
            scaleType = ImageView.ScaleType.FIT_XY
        }
    }

    init {
        layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        setBackgroundResource(R.drawable.splash_background)
        addView(pictureView)
    }

}