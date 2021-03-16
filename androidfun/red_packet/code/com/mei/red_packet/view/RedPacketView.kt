package com.mei.red_packet.view

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.core.content.ContextCompat
import com.mei.base.common.RECYCLE_SCROLL_STATE
import com.mei.orc.Cxt
import com.mei.orc.event.bindAction
import com.mei.orc.ext.dip
import com.mei.orc.util.click.setOnClickNotDoubleListener
import com.mei.red_packet.toReceiveRedPacket
import com.mei.wood.R
import com.mei.wood.ui.MeiCustomActivity
import com.mei.wood.ui.clickRedPacketStat

/**
 *
 * @author Created by lenna on 2020/7/27
 * 产品：本功能已被移除首页
 */
@Deprecated("首页绑定手机号红包暂时去掉")
class RedPacketView(context: Context, attributeSet: AttributeSet? = null, def: Int = 0) : FrameLayout(context, attributeSet, def) {
    private var mIsRunning: Boolean = false
    private var mIv: ImageView? = null
    private var mCloseSet: AnimatorSet? = null

    init {
        mIv = ImageView(context)
        mIv?.id = R.id.red_packet_iv
        addView(mIv)
        loadView(context)
        mCloseSet = AnimatorSet()
        val translation = ObjectAnimator.ofFloat(this, "translationX", 0f, dip(37) * 1f).setDuration(500)
        val alpha = ObjectAnimator.ofFloat(this, "alpha", 1f, 0.3f).setDuration(500)
        mCloseSet?.playTogether(translation, alpha)
    }

    private fun loadView(context: Context) {
        layoutParams = LayoutParams(dip(73), dip(73))
        mIv?.setImageDrawable(ContextCompat.getDrawable(Cxt.get(), R.drawable.icon_receive_red_packet))
        mIv?.setOnClickNotDoubleListener(tag = "clickRedPacket") {
            (context as? MeiCustomActivity)?.toReceiveRedPacket()
            (context as? MeiCustomActivity)?.clickRedPacketStat()
        }
        (context as? MeiCustomActivity)?.bindAction<Boolean>(RECYCLE_SCROLL_STATE) {
            if (it == false) {
                //滑动中
                closeView()
            }
        }
    }

    /**
     * 收起
     */
    private fun closeView() {

        if (!mIsRunning) {
            mIsRunning = !mIsRunning
            mCloseSet?.start()
            postDelayed({
                val expandTranslation = ObjectAnimator.ofFloat(this, "translationX", dip(37) * 1f, 1f).setDuration(500)
                val expandAlpha = ObjectAnimator.ofFloat(this, "alpha", 0.3f, 1f).setDuration(500)
                val expandSet = AnimatorSet()
                expandSet.playTogether(expandTranslation, expandAlpha)
                expandSet.start()
                expandSet.addListener(object : Animator.AnimatorListener {
                    override fun onAnimationRepeat(animation: Animator?) {

                    }

                    override fun onAnimationEnd(animation: Animator?) {
                        mIsRunning = !mIsRunning
                    }

                    override fun onAnimationCancel(animation: Animator?) {
                    }

                    override fun onAnimationStart(animation: Animator?) {
                    }

                })

            }, 1500)
        }

    }

}