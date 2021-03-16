package com.mei.picker.view

import android.content.Context
import android.util.AttributeSet
import android.widget.ImageView

/**
 * Created by hang on 2019-12-24.
 */
class SquareImageView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
    : ImageView(context, attrs, defStyleAttr) {

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec)
    }
}