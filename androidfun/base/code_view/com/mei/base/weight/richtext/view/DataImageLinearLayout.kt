package com.mei.base.weight.richtext.view

import android.content.Context
import android.graphics.Bitmap
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.LinearLayout

/**
 * Created by Ling on 2018/4/12.
 *
 * @ 描述：可以存放Bitmap和Path等信息
 */
class DataImageLinearLayout @JvmOverloads constructor(context: Context, attributeSet: AttributeSet? = null, defStyleAttr: Int = 0)
    : LinearLayout(context, attributeSet, defStyleAttr) {

    var absolutePath: String? = null

    var bitmap: Bitmap? = null

    //当内部包裹BigImageView的时候，BigImageView会抢夺外部控件的点击事件，所以，拦截一下
    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        return true
    }
}
