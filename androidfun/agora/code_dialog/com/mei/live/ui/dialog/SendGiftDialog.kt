package com.mei.live.ui.dialog

import android.graphics.Color
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentActivity
import com.joker.im.bindEventLifecycle
import com.joker.im.custom.CustomType
import com.joker.im.custom.chick.ChickCustomData
import com.joker.im.custom.chick.LiveGift
import com.joker.im.listener.IMAllEventManager
import com.joker.im.mapToMeiMessage
import com.joker.im.message.CustomMessage
import com.joker.im.registered
import com.joker.im.unregistered
import com.mei.base.ui.nav.Nav
import com.mei.dialog.PayFromType
import com.mei.dialog.myRose
import com.mei.dialog.showPayDialog
import com.mei.init.spiceHolder
import com.mei.live.manager.genderAvatar
import com.mei.live.ui.ext.LevelSize
import com.mei.live.ui.ext.getBackGroundLevelResource
import com.mei.live.ui.giftInfoList
import com.mei.live.ui.requestGiftInfo
import com.mei.orc.dialog.BottomDialogFragment
import com.mei.orc.ext.dip
import com.mei.orc.imageload.glide.glideDisplay
import com.mei.orc.john.model.JohnUser
import com.mei.wood.R
import com.mei.wood.cache.getCacheUserInfo
import com.mei.wood.cache.requestUserInfo
import com.mei.wood.extensions.executeKt
import com.mei.wood.extensions.executeToastKt
import com.mei.wood.ui.MeiCustomActivity
import com.net.MeiUser
import com.net.model.chick.tab.tabbarConfig
import com.net.model.gift.UserLevelInfo
import com.net.model.rose.RoseFromSceneEnum
import com.net.network.gift.GiftListInfoRequest
import com.net.network.gift.UserLevelInfoRequest
import com.tencent.imsdk.TIMMessage
import kotlinx.android.synthetic.main.dialog_send_gift.*

/**
 *  Created by lh on 2019-12-7
 *  Des: 送礼弹窗
 */

fun FragmentActivity.showSendGiftDialog(
        userId: Int,
        userName: String,
        roomId: String,
        roseFromScene: RoseFromSceneEnum = RoseFromSceneEnum.COMMON_ROOM,
        fromType: String = "用户主动送礼",
        callBack: (type: Int, extra: String?) -> Unit = { _, _ -> },
        statuBack: (type: Int, gift: LiveGift?) -> Unit = { _, _ -> }
) {
    if (supportFragmentManager.findFragmentByTag("SendGiftDialog") == null) {
        SendGiftDialog().apply {
            this.userId = userId
            this.userName = userName
            this.roomId = roomId
            this.callBack = callBack
            this.statuBack = statuBack
            this.roseFromScene = roseFromScene
            this.fromType = fromType
        }.show(supportFragmentManager, "SendGiftDialog")
    }
}


class SendGiftDialog : BottomDialogFragment() {
    var userId = 0
    var userName = ""
    var roomId = ""
    var callBack: (type: Int, extra: String?) -> Unit = { _, _ -> }
    var statuBack: (type: Int, gift: LiveGift?) -> Unit = { _, _ -> }
    var roseFromScene: RoseFromSceneEnum = RoseFromSceneEnum.PERSONAL_PAGE
    var fromType: String = "用户主动送礼"
    val checkNewEvent by lazy {
        object : IMAllEventManager() {
            override fun onNewMessages(msgs: MutableList<TIMMessage>?): Boolean {
                val customList = msgs.orEmpty()
                        .mapNotNull { it.mapToMeiMessage() as? CustomMessage }
                        .filter { !it.isDeleted() && it.customMsgType == CustomType.send_gift }//去掉已删除的消息
                        .mapNotNull { it.customInfo?.data as? ChickCustomData }
                        .filter { it.userId == JohnUser.getSharedUser().userID }
                if (customList.isNotEmpty()) {
                    requestGiftInfo {
                        requestLevelInfo()
                        spiceHolder().apiSpiceMgr.requestUserInfo(arrayOf(JohnUser.getSharedUser().userID), true)
                    }
                }

                return super.onNewMessages(msgs)
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.dialog_send_gift, container, false)
        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkNewEvent.registered()
        //IM聊天界面样式的区分
        if (roseFromScene == RoseFromSceneEnum.EXCLUSIVE_CHAT) {
            gift_root.delegate.backgroundColor = Color.WHITE
            info_title.isVisible = false
            level_container.isVisible = false
            text_my_rose.setTextColor(Color.parseColor("#333333"))
            rose_count.setTextColor(Color.parseColor("#333333"))
        }
        rose_count.text = (MeiUser.getSharedUser().extra?.coinBalance ?: 0.00).toInt().toString()

        getCacheUserInfo(userId).let {
            user_avatar.glideDisplay(it?.avatar.orEmpty(), it?.gender.genderAvatar(it?.isPublisher))
            user_name.text = it?.nickname.orEmpty()
        }
        gift_list_view.roseFromScene = roseFromScene
        gift_list_view.fromType = fromType

        looking_permission.isVisible = tabbarConfig.showUserLevel == true
        //直播间资料卡进入的 无需再显示资料卡按钮
        requestData()
        top_up.setOnClickListener {
            val payFromType = if (roseFromScene == RoseFromSceneEnum.PERSONAL_PAGE) {
                PayFromType.USER_PAGE
            } else {
                PayFromType.BROADCAST_SEND_GIFT
            }
            activity?.showPayDialog(payFromType, gift_root.height) {
                if (it && isAdded) {
                    activity.myRose { money ->
                        if (money > 0) {
                            rose_count.text = money.toInt().toString()
                        }
                    }
                }
            }
        }
        icon_cancel.setOnClickListener { dismissAllowingStateLoss() }

        icon_cancel.setOnClickListener {
            dismiss()
        }
        requestLevelInfo()
    }

    private fun requestLevelInfo() {
        (activity as MeiCustomActivity).apiSpiceMgr.executeToastKt(UserLevelInfoRequest(), success = {
            updateProgress(it.data)
        }, failure = {
            dismissAllowingStateLoss()
        })
    }

    private fun requestData() {
        (activity as MeiCustomActivity).myRose {
            if (it > 0) {
                rose_count.text = it.toInt().toString()
            }
        }
        rose_count.text = MeiUser.getSharedUser().extra?.coinBalance?.toInt().toString()
        if (giftInfoList.isNotEmpty()) {
            gift_list_view.notifyGiftListData(userId, userName, roomId, giftInfoList, statuBack) { isHide, balance ->
                if (isAdded) {
                    rose_count.text = balance.toInt().toString()
                    if (isHide) {
                        dismissAllowingStateLoss()
                    }
                }
            }
            return
        }

        (activity as MeiCustomActivity).apiSpiceMgr.executeKt(GiftListInfoRequest(), success = { result ->
            result?.data?.let {
                val giftList = it.gifts.orEmpty()
                if (giftList.isNotEmpty()) {
                    giftInfoList.clear()
                    giftInfoList.addAll(giftList)
                    gift_list_view.notifyGiftListData(userId, userName, roomId, giftInfoList, statuBack) { isHide, balance ->
                        if (isAdded) {
                            rose_count.text = balance.toInt().toString()
//                            MeiUser.getSharedUser().extra?.coinBalance = balance
                            if (isHide) {
                                dismissAllowingStateLoss()
                            }
                        }
                    }
                } else {
                    dismissAllowingStateLoss()
                }
            }
        }, failure = {
            dismissAllowingStateLoss()
        })
    }

    @Suppress("DEPRECATION")
    fun updateProgress(userLevelInfo: UserLevelInfo) {
        level_text.apply {
            level_text.setBackgroundResource(getBackGroundLevelResource(userLevelInfo.level, LevelSize.Big))
            level_text.text = userLevelInfo.level.toString()
            if (userLevelInfo.level > 9) {
                level_text.setPadding(0, 0, dip(6), 0)
            } else {
                level_text.setPadding(0, 0, dip(10), 0)
            }
        }
        if (userLevelInfo.nextLevel == 0 && userLevelInfo.levelUpNeed == 0 && userLevelInfo.needSend == 0) {
            next_level_need.text = "更多等级敬请期待"
            level_progress.progress = 100
            return
        }

        next_level_need.text = Html.fromHtml("再送" + "<font color = \"#FFD638\">" + userLevelInfo.needSend + "</font>" + "心币，即可升到Lv." + userLevelInfo.nextLevel)
        level_progress.progress = ((userLevelInfo.levelUpNeed - userLevelInfo.needSend) * 100) / if (userLevelInfo.levelUpNeed > 0) userLevelInfo.levelUpNeed else 1
        looking_permission.setOnClickListener {
            Nav.toAmanLink(activity, userLevelInfo.enterLevelPage.orEmpty())
        }

    }

    override fun getBackgroundAlpha(): Float {
        return if (roseFromScene == RoseFromSceneEnum.EXCLUSIVE_CHAT) 0.6f else 0f
    }

    override fun onDestroyView() {
        super.onDestroyView()
        checkNewEvent.unregistered()
    }
}