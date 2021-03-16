package com.mei.live.ui.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import com.joker.im.bindEventLifecycle
import com.joker.im.custom.CustomType
import com.joker.im.custom.chick.ChickCustomData
import com.joker.im.custom.chick.LiveGift
import com.joker.im.listener.IMAllEventManager
import com.joker.im.mapToMeiMessage
import com.joker.im.message.CustomMessage
import com.mei.live.ext.createSplitSpannable
import com.mei.live.ui.VideoLiveRoomActivity
import com.mei.live.ui.giftInfoList
import com.mei.orc.Cxt
import com.mei.orc.ext.screenWidth
import com.mei.orc.john.model.JohnUser
import com.mei.orc.util.save.getObjectMMKV
import com.mei.wood.R
import com.mei.wood.ui.CustomSupportFragment
import com.mei.wood.util.logDebug
import com.net.model.chick.tab.tabbarConfig
import com.net.model.room.GenericEffectConfig
import com.opensource.svgaplayer.SVGACallback
import com.opensource.svgaplayer.SVGADrawable
import com.opensource.svgaplayer.SVGAParser
import com.opensource.svgaplayer.SVGAVideoEntity
import com.tencent.imsdk.TIMMessage
import kotlinx.android.synthetic.main.fragment_full_screen_gift.*
import java.net.URL
import java.util.*


/**
 * Created by hang on 2018/12/28.
 * 全屏礼物动效界面
 */
const val RECHARGE_SUCCESS_ID = -1
const val RECHARGE_SERVICE_SUCCESS_ID = -2//专属服务充值成功

class FullScreenGiftFragment : CustomSupportFragment() {

    private val checkNewEvent: IMAllEventManager by lazy {
        object : IMAllEventManager() {
            /** 监听最新消息 **/
            override fun onNewMessages(msgs: MutableList<TIMMessage>?): Boolean {
                val customList = msgs.orEmpty()
                        .mapNotNull { it.mapToMeiMessage() as? CustomMessage }
                        .filter { !it.isDeleted() }//去掉已删除的消息

                (activity as? VideoLiveRoomActivity)?.let { activity ->
                    customList.filter { it.peer == activity.roomId }.forEach { handleIMEvent(it) }
                }
                return super.onNewMessages(msgs)
            }
        }
    }

    /**
     * 处理IM消息
     */
    fun handleIMEvent(message: CustomMessage) {
        val type = message.customMsgType
        (message.customInfo?.data as? ChickCustomData)?.let { data ->
            when (type) {
                CustomType.send_gift -> {
                    data.gift?.let { gift ->
                        if (((gift.giftId == RECHARGE_SUCCESS_ID || gift.giftId == RECHARGE_SERVICE_SUCCESS_ID)) && data.whitelist.contains(JohnUser.getSharedUser().userID)) {
                            logDebug("FullScreenGiftFragment", "收到充值成功消息")
                            addGiftQueue(gift)
                        }
                    }
                }
                else -> {

                }
            }
        }
    }

    private val giftAnimList = LinkedList<LiveGift>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_full_screen_gift, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkNewEvent.bindEventLifecycle(this)
        anim_text.paint.isFakeBoldText = true
    }


    fun addGiftQueue(giftInfo: LiveGift) {
        giftAnimList.add(giftInfo)
        if (!isVisible) {
            activity?.supportFragmentManager?.beginTransaction()?.show(this)?.commitNowAllowingStateLoss()
            giftAnimList.poll()?.let { showGiftAnim(it) }
        }
    }

    private fun showGiftAnim(giftInfo: LiveGift) {
        anim_frame.isVisible = false
        val gift = getGiftResForType(giftInfo)
        if (gift != null) {
            SVGAParser.shareParser().decodeFromURL(gift, object : SVGAParser.ParseCompletion {
                override fun onComplete(videoItem: SVGAVideoEntity) {
                    if (isAdded) {
                        logDebug("FullScreenGiftFragment", "onComplete")
                        val svgaDrawable = SVGADrawable(videoItem)
                        var textAnim = false
                        svg_image.callback = object : SVGACallback {
                            override fun onFinished() {
                                if (isAdded) {
                                    anim_frame.isVisible = false
                                    showNext()
                                }
                                logDebug("FullScreenGiftFragment", "onFinished")
                            }

                            override fun onPause() {
                            }

                            override fun onRepeat() {
                            }

                            @SuppressLint("ResourceType", "SetTextI18n")
                            override fun onStep(frame: Int, percentage: Double) {
                                if (isAdded && giftInfo.giftId == RECHARGE_SERVICE_SUCCESS_ID && percentage >= 0.1 && !textAnim) {
                                    anim_frame.apply {
                                        scaleX = 0.1f
                                        scaleY = 0.1f
                                        isVisible = true
                                    }
                                    anim_text.apply {
                                        text = giftInfo.title+ tabbarConfig.diamondEmoji.orEmpty().createSplitSpannable(Cxt.getColor(R.color.color_333333))
                                    }
                                    textAnim = true
                                    anim_frame.animate().scaleX(1f).scaleY(1f).translationY(-100f).setDuration(500).withLayer().start()

                                }
                                if (isAdded && giftInfo.giftId == RECHARGE_SUCCESS_ID && percentage >= 0.1 && !textAnim) {
                                    anim_frame.apply {
                                        scaleX = 0.1f
                                        scaleY = 0.1f
                                        isVisible = true
                                    }
                                    anim_text.apply {
                                        text = giftInfo.title
                                    }
                                    textAnim = true
                                    anim_frame.animate().scaleX(1f).scaleY(1f).translationY(-100f).setDuration(500).withLayer().start()
                                }
                            }
                        }
                        svg_image.updateLayoutParams {
                            height = (screenWidth * videoItem.videoSize.height / videoItem.videoSize.width).toInt()
                            logDebug("FullScreenGiftFragment", "height=$height")
                        }
                        svg_image.setImageDrawable(svgaDrawable)
                        svg_image.startAnimation()
                    }
                }

                override fun onError() {
                    if (isAdded) {
                        logDebug("FullScreenGiftFragment", "onError")
                        showNext()
                    }
                }
            })
        } else {
            if (isAdded) showNext()
        }
    }

    /**
     * 显示下一条动画
     */
    private fun showNext() {
        val poll = giftAnimList.poll()
        if (poll != null) {
            showGiftAnim(poll)
        } else {
            activity?.supportFragmentManager?.beginTransaction()?.hide(this)?.commitNowAllowingStateLoss()
        }
    }

    /***
     * 通过gift_type获取本地存储的礼物信息
     */
    private fun getGiftResForType(info: LiveGift): URL? {
        return try {
            if (info.giftId == RECHARGE_SUCCESS_ID || info.giftId == RECHARGE_SERVICE_SUCCESS_ID) {
                val genericEffectConfig = GenericEffectConfig::class.java.name.getObjectMMKV(GenericEffectConfig::class.java)
                logDebug("FullScreenGiftFragment", genericEffectConfig?.rechargeSuccessAnimation.orEmpty())
                URL(genericEffectConfig?.rechargeSuccessAnimation.orEmpty())
            } else {
                URL(if (info.giftEffect.isNotEmpty()) info.giftEffect else
                    giftInfoList.first { it.giftType == info.giftType }.giftEffect.orEmpty())
            }
        } catch (e: Exception) {
            null
        }
    }

}