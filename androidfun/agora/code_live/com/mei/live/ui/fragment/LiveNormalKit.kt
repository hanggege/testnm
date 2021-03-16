package com.mei.live.ui.fragment

import android.Manifest
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import com.mei.GrowingUtil
import com.mei.base.common.APPLY_UPSTREAM_STATE
import com.mei.dialog.PayFromType
import com.mei.dialog.showPayDialog
import com.mei.init.spiceHolder
import com.mei.live.manager.checkMuteState
import com.mei.live.ui.VideoLiveRoomActivity
import com.mei.live.ui.dialog.showGoPermissionSettingDialog
import com.mei.live.ui.dialog.showSingleBtnCommonDialog
import com.mei.orc.event.postAction
import com.mei.orc.ext.layoutInflaterKt
import com.mei.orc.john.model.JohnUser
import com.mei.orc.rxpermission.requestMulPermissionZip
import com.mei.orc.ui.toast.UIToast
import com.mei.orc.util.save.getBooleanMMKV
import com.mei.orc.util.save.putMMKV
import com.mei.wood.R
import com.mei.wood.cache.requestUserInfo
import com.mei.wood.dialog.DialogData
import com.mei.wood.dialog.NormalDialogLauncher
import com.mei.wood.extensions.executeKt
import com.mei.wood.extensions.executeToastKt
import com.mei.wood.ui.MeiCustomActivity
import com.net.model.room.RoomApplyLaunch
import com.net.model.room.RoomApplyType
import com.net.model.room.RoomType
import com.net.network.room.RoomApplyLaunchRequest
import kotlinx.android.synthetic.main.fragment_live_video_split_layout.*

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2019-12-15
 * 申请连线
 */
fun MeiCustomActivity.applyUpStream(roomId: String, roomType: RoomType, from: String = "", videoMode: Int = 0, couponNum: Int = 0, back: (RoomApplyLaunch?) -> Unit = {}) {
    requestMulPermissionZip(Manifest.permission.RECORD_AUDIO, Manifest.permission.CAMERA) {
        if (!it) {
            showGoPermissionSettingDialog()
        } else {
            checkMuteState(roomId, JohnUser.getSharedUser().userID) {
                if (it) {
                    val request = RoomApplyLaunchRequest().apply {
                        userId = JohnUser.getSharedUser().userID
                        this.roomId = roomId
                        type = RoomApplyType.UPSTREAM
                        room_type = roomType
                        this.from = from
                        this.videoMode = videoMode
                        this.couponNum = couponNum
                    }
                    showLoading(true)
                    apiSpiceMgr.executeKt(request, success = { response ->
                        when {
                            response.isSuccess -> {
                                back(response.data)
                            }
                            response.rtn == 700 || response.rtn == 406 -> {
                                showPayDialog(PayFromType.BROADCAST)
                            }
                            response.rtn == 606 -> {
                                showSingleBtnCommonDialog(response.msg.orEmpty(), btnText = "好的") { dialog ->
                                    dialog.dismissAllowingStateLoss()
                                }
                            }
                            else -> {
                                UIToast.toast(response.msg.orEmpty())
                            }
                        }
                    }, failure = { throwable ->
                        UIToast.toast(throwable?.msg.orEmpty())
                    }, finish = {
                        showLoading(false)
                    })
                } else {
                    UIToast.toast(this, "你已被禁言，无法操作")
                }
            }
        }
    }
}

/**
 * 邀请上麦
 */
fun MeiCustomActivity.inviteUpStream(roomId: String, userId: Int, applyType: RoomApplyType, roomType: RoomType, costTips: String, from: String = "", back: () -> Unit = {}) {
    apiSpiceMgr.requestUserInfo(arrayOf(userId)) {
        it.firstOrNull()?.let { userInfo ->
            checkMuteState(roomId, userId) { hasPermission ->
                if (hasPermission) {
                    when (userId) {
                        (activity as? VideoLiveRoomActivity)?.liveVideoSplitFragment?.user_video_layout?.userId -> {
                            UIToast.toast("用户连线中,暂时无法邀请")
                        }
                        else -> startInviteUpStreamRequest(roomId, applyType, roomType, userId, costTips, from, back)
                    }
                } else {
                    UIToast.toast("${userInfo.nickname}已被禁言，无法进行连线操作")
                }
            }
        }
    }
}

fun MeiCustomActivity.startInviteUpStreamRequest(roomId: String, applyType: RoomApplyType, roomType: RoomType, userId: Int, costTips: String, from: String = "", back: () -> Unit) {
    GrowingUtil.track(
            "push_popup_view",
            "popup_type", "主播端确认是否邀请用户连线",
            "time_stamp", (System.currentTimeMillis() / com.mei.orc.unit.TimeUnit.SECOND).toString())
    fun isShowInviteDialog(callback: (Boolean) -> Unit) {
        if (from.isNotEmpty()) {
            callback(true)
        } else {
            showInviteDialog(applyType, costTips) {
                GrowingUtil.track(
                        "push_popup_click",
                        "popup_type", "主播端确认是否邀请用户连线",
                        "popup_click_type", if (it) "确认" else "取消",
                        "time_stamp", (System.currentTimeMillis() / com.mei.orc.unit.TimeUnit.SECOND).toString())
                callback(it)
            }
        }
    }
    isShowInviteDialog {
        if (it) {
            val request = RoomApplyLaunchRequest().apply {
                type = RoomApplyType.INVITE_UPSTREAM
                this.userId = userId
                room_type = roomType
                this.roomId = roomId
                this.from = from
            }
            showLoading(true)
            spiceHolder().apiSpiceMgr.executeToastKt(request, success = { response ->
                if (response.isSuccess) {
//                            dialog.dismissAllowingStateLoss()
                    UIToast.toast("已发送连线邀请，请耐心等待用户回复")
                    back()
                }
            }, finish = {
                showLoading(false)
            })
        }
    }
}

fun FragmentActivity.showInviteDialog(applyType: RoomApplyType, costTips: String, back: (Boolean) -> Unit) {
    val saveKey = "invite_need_show_dialog${applyType.name}"
    if (saveKey.getBooleanMMKV(true)) {
        val dialog = NormalDialogLauncher.newInstance()
        val contentView = layoutInflaterKt(R.layout.view_confirm_invite_upstream)
        val notHintCheck = contentView.findViewById<ImageView>(R.id.not_hint_check)
        contentView.findViewById<TextView>(R.id.invite_title).text = costTips
        contentView.findViewById<View>(R.id.not_hint).setOnClickListener {
            notHintCheck.isSelected = !notHintCheck.isSelected
        }

        contentView.findViewById<View>(R.id.cancel_dialog_btn).setOnClickListener {
            dialog.dismissAllowingStateLoss()
            back(false)
        }
        contentView.findViewById<View>(R.id.confirm_dialog_btn).setOnClickListener {
            saveKey.putMMKV(!notHintCheck.isSelected)
            dialog.dismissAllowingStateLoss()
            back(true)
        }

        dialog.showDialog(this, DialogData(customView = contentView, canceledOnTouchOutside = true),
                okBack = {})
    } else {
        back(true)
    }
}