package com.mei.orc.ext

import android.animation.Animator
import android.view.ViewPropertyAnimator

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2018/6/21
 */

fun ViewPropertyAnimator.setListenerEnd(finish: () -> Unit = {}): ViewPropertyAnimator = setListener { finish() }

fun ViewPropertyAnimator.setListener(doStart: (Animator?) -> Unit = {},
                                     doRepeat: (Animator?) -> Unit = {},
                                     doCancel: (Animator?) -> Unit = {},
                                     doEnd: (Animator?) -> Unit = {}): ViewPropertyAnimator =
        setListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator?) {
                doStart(animation)
            }

            override fun onAnimationEnd(animation: Animator?) {
                doEnd(animation)
            }

            override fun onAnimationCancel(animation: Animator?) {
                doCancel(animation)
            }

            override fun onAnimationRepeat(animation: Animator?) {
                doRepeat(animation)
            }

        })