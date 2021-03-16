package com.mei.live.ui.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.joker.im.bindEventLifecycle
import com.joker.im.custom.CustomType
import com.joker.im.custom.chick.ChickCustomData
import com.joker.im.custom.chick.LiveGift
import com.joker.im.listener.IMAllEventManager
import com.joker.im.mapToMeiMessage
import com.joker.im.message.CustomMessage
import com.mei.live.ui.VideoLiveRoomActivity
import com.mei.live.ui.giftInfoList
import com.mei.live.views.GiftBannerView
import com.mei.orc.john.model.JohnUser
import com.mei.wood.R
import com.mei.wood.ui.CustomSupportFragment
import com.tencent.imsdk.TIMMessage
import kotlinx.android.synthetic.main.fragment_live_banner_gift.*
import java.util.*

/**
 * Created by hang on 2019/1/3.
 * banner礼物
 */
class LiveBannerGiftFragment : CustomSupportFragment() {

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
                        if (gift.userId != JohnUser.getSharedUser().userID && gift.giftId > 0) {
                            addGiftQueue(gift)
                        }
                    }
                }
//                CustomType.invite_send_gift -> {
//                    data.inviteSendGift?.let {
//                        activity?.showGiftGuideDialog(it)
//                    }
//                }
                else -> {

                }
            }
        }
    }

    private var bottomView: GiftBannerView? = null

    private val bannerViewCache = LinkedList<GiftBannerView>()

    private var giftQueue = LinkedList<LiveGift>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_live_banner_gift, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkNewEvent.bindEventLifecycle(this)
    }

    fun addGiftQueue(item: LiveGift) {
        context?.let { context ->
            giftQueue.offer(item)

            if (giftQueue.isNotEmpty() && (bottomView?.isSameGift(giftQueue[0]) == true || root_layout.childCount < 5)) {
                giftQueue.poll()?.let { startBannerAnim(context, it) }
            }
        }
    }

    private fun startBannerAnim(context: Context, poll: LiveGift) {
        if (bottomView?.isSameGift(poll) == true) {
            bottomView?.plusGiftNumber(poll)
        } else {
            /**
             * 全屏礼物不显示连击效果
             */
            if (activity is VideoLiveRoomActivity) {
                with(activity as VideoLiveRoomActivity) {
                    val find = if (poll.giftId == roomInfo.gift?.giftId) roomInfo.gift else giftInfoList.find { it.giftId == poll.giftId }
                    find?.let {
                        //                        if (it.costCoin >= 99) {
                        liveFullScreenGiftFragment.addGiftQueue(poll)
//                        }
                    }
                }
            }

            if (root_layout.childCount < 5) {
                /** 如果第一条没有礼物展示 或者 和加入的礼物不一样**/
                bottomView = bannerViewCache.poll() ?: createBannerView(context) { v ->
                    if (isAdded) {
                        root_layout.removeView(v)
                        bannerViewCache.offer(v)

                        giftQueue.poll()?.let {
                            startBannerAnim(context, it)
                        }
                    }
                }
                (bottomView?.parent as? ViewGroup)?.removeView(bottomView)
                root_layout.addView(bottomView)
                bottomView?.refreshData(poll)
            }
        }
    }

    private fun GiftBannerView.isSameGift(info: LiveGift): Boolean {
        return info.giftId == giftInfo?.giftId && info.atId == giftInfo?.atId && info.userId == giftInfo?.userId
    }

    private fun createBannerView(context: Context, callback: (view: GiftBannerView) -> Unit): GiftBannerView {
        return GiftBannerView(context).apply {
            layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            this.callback = callback
        }
    }

}