package com.mei.intimate.view

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.animation.Interpolator
import android.widget.ImageView
import android.widget.RelativeLayout
import com.mei.base.weight.heart.BesselEvaluator
import com.mei.orc.ext.dip
import com.mei.wood.R
import kotlinx.android.synthetic.main.view_cover_living.view.*
import java.util.*

/**
 * Created by hang on 2019/4/8.
 * 直播tab条目动效
 */
class LivingCoverView @JvmOverloads constructor(context: Context, attributeSet: AttributeSet? = null, defStyleAttr: Int = 0)
    : RelativeLayout(context, attributeSet, defStyleAttr) {


    init {
        LayoutInflater.from(context).inflate(R.layout.view_cover_living, this)
    }

    /**做动画的imageView的缓存列表*/
    private val mImageViewList = LinkedList<ImageView>()
    private val resIds = arrayListOf(R.drawable.live_heart_red, R.drawable.live_heart_yellow,
            R.drawable.live_heart_pink, R.drawable.live_heart_purple)

    private var parentWidth = 0
    private var parentHeight = 0

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        parentWidth = w
        parentHeight = h
//        val bottomView = getBottomView()
//        addView(bottomView)
    }

    private val runnable = object : Runnable {
        override fun run() {
            /**
             * 再显示做动画的图片
             * 先从缓存里面取  取不到再新建imageView
             */
            val imagePoll = mImageViewList.poll()
            val itemView = imagePoll ?: getItemView()
            itemView.setImageResource(resIds[Random().nextInt(4)])
            live_gif_layout.addView(itemView)
            startAnimator(itemView)

            //随机事件
            val intervalTime = Random().nextInt(1000) + 800
            postDelayed(this, intervalTime.toLong())
        }
    }

    private fun startAnimator(target: ImageView) {
        /**
         * 第二步 贝塞尔曲线移动动画 、缩放动画和透明度渐变动画
         */
        val params = target.layoutParams
        val targetSize = params.width
        val point0 = FloatArray(2)
        point0[0] = target.x
        point0[1] = target.y

        val point1 = FloatArray(2)
        val point2 = FloatArray(2)
        when (Random().nextInt(100) % 3) {
            0 -> {
                point1[0] = -dip(10).toFloat()
                point1[1] = (parentHeight / 2).toFloat()
                point2[0] = (parentWidth - dip(20)).toFloat()
                point2[1] = (parentHeight / 3).toFloat()
            }
            1 -> {
                point1[0] = (parentWidth / 2 - dip(32) / 2).toFloat()
                point1[1] = (parentHeight / 2).toFloat()
                point2[0] = (parentWidth / 2 - dip(12)).toFloat()
                point2[1] = (parentHeight / 3).toFloat()
            }
            2 -> {
                point1[0] = (parentWidth - dip(25)).toFloat()
                point1[1] = (parentHeight / 2).toFloat()
                point2[0] = -dip(10).toFloat()
                point2[1] = (parentHeight / 3).toFloat()
            }
        }

        val point3 = FloatArray(2)
        point3[0] = (Math.random() * (parentWidth - targetSize)).toFloat()
        point3[1] = 0f

        val evaluator = BesselEvaluator(point1, point2)
        val besselAnimator = ValueAnimator.ofObject(evaluator, point0, point3).setDuration(8000)
        besselAnimator.interpolator = CustomInterpolator()
//        besselAnimator.interpolator = LinearInterpolator()
        besselAnimator.addUpdateListener { animation ->
            val position = animation.animatedValue as FloatArray
            target.translationX = position[0]
            target.translationY = position[1]
            val fraction = animation.animatedFraction

            // 0.9 ~ 0
            target.scaleX = 1.0f - fraction
            target.scaleY = 1.0f - fraction

            if (fraction > 0.2f) {
                target.alpha = 1.25f - fraction * 1.25f
            }
        }
        besselAnimator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {
                super.onAnimationEnd(animation)
                live_gif_layout.removeView(target)
                resetView(target)
                mImageViewList.offer(target)
            }
        })
        besselAnimator.start()
    }

    private fun getItemView(): ImageView {
        return ImageView(context).apply {
            layoutParams = LayoutParams(dip(22), dip(22))
            x = ((parentWidth - dip(22)) / 2).toFloat()
            y = parentHeight.toFloat() - dip(30)
            scaleX = 0.2f
            scaleY = 0.2f
        }
    }

//    private fun getBottomView(): ImageView {
//        return ImageView(context).apply {
//            layoutParams = LayoutParams(dip(22), dip(22))
//            setImageResource(R.drawable.live_gif_bottom_icon)
//            x = ((parentWidth - dip(22)) / 2).toFloat()
//            y = (parentHeight - dip(22)).toFloat()
//        }
//    }

    /**重置view的初始属性*/
    private fun resetView(target: ImageView) {
        target.apply {
            x = ((parentWidth - dip(22)) / 2).toFloat()
            y = parentHeight.toFloat() - dip(30)
            alpha = 1f
            scaleX = 0.2f
            scaleY = 0.2f
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        postDelayed(runnable, 1000)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        removeCallbacks(runnable)
    }
}

class CustomInterpolator : Interpolator {
    override fun getInterpolation(input: Float): Float {
        return when {
            input < 0.3 -> (log2(input.toDouble() + 1)).toFloat()
            input < 0.8 -> (((0.9 - log2(1.3)) / 0.5) * (input - 0.3) + log2(1.3)).toFloat()
            else -> (0.5 * input + 0.5).toFloat()
        }
    }

    private fun log2(input: Double): Double {
        return Math.log(input) / Math.log(2.0)
    }
}