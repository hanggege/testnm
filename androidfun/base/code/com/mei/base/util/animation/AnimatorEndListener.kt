package com.mei.base.util.animation

import android.animation.Animator

/**
 *
 * @author Created by Ling on 2019-07-16
 * 动画监听适配器
 */
open class AnimatorEndListener : Animator.AnimatorListener {
    override fun onAnimationRepeat(animation: Animator?) {
        // Do nothing
    }

    override fun onAnimationEnd(animation: Animator?) {
        // Do nothing
    }

    override fun onAnimationCancel(animation: Animator?) {
        // Do nothing
    }

    override fun onAnimationStart(animation: Animator?) {
        // Do nothing
    }
}