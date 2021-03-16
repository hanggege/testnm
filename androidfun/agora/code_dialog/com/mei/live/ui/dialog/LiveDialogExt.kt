package com.mei.live.ui.dialog

import android.Manifest
import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.joker.im.custom.CustomType
import com.joker.im.custom.chick.ChickCustomData
import com.joker.im.custom.chick.ServiceInfo
import com.joker.im.custom.chick.SplitText
import com.mei.GrowingUtil
import com.mei.base.common.FOLLOW_USER_STATE
import com.mei.dialog.getExclusiveProtectView
import com.mei.init.spiceHolder
import com.mei.live.ext.createSplitSpannable
import com.mei.live.manager.checkMuteState
import com.mei.live.manager.genderAvatar
import com.mei.live.manager.liveSendCustomMsg
import com.mei.live.ui.VideoLiveRoomActivity
import com.mei.live.ui.fragment.inviteUpStream
import com.mei.live.ui.getRoomTypeForGrowingTrack
import com.mei.orc.Cxt
import com.mei.orc.event.postAction
import com.mei.orc.ext.dip
import com.mei.orc.ext.layoutInflaterKt
import com.mei.orc.imageload.glide.glideDisplay
import com.mei.orc.john.model.JohnUser
import com.mei.orc.rxpermission.requestMulPermissionZip
import com.mei.orc.ui.loading.LoadingToggle
import com.mei.orc.ui.toast.UIToast
import com.mei.orc.util.click.setOnClickNotDoubleListener
import com.mei.widget.choince.ChoiceView
import com.mei.widget.round.RoundImageView
import com.mei.wood.R
import com.mei.wood.cache.getCacheUserInfo
import com.mei.wood.dialog.*
import com.mei.wood.extensions.executeToastKt
import com.mei.wood.extensions.subscribeBy
import com.mei.wood.ui.MeiCustomBarActivity
import com.net.MeiUser
import com.net.model.room.RoomApplyType
import com.net.model.room.RoomInfo
import com.net.model.room.RoomType
import com.net.model.room.RoomUserQueue
import com.net.network.chick.friends.FollowFriendRequest
import com.net.network.exclusive.HasBuyExclusiveServiceRequest
import com.net.network.room.RoomQuestionListRequest
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import java.util.concurrent.TimeUnit

/**
 * Created by hang on 2020/5/30.
 */
/**
 * 专属服务确认弹窗
 */
@SuppressLint("SetTextI18n")
fun FragmentActivity.showSpecialServiceDialog(data: ChickCustomData, back: (NormalDialog, Int) -> Unit = { _, _ -> }): NormalDialog {
    val dialog = NormalDialogLauncher.newInstance()
    val contentView = layoutInflaterKt(R.layout.dialog_exclusive_service)

    getCacheUserInfo(data.userId).let { userInfo ->
        contentView.findViewById<RoundImageView>(R.id.user_avatar_img).glideDisplay(userInfo?.avatar.orEmpty(), userInfo?.gender.genderAvatar(userInfo?.isPublisher))
        contentView.findViewById<TextView>(R.id.mentor_name).text = userInfo?.nickname.orEmpty()
    }
    contentView.findViewById<TextView>(R.id.live_content).apply {
        gravity = when (data.contentAlign) {
            0 -> Gravity.START
            1 -> Gravity.CENTER
            else -> Gravity.END
        }
        text = data.content.createSplitSpannable(Color.BLACK)
    }

    contentView.findViewById<TextView>(R.id.service_name).text = data.serviceInfo?.serviceName.orEmpty()
    contentView.findViewById<TextView>(R.id.service_duration).text = "${data.serviceInfo?.timeRemaining ?: 0}分钟"
    val leftButton = contentView.findViewById<TextView>(R.id.close_dialog_btn).apply {
        text = getCountDownText(data.leftText, data.timeOut, data.dialogConfig?.timeCountdown ?: 0)
        isVisible = data.leftText.isNotEmpty()
        setOnClickNotDoubleListener {
            contentView.isSelected = true
            back(dialog, DISSMISS_DO_CANCEL)
        }
    }
    val rightButton = contentView.findViewById<TextView>(R.id.submit_dialog_btn).apply {
        text = if (data.leftText.isEmpty()) getCountDownText(data.rightText, data.timeOut, data.dialogConfig?.timeCountdown
                ?: 0) else data.rightText
        isVisible = data.rightText.isNotEmpty()
        setOnClickNotDoubleListener {
            contentView.isSelected = true
            this@showSpecialServiceDialog.requestMulPermissionZip(Manifest.permission.RECORD_AUDIO, Manifest.permission.CAMERA) { has ->
                if (!has) this@showSpecialServiceDialog.showGoPermissionSettingDialog()
                back(dialog, if (has) DISSMISS_DO_OK else DISSMISS_DO_CANCEL)
            }
        }
    }
    when {
        data.leftText.isEmpty() -> {
            rightButton.updateLayoutParams { width = dip(150) }
        }
        data.rightText.isEmpty() -> {
            leftButton.updateLayoutParams { width = dip(150) }
        }
        else -> {
            leftButton.updateLayoutParams { width = dip(110) }
            rightButton.updateLayoutParams { width = dip(110) }
        }
    }

    contentView.findViewById<ChoiceView>(R.id.choice_view).apply {
        isVisible = data.dialogConfig?.rightCloseButton ?: false
        setOnClickNotDoubleListener {
            contentView.isSelected = true
            back(dialog, DISSMISS_DO_CANCEL)
        }
    }

    if (data.timeOut > 0) {
        Observable.interval(1, TimeUnit.SECONDS)
                .compose(dialog.bindToLifecycle())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy {
                    if (data.leftText.isEmpty()) {
                        rightButton.text = getCountDownText(data.rightText, (data.timeOut - it - 1).toInt(), data.dialogConfig?.timeCountdown
                                ?: 0)
                    } else {
                        leftButton.text = getCountDownText(data.leftText, (data.timeOut - it - 1).toInt(), data.dialogConfig?.timeCountdown
                                ?: 0)
                    }
                    if (it == data.timeOut.toLong() - 1 && !dialog.isHidden) {
                        contentView.isSelected = true
                        when {
                            dialog.lifecycle.currentState == Lifecycle.State.CREATED -> {
                                back(dialog, DISSMISS_DO_NOTHING)
                            }
                            data.dialogConfig?.timeoutResult != 1 -> {
                                back(dialog, data.dialogConfig?.timeoutResult ?: 2)
                            }
                            else -> {
                                this@showSpecialServiceDialog.requestMulPermissionZip(Manifest.permission.RECORD_AUDIO, Manifest.permission.CAMERA) { has ->
                                    if (!has) this@showSpecialServiceDialog.showGoPermissionSettingDialog()
                                    back(dialog, if (has) DISSMISS_DO_OK else DISSMISS_DO_CANCEL)
                                }
                            }
                        }
                        dialog.dismissAllowingStateLoss()
                    }
                }
    }

    dialog.showDialog(this, DialogData(customView = contentView, canceledOnTouchOutside = data.dialogConfig?.canceledOnTouchOutside
            ?: false, key = "invite_dialog", backCanceled = data.dialogConfig?.backCanceled
            ?: false),
            back = object : DialogBack {
                override fun handle(state: Int) {
                    if (!contentView.isSelected) back(dialog, state)
                }
            })
    return dialog
}

fun getCountDownText(text: String, time: Int, timeCountdown: Int) = when (timeCountdown) {
    0 -> text
    1 -> "$time$text"
    else -> "$text($time)"
}

/**
 * 专属服务保护弹窗
 */
@SuppressLint("SetTextI18n")
fun FragmentActivity.showSpecialProtectDialog(roomInfo: RoomInfo, back: (NormalDialog, Int) -> Unit) {
    val dialog = NormalDialogLauncher.newInstance()
    val contentView = layoutInflaterKt(R.layout.view_exclusice_context_dialog)
    contentView.findViewById<TextView>(R.id.studio_label).apply {
        isVisible = roomInfo.isSpecialStudio
        text = roomInfo.groupInfo?.groupName.orEmpty()
    }

    val recommendInfo = roomInfo.specialServiceSampleDto?.recommendInfo
    contentView.findViewById<TextView>(R.id.recommend_hint).apply {
        isVisible = recommendInfo?.recommendTips.orEmpty().isNotEmpty()
        text = recommendInfo?.recommendTips.orEmpty()
    }
    val createUser = roomInfo.createUser
    contentView.findViewById<RoundImageView>(R.id.mentor_avatar).glideDisplay(createUser?.avatar.orEmpty(), createUser?.gender.genderAvatar(true))
    contentView.findViewById<TextView>(R.id.mentor_name).text = createUser?.nickName.orEmpty()
    contentView.findViewById<TextView>(R.id.special_service_name).apply {
        paint.isFakeBoldText = true
        text = roomInfo.specialServiceSampleDto?.serviceName.orEmpty()
    }
    contentView.findViewById<TextView>(R.id.special_service_price).text = "${roomInfo.specialServiceSampleDto?.serviceCost}"
    contentView.findViewById<TextView>(R.id.special_service_specification).text = "/${roomInfo.specialServiceSampleDto?.serviceMinutes ?: 0}分钟  " +
            if (roomInfo.specialServiceSampleDto?.serviceMin ?: 0 > 1) "${roomInfo.specialServiceSampleDto?.serviceMin}次起" else ""

    contentView.findViewById<LinearLayout>(R.id.recommend_service_info).isVisible = recommendInfo?.recommendTips.orEmpty().isNotEmpty()
    contentView.findViewById<ImageView>(R.id.recommend_anchor_avatar).glideDisplay(recommendInfo?.personImage.orEmpty(), recommendInfo?.gender.genderAvatar(true))
    contentView.findViewById<TextView>(R.id.recommend_anchor_name).text = recommendInfo?.nickname.orEmpty()
    contentView.findViewById<View>(R.id.history_service_ll).isVisible = roomInfo.specialServiceSampleDto?.priceText?.isNotEmpty() != true
    contentView.findViewById<TextView>(R.id.price_text_tv)?.apply {
        isVisible = roomInfo.specialServiceSampleDto?.priceText?.isNotEmpty() == true
        text = roomInfo.specialServiceSampleDto?.priceText.orEmpty().createSplitSpannable(Cxt.getColor(R.color.color_333333))
    }

    fun growingTrack(event: String, clickType: String) {
        GrowingUtil.track(event, "popup_type", "专属服务弹窗", "view_type", "专属服务弹窗",
                "popup_click_type", clickType, "room_type", roomInfo.getRoomTypeForGrowingTrack(),
                "time_stamp", (System.currentTimeMillis() / com.mei.orc.unit.TimeUnit.SECOND).toString())
    }

    contentView.findViewById<LinearLayout>(R.id.live_service).setOnClickListener {
        back(dialog, 1)
    }
    contentView.findViewById<ChoiceView>(R.id.choice_view).setOnClickListener {
        growingTrack("push_popup_click", "×")
        back(dialog, 0)
    }
    contentView.findViewById<TextView>(R.id.confirm_btn).setOnClickListener {
        growingTrack("push_popup_click", "查看详情")
        back(dialog, 1)
    }
    growingTrack("push_popup_view", "")
    dialog.showDialog(this, DialogData(customView = contentView), okBack = {})
}

/**
 * 私密保护弹窗
 */
fun FragmentActivity.showExclusiveProtectDialog(price: Int) {
    val dialog = NormalDialogLauncher.newInstance()
    dialog.showDialog(this, DialogData(customView = getExclusiveProtectView(this, price) {
        dialog.dismissAllowingStateLoss()
    }), okBack = {})
}

/**
 *选择服务弹窗
 */
fun VideoLiveRoomActivity.showSelectServiceDialog(loading: LoadingToggle, userId: Int, applyOption: RoomUserQueue.InviteOption, back: () -> Unit = {}) {
    loading.showLoading(true)
    apiSpiceMgr.executeToastKt(HasBuyExclusiveServiceRequest(userId, JohnUser.getSharedUser().userID), success = {
        val service = it.data?.service
        if (it.data == null || service.isNullOrEmpty()) {
            UIToast.toast("当前无服务")
        } else if (service.size == 1) {
            inviteUpStream(roomId, userId, RoomApplyType.parseValue(applyOption.applyType), RoomType.parseValue(applyOption.roomType), applyOption.costTips.orEmpty(), service[0].specialServiceOrderId, back)
        } else {
            val dialog = NormalDialogLauncher.newInstance()
            val content = layoutInflaterKt(R.layout.dialog_select_service)
            content.findViewById<ChoiceView>(R.id.back_choice).setOnClickListener {
                dialog.dismissAllowingStateLoss()
            }
            val serviceList = content.findViewById<RecyclerView>(R.id.service_list)

            serviceList.adapter = object : BaseQuickAdapter<ServiceInfo, BaseViewHolder>(R.layout.item_select_service, service) {
                override fun convert(holder: BaseViewHolder, item: ServiceInfo) {
                    holder.getView<TextView>(R.id.service_title).apply {
                        paint.isFakeBoldText = true
                        text = item.serviceName
                    }
                    holder.setText(R.id.remain_time, "剩余时长：${item.timeRemaining}分钟")

                    holder.itemView.setOnClickListener {
                        inviteUpStream(roomId, userId, RoomApplyType.parseValue(applyOption.applyType), RoomType.parseValue(applyOption.roomType), applyOption.costTips.orEmpty(), item.specialServiceOrderId) {
                            back()
                            dialog.dismissAllowingStateLoss()
                        }
                    }
                }
            }
            dialog.showDialog(this, DialogData(customView = content), okBack = {})
        }
    }, finish = {
        loading.showLoading(false)
    })
}

/**
 * 退出直播间关注弹窗 0-》下麦 1-》退出
 */
fun FragmentActivity.showQuitRoomFollowDialog(isUpstream: Boolean, roomId: String, userId: Int, back: (Int) -> Unit) {
    val dialog = NormalDialogLauncher.newInstance()
    val contentView = layoutInflaterKt(R.layout.dialog_quit_room_follow)

    contentView.findViewById<TextView>(R.id.follow_title).text = if (isUpstream) "下麦前关注一下达人吧" else "关注一下，欢迎常来"

    contentView.findViewById<TextView>(R.id.stop_stream).apply {
        isVisible = isUpstream
        setOnClickListener {
            dialog.dismissAllowingStateLoss()
            back(0)
        }
    }

    contentView.findViewById<TextView>(R.id.quit_room).setOnClickListener {
        dialog.dismissAllowingStateLoss()
        back(1)
    }

    contentView.findViewById<View>(R.id.back_choice).setOnClickListener {
        dialog.dismissAllowingStateLoss()
    }

    contentView.findViewById<TextView>(R.id.follow_stop_stream).apply {
        text = if (isUpstream) "关注并下麦" else "关注并退出"
        setOnClickListener {
            dialog.dismissAllowingStateLoss()
            spiceHolder().apiSpiceMgr.executeToastKt(FollowFriendRequest(userId, 3, roomId), success = {
                if (it.isSuccess) {
                    postAction(FOLLOW_USER_STATE, Pair(userId, true))
                }
            })
            if (isUpstream) {
                back(0)
            } else {
                back(1)
            }
        }
    }

    dialog.showDialog(this, DialogData(customView = contentView), okBack = {})
}

/**
 * 直播间提问pop
 */
fun ImageView.showConsultQuestionPop(roomId: String, isCreator: Boolean, msgState: Int) {
    (context as? MeiCustomBarActivity)?.requestQuestionList { questions ->
        val popupWindow = PopupWindow(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT).apply {
            isFocusable = true
            isOutsideTouchable = true
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            contentView = layoutInflaterKt(R.layout.pop_select_question).apply {
                val questionRecycler = findViewById<RecyclerView>(R.id.question_recycler)
                val adapter = object : BaseQuickAdapter<String, BaseViewHolder>(R.layout.item_consult_question) {
                    override fun convert(holder: BaseViewHolder, item: String) {
                        holder.setText(R.id.item_question, item)

                        holder.itemView.setOnClickListener {
                            if (isCreator) {
                                UIToast.toast("主播不能咨询")
                            } else {
                                checkMuteState(roomId) {
                                    if (it) {
                                        liveSendCustomMsg(roomId, CustomType.send_text, applyData = {
                                            val name = MeiUser.getSharedUser().info?.nickname.orEmpty()
                                            content = arrayListOf<SplitText>().apply {
                                                if (name.isNotEmpty()) add(SplitText("$name：", "#B3FFFFFF"))
                                                add(SplitText(item))
                                            }
                                            fromUserHandle = true
                                            this.msgState = msgState
                                        }) { code ->
                                            when (code) {
                                                0 -> {
                                                    dismiss()
                                                    UIToast.toast(context, "发送成功")
                                                }
                                                10017 -> UIToast.toast(context, "你已被禁言")
                                                else -> UIToast.toast(context, "发送失败$code，请重试")
                                            }
                                        }
                                    } else {
                                        UIToast.toast(context, "您已被禁言，无法发送消息")
                                    }
                                }
                            }
                        }
                    }

                }
                questionRecycler.adapter = adapter
                adapter.setList(questions)
                findViewById<TextView>(R.id.change_question).setOnClickListener {
                    (context as? MeiCustomBarActivity)?.requestQuestionList {
                        adapter.setList(it)
                    }
                }
            }
        }
        val location = IntArray(2)
        getLocationOnScreen(location)
        popupWindow.showAtLocation(this, Gravity.NO_GRAVITY, dip(10), location[1] - dip(273))
        glideDisplay(R.drawable.icon_question_arrow_down)
        popupWindow.setOnDismissListener {
            glideDisplay(R.drawable.icon_question_arrow_up)
        }
    }
}


fun MeiCustomBarActivity.requestQuestionList(back: (List<String>) -> Unit) {
    showLoading(true)
    apiSpiceMgr.executeToastKt(RoomQuestionListRequest(), success = { response ->
        back(response.data?.questions.orEmpty())
    }, finish = { showLoading(false) })
}