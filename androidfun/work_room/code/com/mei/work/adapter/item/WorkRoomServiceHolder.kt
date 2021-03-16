package com.mei.work.adapter.item

import android.graphics.Color
import android.view.View
import android.widget.LinearLayout
import androidx.core.view.isVisible
import com.joker.im.custom.CustomType
import com.joker.im.custom.chick.ChickCustomData
import com.joker.im.custom.chick.ServiceInfo
import com.joker.im.custom.chick.SplitText
import com.mei.chat.toImChat
import com.mei.live.LiveEnterType
import com.mei.live.ext.createSplitSpannable
import com.mei.live.manager.genderAvatar
import com.mei.live.ui.VideoLiveRoomActivity
import com.mei.live.ui.VideoLiveRoomActivityLauncher
import com.mei.orc.imageload.glide.glideDisplay
import com.mei.orc.john.model.JohnUser
import com.mei.orc.ui.toast.UIToast
import com.mei.orc.util.click.setOnClickNotDoubleListener
import com.mei.orc.util.json.toJson
import com.mei.widget.holder.activity
import com.mei.widget.round.RoundImageView
import com.mei.wood.R
import com.mei.wood.cache.requestImUserInfo
import com.mei.wood.ext.AmanLink
import com.mei.wood.extensions.executeKt
import com.mei.wood.util.AppManager
import com.mei.wood.util.MeiUtil
import com.mei.wood.web.MeiWebActivityLauncher
import com.mei.wood.web.MeiWebData
import com.mei.work.ui.getCurWorkRoomLivingRoomId
import com.net.MeiUser
import com.net.model.chick.tab.tabbarConfig
import com.net.network.chick.recommend.BatRoomStatusRequest

/**
 * WorkRoomServiceHolder
 *
 * 工作室专属服务信息
 *
 * @author kun
 * @version 1.0.0
 * @since 2020-07-25
 */
class WorkRoomServiceHolder(view: View) : WorkRoomBaseHolder(view) {

    private var info: ServiceInfo = ServiceInfo()

    init {
        getView<View>(R.id.room_main_service_left_container).setOnClickNotDoubleListener(tag = "specific") {
            MeiWebActivityLauncher.startActivity(activity,
                    MeiWebData(MeiUtil.appendParamsUrl(AmanLink.NetUrl.exclusive_service_details,
                            "service_id" to "${info.specialServiceId}",
                            "from" to "self_page",
                            "room_id" to getCurWorkRoomLivingRoomId(info.roomId, info.groupId)),
                            0).apply {
                        need_reload_web = 1
                    })
        }

        getView<View>(R.id.room_main_service_ask_rll).setOnClickNotDoubleListener(tag = "ask_rll") {
            askClick()
        }
    }

    override fun refresh(item: Any) {
        if (item is ServiceInfo) {
            info = item
            setText(R.id.room_main_service_name_tv, info.serviceName)

            val price = info.priceText.orEmpty().createSplitSpannable(Color.parseColor("#333333"))
            setText(R.id.room_main_service_price_tv, price)

            getView<RoundImageView>(R.id.room_main_service_teacher_avatar).glideDisplay(info.publisherInfo?.avatar.orEmpty(), info.publisherInfo?.gender.genderAvatar())
            setText(R.id.room_main_service_teacher_text, info.publisherInfo?.nickname)
            getView<LinearLayout>(R.id.room_main_service_ask_rll).isVisible = info.publisherId != JohnUser.getSharedUser().userID && MeiUser.getSharedUser().info?.isPublisher == false
        }
    }

    private fun askClick() {
        if (MeiUser.getSharedUser().info?.groupRole ?: 0 > 0 || MeiUser.getSharedUser().info?.isPublisher == true) {
            UIToast.toast("暂不支持知心达人之间的私聊功能")
            return
        }
        showLoading(true)
        val liveRoomActivity = (AppManager.getInstance().getTargetActivity(VideoLiveRoomActivity::class.java) as? VideoLiveRoomActivity)
        val roomId = liveRoomActivity?.roomId.orEmpty()
        val groupId = liveRoomActivity?.roomInfo?.groupInfo?.groupId
        if (roomId == info.roomId || groupId == info.groupId) { // 是大直播间 或 是同工作室成员在直播
            gotoPushIM(roomId)
            showLoading(false)
        } else {
            apiSpiceMgr.executeKt(BatRoomStatusRequest(arrayListOf(info.publisherId)), success = {
                gotoPushIM(it.data["${info.groupId}"]?.roomId.orEmpty())
            }, failure = {
                gotoPushIM("")
            }, finish = {
                showLoading(false)
            })
        }

        // 普通用户点击询问，判断是否在直播中
        //主播端自己看自己是没有推荐的，因为个人主页入口很多，如果主播没在直播中就根本不知道推荐给谁，鑫姐说的

    }

    private fun gotoPushIM(roomId: String) {
        if (roomId.isNotEmpty()) {
            //直播中 （跳转到直播间）
            VideoLiveRoomActivityLauncher.startActivity(activity, roomId, LiveEnterType.user_home_page, ChickCustomData.Result(CustomType.special_service_card, ChickCustomData().apply {
                this.serviceInfo = info
                this.msgState = 2
                this.content = arrayListOf(SplitText(text = "${MeiUser.getSharedUser().info?.nickname ?: ""}想要询问【专属服务】", color = "#A3E2FB", action = AmanLink.URL.exclusive_service_ask_dialog))
            }).toJson())
        } else {

            apiSpiceMgr.requestImUserInfo(arrayOf(info.publisherId), needRefresh = true, back = {
                val userInfo = it.firstOrNull()
                when {
                    userInfo?.isSelfToBlackList == true -> {
                        UIToast.toast(tabbarConfig.groupBlacklistUser)
                    }
                    userInfo?.isTheOtherToBlackList == true -> {
                        UIToast.toast(tabbarConfig.hasBlacklistTips)
                    }
                    else -> {
                        //未直播  (跳转到im页面发一条消息）
                        activity?.toImChat("${info.publisherId}", false, ChickCustomData.Result(CustomType.special_service_card, ChickCustomData().apply {
                            serviceInfo = info
                            content = arrayListOf(SplitText(text = "${MeiUser.getSharedUser().info?.nickname ?: ""}想要询问【专属服务】", color = "#A3E2FB", action = AmanLink.URL.exclusive_service_ask_dialog))
                        }))
                    }
                }
            })

        }
    }
}