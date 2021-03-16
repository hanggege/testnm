package com.mei.widget.living

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2018/8/23
 *
 *
 * 用来兼容LivingView放在列表中的问题
 * 因为设置setVisibility会停止动画，而列表需要复用，所以经常出现停顿的情况
 */
class LivingItemView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : FrameLayout(context, attrs, defStyleAttr) {
    private var livingView: LivingView? = null

    init {
        livingView = LivingView(context)
        addView(livingView)
    }

    override fun setVisibility(visibility: Int) {
        super.setVisibility(visibility)
        if (visibility == View.VISIBLE) {
            livingView?.showLiving(true)
        }
    }

    override fun removeAllViews() {
        super.removeAllViews()
        livingView = null
    }

    fun livingView(): LivingView? {
        return livingView
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        visibility = visibility
    }
}
