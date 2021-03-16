package com.mei.live.manager

import android.animation.*
import android.view.View

/**
 * Created by hang on 2019/1/7.
 */
class GiftDoubleAnimManager(val whiteView: View,
                            val blueView: View,
                            val redView: View,
                            val repeatCount: Int = 0) {

    val animatorSet = AnimatorSet()

    fun startAnim() {
        val keyframe1White = Keyframe.ofFloat(0f, 1f)
        val keyframe2White = Keyframe.ofFloat(0.4f, 1.3f)
        val keyframe3White = Keyframe.ofFloat(0.7f, 1f)
        val keyframe4White = Keyframe.ofFloat(1f, 1f)
        val holderWhiteX = PropertyValuesHolder.ofKeyframe("scaleX", keyframe1White, keyframe2White, keyframe3White, keyframe4White)
        val holderWhiteY = PropertyValuesHolder.ofKeyframe("scaleY", keyframe1White, keyframe2White, keyframe3White, keyframe4White)
        val animatorWhite = ObjectAnimator.ofPropertyValuesHolder(whiteView, holderWhiteX, holderWhiteY).apply {
            repeatCount = this@GiftDoubleAnimManager.repeatCount
            repeatMode = ValueAnimator.RESTART
        }

        val keyframe1Blue = Keyframe.ofFloat(0f, 1f)
        val keyframe2Blue = Keyframe.ofFloat(0.12f, 1f)
        val keyframe3Blue = Keyframe.ofFloat(0.52f, 1.3f)
        val keyframe4Blue = Keyframe.ofFloat(0.82f, 1f)
        val keyframe5Blue = Keyframe.ofFloat(1f, 1f)
        val holderBlueX = PropertyValuesHolder.ofKeyframe("scaleX", keyframe1Blue, keyframe2Blue, keyframe3Blue, keyframe4Blue, keyframe5Blue)
        val holderBlueY = PropertyValuesHolder.ofKeyframe("scaleY", keyframe1Blue, keyframe2Blue, keyframe3Blue, keyframe4Blue, keyframe5Blue)
        val animatorBlue = ObjectAnimator.ofPropertyValuesHolder(blueView, holderBlueX, holderBlueY).apply {
            repeatCount = this@GiftDoubleAnimManager.repeatCount
            repeatMode = ValueAnimator.RESTART
        }

        val keyframe1Red = Keyframe.ofFloat(0f, 1f)
        val keyframe2Red = Keyframe.ofFloat(0.24f, 1f)
        val keyframe3Red = Keyframe.ofFloat(0.64f, 1.3f)
        val keyframe4Red = Keyframe.ofFloat(1f, 1f)
        val holderRedX = PropertyValuesHolder.ofKeyframe("scaleX", keyframe1Red, keyframe2Red, keyframe3Red, keyframe4Red)
        val holderRedY = PropertyValuesHolder.ofKeyframe("scaleY", keyframe1Red, keyframe2Red, keyframe3Red, keyframe4Red)
        val animatorRed = ObjectAnimator.ofPropertyValuesHolder(redView, holderRedX, holderRedY).apply {
            repeatCount = this@GiftDoubleAnimManager.repeatCount
            repeatMode = ValueAnimator.RESTART
        }


        animatorSet.playTogether(animatorWhite, animatorBlue, animatorRed)
        animatorSet.duration = 600
        animatorSet.start()
    }

    fun isAnimRunning(): Boolean {
        return animatorSet.isRunning
    }

    fun cancelAnim() {
        animatorSet.cancel()
    }
}