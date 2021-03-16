package com.mei.live.views

import android.annotation.SuppressLint
import android.content.Context
import android.os.SystemClock
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.core.animation.addListener
import com.joker.im.custom.chick.LiveGift
import com.mei.base.util.glide.GlideDisPlayDefaultID
import com.mei.live.manager.GiftDoubleAnimManager
import com.mei.live.manager.genderAvatar
import com.mei.orc.Cxt
import com.mei.orc.ext.screenWidth
import com.mei.orc.ext.selectBy
import com.mei.orc.ext.setListenerEnd
import com.mei.orc.imageload.glide.glideDisplay
import com.mei.wood.R
import kotlinx.android.synthetic.main.item_banner_gift.view.*

/**
 * Created by hang on 2019/1/16.
 */
class GiftBannerView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
        FrameLayout(context, attrs, defStyleAttr) {

    private val ENTER_TIME: Long = 600
    private val SHOW_GIFT_TIME: Long = ANIMATOR_DURATION
    var exitRunnable: Runnable? = null

    var callback: (view: GiftBannerView) -> Unit = { _ -> }
    private var count = 0

    init {
        LayoutInflater.from(context).inflate(R.layout.item_banner_gift, this)
    }

    var giftInfo: LiveGift? = null

    /** 礼物开始时间 **/
    var showTime: Long = 0L

    /**
     * 完整显示动画
     */
    fun refreshData(item: LiveGift) {
        count = item.count
        giftInfo = null
        translationX = (measuredWidth == 0).selectBy(-screenWidth.toFloat(), -measuredWidth.toFloat())
        alpha = 0.3f
        if (count == 1) {
            refreshUI(item)
            animate().translationX(0f)
                    .alpha(1f)
                    .setDuration(ENTER_TIME)
                    .setListenerEnd {
                        GiftDoubleAnimManager(count_white_view, count_blue_view, count_red_view, 2).startAnim()
                    }.start()
        } else {
            refreshBaseUi(item)
            animate().translationX(0f)
                    .alpha(1f)
                    .setDuration(ENTER_TIME)
                    .setListenerEnd {
                        post(autoRunnable)
                    }.start()
        }
    }

    /**
     * 覆盖上一条相同礼物，不做进入动画
     */
    fun plusGiftNumber(item: LiveGift) {
        count += item.count
        translationX = 0f
        refreshUI(item)
        GiftDoubleAnimManager(count_white_view, count_blue_view, count_red_view, 2).startAnim()
    }

    @SuppressLint("SetTextI18n")
    private fun refreshUI(item: LiveGift) {
        refreshBaseUi(item)

        count_red_view.text = "x $count"
        count_blue_view.text = "x $count"
        count_white_view.text = "x $count"
        listenAnimLife()
    }

    private fun refreshBaseUi(item: LiveGift) {
        giftInfo = item
        gift_name.text = Cxt.getStr(R.string.send_gift, item.atName + " " + item.giftName)

        gift_image.glideDisplay(item.giftImg, GlideDisPlayDefaultID.default_gift)
        head_cover.glideDisplay(item.avatar, item.gender.genderAvatar(item.isPublisher))
        user_name.text = item.nickName
    }


    /**
     * 监听动画展示周期
     */
    private fun listenAnimLife() {
        showTime = SystemClock.elapsedRealtime()
        removeCallbacks(exitRunnable)
        exitRunnable = Runnable {
            /** 礼物显示完了 **/
            giftInfo = null
            animate().alpha(0.3f).setDuration(ENTER_TIME).setListenerEnd {
                callback(this)
            }.start()
        }
        postDelayed(exitRunnable, SHOW_GIFT_TIME)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        clearAnimation()
        removeCallbacks(exitRunnable)
        removeCallbacks(autoRunnable)
    }

    var num = 0
    private val autoRunnable = object : Runnable {
        override fun run() {
            num++
            val repeatCount = if (num == count) 2 else 0
            val animManager = GiftDoubleAnimManager(count_white_view, count_blue_view, count_red_view, repeatCount)
            count_red_view.text = "x $num"
            count_blue_view.text = "x $num"
            count_white_view.text = "x $num"
            animManager.startAnim()
            animManager.animatorSet.addListener(onEnd = {
                if (num < count) {
                    post(this)
                } else {
                    /** 礼物显示完了 **/
                    giftInfo = null
                    num = 0
                    animate().alpha(0.3f).setDuration(ENTER_TIME).setListenerEnd {
                        callback(this@GiftBannerView)
                    }.start()
                }
            })
        }

    }
}