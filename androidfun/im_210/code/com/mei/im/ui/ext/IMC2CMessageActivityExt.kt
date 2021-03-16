@file:Suppress("DEPRECATION")

package com.mei.im.ui.ext

import android.app.Activity
import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.joker.im.custom.CustomType
import com.joker.im.custom.chick.ChickCustomData
import com.joker.im.custom.chick.ServiceInfo
import com.joker.im.message.CustomMessage
import com.mei.GrowingUtil
import com.mei.base.common.CANCEL_SHIELD_USER
import com.mei.base.common.FOLLOW_USER_STATE
import com.mei.base.common.SHIELD_USER
import com.mei.chat.ext.CallStatus
import com.mei.im.ui.IMC2CMessageActivity
import com.mei.live.views.followDelete
import com.mei.live.views.followFriend
import com.mei.orc.Cxt
import com.mei.orc.event.bindAction
import com.mei.orc.event.postAction
import com.mei.orc.ext.dp
import com.mei.orc.ui.toast.UIToast
import com.mei.orc.util.click.setOnClickNotDoubleListener
import com.mei.orc.util.string.getInt
import com.mei.widget.gradient.applyViewDelegate
import com.mei.wood.R
import com.mei.wood.extensions.executeKt
import com.net.MeiUser
import com.net.model.chick.tab.tabbarConfig
import com.net.network.chat.C2cStatisticsRequest
import com.net.network.chick.report.PublisherInfoRequest
import com.net.network.chick.report.ShieldingAddRequest
import com.net.network.chick.report.ShieldingDeleteRequest
import com.net.network.chick.report.UserInfoRequest
import com.net.network.exclusive.HasBuyExclusiveServiceRequest
import kotlinx.android.synthetic.main.chat_c2c_activity.*
import kotlinx.android.synthetic.main.chat_c2c_header_private_layout.*
import java.lang.ref.WeakReference


fun IMC2CMessageActivity.publisherInfo(targetId: String, isPub: Boolean) {
    val tabBar = tabbarConfig
    if (isPub) {
        apiSpiceMgr.executeKt(PublisherInfoRequest(targetId), success = {
            val useInfo = it.data.info
            //判断是否是工作室成员，如果是就加载工作室信息展示
            if (useInfo.userInfo.groupInfo != null) {
                im_c2c_chart_user_pub_info.initGroupInfo(useInfo)
            } else {
                im_c2c_chart_user_pub_info.initPublisher(useInfo)
            }
            im_service_banner_islbv.notifyBannerItem(it.data?.mySpecialServices.orEmpty(), chatId.getInt())
            it.data?.mySpecialServices?.apply {
                im_service_banner_islbv.isVisible = size > 0 && tabBar.review != true
                im_user_option_layout.isVisible = (size == 0 && useInfo.userInfo.groupInfo == null) || tabBar.review == true
            }
            imMsgListFragment.bottomRecycler()
            rightText.apply {
                isVisible = !(useInfo.isFollow)
                if (!useInfo.isFollow) {
                    delegate.applyViewDelegate {
                        startColor = ContextCompat.getColor(context, R.color.color_FF7F33)
                        endColor = ContextCompat.getColor(context, R.color.color_FF3F36)
                        radius = 2.dp
                    }
                    setTextColor(Cxt.getColor(android.R.color.white))
                    text = "关注"
                }
            }
            rightText.setOnClickNotDoubleListener {
                if (rightText.text == "已关注") {
                    followDelete(useInfo.userId) { isOk ->
                        if (isOk) {
                            rightText.delegate.applyViewDelegate {
                                startColor = ContextCompat.getColor(context, R.color.color_FF7F33)
                                endColor = ContextCompat.getColor(context, R.color.color_FF3F36)
                                radius = 2.dp
                            }
                            rightText.setTextColor(Cxt.getColor(android.R.color.white))
                            rightText.text = "关注"
                        }
                    }
                } else {
                    followFriend(useInfo.userId, 4, useInfo.userId.toString()) { isOk ->
                        if (isOk) {
                            rightText.delegate.applyViewDelegate {
                                strokeWidth = 1.dp
                                strokeStartColor = ContextCompat.getColor(context, R.color.color_DEDFDE)
                                endColor = ContextCompat.getColor(context, R.color.colorWhite)
                                radius = 2.dp
                            }
                            rightText.setTextColor(Cxt.getColor(R.color.color_999999))
                            rightText.text = "已关注"
                        }
                    }
                }

            }
        })
    } else {
        rightText.visibility = View.INVISIBLE
        apiSpiceMgr.executeKt(UserInfoRequest(targetId), success = {
            it?.let {
                im_c2c_chart_user_pub_info.initNoPublisher(it.data)
                it.data?.mySpecialServices?.apply {
                    im_service_banner_islbv.notifyBannerItem(this, chatId.getInt())
                }

                it.data?.mySpecialServices?.apply {
                    im_service_banner_islbv.isVisible = size > 0 && tabBar.review != true
                }
                imMsgListFragment.bottomRecycler()
            }
        })

    }
}

/**
 * 开始震动
 */
fun IMC2CMessageActivity.startVibrator() {
    val wr = WeakReference<Activity>(activity)
    val vibrator = wr.get()?.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator?
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        vibrator?.vibrate(VibrationEffect.createOneShot(150, VibrationEffect.DEFAULT_AMPLITUDE))
    } else {
        vibrator?.vibrate(150)
    }
}

fun IMC2CMessageActivity.handleNewMsgEvent(msg: CustomMessage) {
    (msg.customInfo?.data as? ChickCustomData)?.let { data ->
        when (data.type) {
            CustomType.call_status_changed -> {
                when (data.exclusiveResult?.status) {
                    //私密连线已结束通知状态变化，可更新banner
                    CallStatus.FINISHED.name -> {
                        exclusiveFinishMessage()
                        requestData()
                    }
                }
            }
            CustomType.special_service_card -> {
                if (data.serviceInfo?.cardType == ServiceInfo.CardType.TYPE_RECHARGE) {
                    //刷新关注状态
                    refreshFollowState(false)
                    exclusiveFinishMessage()
                    requestData()
                    //购买成功刷新聊天配置
                    refreshChatConfig()
                }

            }
            CustomType.exclusive_system_notify -> {
                requestData()
            }
            else -> {
            }
        }

    }

}

/**
 * 处理拉黑操作通知
 */
fun IMC2CMessageActivity.handleShielding(targetId: List<Int>?) {
    if (targetId?.any { it == info?.userId } == true) {
        requestData()
    }
}

fun IMC2CMessageActivity.exclusiveFinishMessage() {
    val publishId: Int
    val userId: Int

    if (info?.isPublisher == true) {
        publishId = chatId.toInt()
        userId = MeiUser.getSharedUser().info?.userId ?: 0
    } else {
        publishId = MeiUser.getSharedUser().info?.userId ?: 0
        userId = chatId.toInt()
    }
    apiSpiceMgr.executeKt(HasBuyExclusiveServiceRequest(userId, publishId), success = {
        it?.data?.service?.apply {
            im_service_banner_islbv.isVisible = size > 0 && tabbarConfig.review != true
            if (size > 0) {
                im_service_banner_islbv.notifyBannerItem(this, chatId.getInt())
            }
        }
    })


}

fun IMC2CMessageActivity.bindActionState() {
    bindAction<Pair<Int, Boolean>>(FOLLOW_USER_STATE) {
        if (it != null && "${it.first}" == chatId) {
            refreshFollowState(it.second)
        }
    }
    bindAction<List<Int>>(SHIELD_USER) {
        if (it?.any { id -> id == chatId.getInt(0) } == true) {
            requestData()
        }
    }
    bindAction<List<Int>>(CANCEL_SHIELD_USER) {
        if (it?.any { id -> id == chatId.getInt(0) } == true) {
            requestData()
        }
    }


}

/**
 * 刷新关注状态
 */
private fun IMC2CMessageActivity.refreshFollowState(isFollow: Boolean) {
    if (isFollow) {
        rightText.isVisible = false
    } else {
        rightText.isVisible = true
        rightText.delegate.applyViewDelegate {
            startColor = ContextCompat.getColor(context, R.color.color_FF7F33)
            endColor = ContextCompat.getColor(context, R.color.color_FF3F36)
            radius = 2.dp
        }
        rightText.setTextColor(Cxt.getColor(android.R.color.white))
        rightText.text = "关注"
    }
}

/**
 * 取消黑名单
 */
fun IMC2CMessageActivity.shieldedDelete(userID: String) {
    apiSpiceMgr.executeKt(ShieldingDeleteRequest(userID, ""), success = { response ->
        if (response.isSuccess) {
            postAction(CANCEL_SHIELD_USER, arrayListOf(userID.getInt(0)))
        }
        UIToast.toast(response.msg)
    })
}

/**
 * 添加黑名单
 */
fun IMC2CMessageActivity.shieldAddUser(userID: String) {
    apiSpiceMgr.executeKt(ShieldingAddRequest(userID, ""), success = { response ->
        if (response.isSuccess) {
            postAction(SHIELD_USER, if (response.data.userIds?.isNotEmpty() == true) response.data.userIds else arrayListOf(userID.getInt(0)))
            //退出私聊
            finish()
        }
        UIToast.toast(response.msg)
    })
}

/**
 * 统计进入私聊页面没有发送过内容的用户
 */
fun IMC2CMessageActivity.statisticsGoC2cUnSendContent() {
    if (MeiUser.getSharedUser().info?.isPublisher != true) {
        apiSpiceMgr.executeKt(C2cStatisticsRequest(chatId))
    }

}

/**私聊页浏览统计*/
fun statImC2cBrowseEvent() = try {
    GrowingUtil.track("common_page_view", "page_name", "私聊页",
            "page_type", "",
            "click_content", "",
            "time_stamp", "${System.currentTimeMillis() / 1000}")
} catch (e: Exception) {
    e.printStackTrace()
}