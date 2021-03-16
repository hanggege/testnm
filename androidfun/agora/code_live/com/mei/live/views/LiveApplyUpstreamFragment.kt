package com.mei.live.views

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.buildSpannedString
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentActivity
import com.joker.im.bindEventLifecycle
import com.joker.im.custom.CustomType
import com.joker.im.custom.chick.ChickCustomData
import com.joker.im.listener.IMAllEventManager
import com.joker.im.mapToMeiMessage
import com.joker.im.message.CustomMessage
import com.mei.agora.event.AgoraEventHandler
import com.mei.base.common.APPLY_UPSTREAM_ENABLE
import com.mei.base.common.APPLY_UPSTREAM_STATE
import com.mei.base.common.UPSTREAM_ACTION
import com.mei.live.manager.genderAvatar
import com.mei.live.ui.VideoLiveRoomActivity
import com.mei.live.ui.dialog.showDoubleBtnCommonView
import com.mei.live.ui.dialog.showLiveListDialog
import com.mei.live.ui.dialog.showUpstreamChoiceDialog
import com.mei.live.ui.fragment.RECHARGE_SERVICE_SUCCESS_ID
import com.mei.live.ui.fragment.startApplyUpstream
import com.mei.orc.event.bindAction
import com.mei.orc.ext.setListenerEnd
import com.mei.orc.imageload.glide.glideDisplay
import com.mei.orc.john.model.JohnUser
import com.mei.orc.ui.toast.UIToast
import com.mei.orc.util.click.setOnClickNotDoubleListener
import com.mei.orc.util.span.CustomImageSpan
import com.mei.wood.R
import com.mei.wood.extensions.appendSpannable
import com.mei.wood.extensions.executeToastKt
import com.mei.wood.ui.CustomSupportFragment
import com.net.MeiUser
import com.net.model.room.RoomInfo
import com.net.model.room.RoomUserTypeEnum
import com.net.model.user.UserInfo
import com.net.network.room.RoomUserQueueRequest
import com.net.network.room.UpstreamTypeListRequest
import com.net.network.room.UpstreamTypeTextRequest
import com.tencent.imsdk.TIMMessage
import io.agora.rtc.IRtcEngineEventHandler
import kotlinx.android.synthetic.main.view_live_apply_upstream.*

/**
 * Created by hang on 2020/6/29.
 * 我要连线按钮
 */
class LiveApplyUpstreamFragment : CustomSupportFragment(), AgoraEventHandler {

    var roomInfo: RoomInfo = RoomInfo()
        set(value) {
            field = value
            if (isAdded) {
                initView()
            }
        }

    private val checkNewEvent: IMAllEventManager by lazy {
        object : IMAllEventManager() {
            /** 监听最新消息 **/
            override fun onNewMessages(msgs: MutableList<TIMMessage>?): Boolean {
                val customList = msgs.orEmpty()
                        .mapNotNull { it.mapToMeiMessage() as? CustomMessage }
                        .filter { !it.isDeleted() }//去掉已删除的消息

                /** 只接收群消息与发送指令的消息 **/
                customList.filter { it.peer == roomInfo.roomId }
                        .forEach { handleIMEvent(it) }
                return super.onNewMessages(msgs)
            }
        }
    }

    private fun handleIMEvent(msg: CustomMessage) {
        val type = msg.customMsgType
        (msg.customInfo?.data as? ChickCustomData)?.also { data ->
            when (type) {
                CustomType.people_changed -> {
                    if (data.reason == 4 && data.targetUserId == JohnUser.getSharedUser().userID) {
                        showServiceCouponInfo()
                    }
                    if ((data.reason == 3 || data.reason == 1) && roomInfo.isCreator) {
                        showNextApplyMicUserInfo()
                    }
                }
                CustomType.room_apply_switch_change -> {
                    if (data.roomId == roomInfo.roomId) {
                        roomInfo.applyUpstreamEnable = data.mode == 1

                        if (data.mode == 1) {
                            apply_tips_text.text = "我要连线"
                        } else {
                            apply_tips_text.text = "暂停连线"
                        }
                    }
                }
                CustomType.apply_upstream, CustomType.apply_warning -> {
                    if (roomInfo.isCreator) {
                        showNextApplyMicUserInfo()
                    }
                }
                CustomType.send_gift -> {
                    if (data.roomId == roomInfo.roomId && data.gift?.giftId == RECHARGE_SERVICE_SUCCESS_ID && data.userId == JohnUser.getSharedUser().userID) {
                        showServiceCouponInfo()
                    }
                }
                CustomType.room_reject_up -> {
                    if (roomInfo.isCreator) {
                        showNextApplyMicUserInfo()
                    }
                }
                else -> {
                }
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.view_live_apply_upstream, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initEvent()
        upstream_hint_pop.paint.isFakeBoldText = true
        checkNewEvent.bindEventLifecycle(this)
        if (roomInfo.roomId.isNotEmpty()) {
            initView()
        }
    }

    private fun initEvent() {
        bindAction<Boolean>(UPSTREAM_ACTION) {
            if (it == true) {
                if (roomInfo.isCreator) {
                    showNextApplyMicUserInfo()
                } else {
                    hide()
                }
            } else {
                show()
                refreshApplyState(null)

                showServiceCouponInfo()
            }
        }

        bindAction<Boolean>(APPLY_UPSTREAM_STATE) {
            if (it == true) {
                refreshApplyState(MeiUser.getSharedUser().info)
            } else {
                refreshApplyState(null)
            }
        }
        bindAction<Boolean>(APPLY_UPSTREAM_ENABLE) {
            if (it == true) {
                apply_tips_text.text = "我要连线"
            } else {
                apply_tips_text.text = "暂停连线"
            }
        }
        view?.setOnClickNotDoubleListener {
            activity?.performApply()
        }
    }

    private fun initView() {
        if (isAdded) {
            if (roomInfo.isSpecialStudio) {
                hide()
            } else {
                show()

                apply_tips_text.paint.isFakeBoldText = true
                upstream_coupon_text.paint.isFakeBoldText = true
                if (roomInfo.applyUpstreamEnable) {
                    apply_tips_text.text = "我要连线"
                } else {
                    apply_tips_text.text = "暂停连线"
                }
                apply_end_text.text = buildSpannedString {
                    appendSpannable(" ", CustomImageSpan(requireActivity(), R.drawable.icon_check_mark))
                    append(" ${roomInfo.applySuccessText.orEmpty()}")
                }

                showServiceCouponInfo()
            }
        }
    }

    private fun refreshApplyState(user: UserInfo?) {
        apply_user_avatar.isVisible = user != null
        apply_end_text.isVisible = user != null

        icon_to_upstream.isVisible = user == null
        apply_tips_text.isVisible = user == null

        user?.also { apply_user_avatar.glideDisplay(it.avatar.orEmpty(), it.gender.genderAvatar()) }
        (activity as? VideoLiveRoomActivity)?.pendingUpStream = user?.userId == JohnUser.getSharedUser().userID
    }

    override fun onUserJoined(uid: Int, elapsed: Int) {
        if (isAdded) {
            if (roomInfo.isCreator) {
                showNextApplyMicUserInfo()
            }
        }
    }

    override fun onClientRoleChanged(oldRole: Int, newRole: Int) {
        super.onClientRoleChanged(oldRole, newRole)
        if (newRole == IRtcEngineEventHandler.ClientRole.CLIENT_ROLE_BROADCASTER && !roomInfo.isCreator) {
            hide()
        }
    }

    private fun hide() {
        view?.also {
            if (it.isVisible) {
                it.translationX = 0f
                it.animate().translationX(it.measuredWidth.toFloat()).setDuration(800).setListenerEnd {
                    if (isAdded) {
                        it.isVisible = false
                    }
                }.start()
            }
        }
    }

    private fun show() {
        if (!roomInfo.isSpecialStudio) {
            view?.also {
                if (it.isGone) {
                    it.translationX = it.measuredWidth.toFloat()
                    it.animate().translationX(0f).setDuration(300).setListenerEnd {
                        if (isAdded) {
                            it.isVisible = true
                        }
                    }.start()
                }
            }
        }
    }

    /**
     * 获取下一个申请上麦的用户信息
     */
    @SuppressLint("SetTextI18n")
    private fun showNextApplyMicUserInfo() {
        apiSpiceMgr.executeToastKt(RoomUserQueueRequest(roomInfo.roomId.orEmpty(), RoomUserTypeEnum.ROOM_APPLY_USER, 1), success = {
            val userItems = it.data?.userItems
            refreshApplyState(userItems?.getOrNull(0)?.user)

            upstream_hint_pop.isVisible = userItems.orEmpty().isNotEmpty()
            upstream_coupon_layout.isVisible = false
            upstream_label.isVisible = false
            upstream_hint_pop.text = "共${userItems.orEmpty().size}位申请连线"
        })
    }

    /**
     * 获取优惠券专属服务信息
     */
    private fun showServiceCouponInfo() {
        if (!roomInfo.isCreator) {
            apiSpiceMgr.executeToastKt(UpstreamTypeTextRequest(roomInfo.roomId), success = {
                if (it.data != null) {
                    upstream_coupon_layout.isVisible = it.data.type == 1
                    upstream_label.isVisible = it.data.type == 1
                    upstream_hint_pop.isVisible = it.data.type == 2

                    if (it.data.type == 1) {
                        upstream_coupon_text.text = it.data.text
                    } else {
                        upstream_hint_pop.text = it.data.text
                    }
                    refreshApplyState(if (it.data.hasApply) MeiUser.getSharedUser()?.info else null)
                }
            })
        }
    }
}

/**
 * 申请排麦
 */
fun FragmentActivity.performApply() {
    (this as? VideoLiveRoomActivity)?.run {
        if (roomInfo.isCreator) {
            /**当前用户是知心达人 则查看用户列表*/
            upstreamListDialog?.dismissAllowingStateLoss()
            showLiveListDialog(roomInfo.roomId, back = {
                upstreamListDialog = it
            }, dismiss = {
                upstreamListDialog = null
            })
        } else {
            if (liveVideoSplitFragment.upstreamUserIds.none { it == roomInfo.createUser?.userId } || !roomInfo.applyUpstreamEnable) {
                UIToast.toast("暂时不能申请，请等待主播开启连线功能")
            } else {
                showUpstreamChoiceDialog(roomId)
            }
        }
    }
}

fun VideoLiveRoomActivity.launchCouponApply(couponNum: Int) {
    showLoading(true)
    apiSpiceMgr.executeToastKt(UpstreamTypeListRequest(roomId), success = {
        if (it.isSuccess) {
            val data = it.data
            data?.option?.also {
                if (data.isApplyOtherRoom) {
                    /** 如果已经在其他直播间申请过连线*/
                    showDoubleBtnCommonView("连线申请提示", "您目前正在其他知心达人房间排队\n确定申请将从其他队列退出", "", "取消", "确定", false) { dialog, back ->
                        dialog.dismissAllowingStateLoss()
                        if (back == 1) {
                            startApplyUpstream(it, couponNum)
                        }
                    }
                } else {
                    startApplyUpstream(it, couponNum)
                }
            }
        }
    }, finish = { showLoading(false) })
}