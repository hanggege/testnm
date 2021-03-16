package com.mei.live.ui.dialog

import android.Manifest
import android.annotation.SuppressLint
import android.graphics.Color
import android.os.CountDownTimer
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.text.buildSpannedString
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import com.flyco.roundview.RoundTextView
import com.joker.im.custom.chick.ChickCustomData
import com.mei.dialog.PayFromType
import com.mei.live.ext.createSplitSpannable
import com.mei.live.ext.parseColor
import com.mei.live.manager.genderAvatar
import com.mei.live.views.ApplyUpstreamRechargeView
import com.mei.orc.Cxt
import com.mei.orc.ext.dip
import com.mei.orc.ext.dp
import com.mei.orc.ext.dp2px
import com.mei.orc.ext.layoutInflaterKt
import com.mei.orc.imageload.glide.glideDisplay
import com.mei.orc.rxpermission.requestMulPermissionZip
import com.mei.orc.util.click.setOnClickNotDoubleListener
import com.mei.orc.util.date.formatTimeToHMS2
import com.mei.orc.util.permission.PermissionCheck
import com.mei.widget.choince.ChoiceView
import com.mei.widget.gradient.GradientTextView
import com.mei.widget.gradient.applyViewDelegate
import com.mei.widget.round.RoundImageView
import com.mei.wood.R
import com.mei.wood.cache.getCacheUserInfo
import com.mei.wood.dialog.*
import com.mei.wood.extensions.appendLink
import com.mei.wood.extensions.subscribeBy
import com.net.model.room.RoomStatus
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import java.util.concurrent.TimeUnit

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2019-12-07
 */
fun FragmentActivity.showLiveAvatarDialog(
        data: ChickCustomData,
        back: (dialog: NormalDialog, Int) -> Unit
): NormalDialog {
    val contentView = layoutInflaterKt(R.layout.view_invite_live)
    val dialog = NormalDialogLauncher.newInstance()

    getCacheUserInfo(data.userId).let { user ->
        contentView.findViewById<RoundImageView>(R.id.user_avatar_img).glideDisplay(user?.avatar.orEmpty(), user?.gender.genderAvatar(user?.isPublisher))
        contentView.findViewById<TextView>(R.id.mentor_name).text = user?.nickname.orEmpty()
    }
    contentView.findViewById<ChoiceView>(R.id.choice_view).apply {
        isVisible = data.dialogConfig?.rightCloseButton ?: false
        setOnClickNotDoubleListener {
            contentView.isSelected = true
            back(dialog, DISSMISS_DO_CANCEL)
        }
    }
    // start 正在使用优惠券显示与隐藏处理
    contentView.findViewById<View>(R.id.invite_now_apply_coupon_ll).apply {
        isVisible = data.couponMessageItem != null
    }
    contentView.findViewById<TextView>(R.id.invite_now_apply_prefix).apply {
//        text = data.couponMessageItem?.prefix.orEmpty()
    }
    contentView.findViewById<TextView>(R.id.invite_now_apply_coupon_title).apply {
        text = data.couponMessageItem?.couponName.orEmpty()
    }
    contentView.findViewById<TextView>(R.id.invite_now_apply_coupon_content_hint).apply {
        text = data.couponMessageItem?.rightButtonTipsText.orEmpty()
    }
    // end   正在使用优惠券显示与隐藏处理
    contentView.findViewById<TextView>(R.id.live_content).apply {
        text = data.title
        isVisible = data.title.isNotEmpty()
    }
    contentView.findViewById<TextView>(R.id.live_hint_content).apply {
        text = data.summary
        isVisible = data.summary.isNotEmpty()
    }
    contentView.findViewById<TextView>(R.id.live_sub_hint_content).apply {
        text = data.extra?.request.orEmpty()
        isVisible = data.extra?.request?.isNotEmpty() == true
    }
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
            this@showLiveAvatarDialog.requestMulPermissionZip(Manifest.permission.RECORD_AUDIO, Manifest.permission.CAMERA) { has ->
                if (!has) this@showLiveAvatarDialog.showGoPermissionSettingDialog()
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
                                this@showLiveAvatarDialog.requestMulPermissionZip(Manifest.permission.RECORD_AUDIO, Manifest.permission.CAMERA) { has ->
                                    if (!has) this@showLiveAvatarDialog.showGoPermissionSettingDialog()
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


/**
 * 两个按钮通用弹窗 -》1.申请付费连线
 * 2.确认关闭直播
 * 3.连线申请提示
 * 4.知心达人同意与你连线
 * 5.邀请进入专属房
 */
@SuppressLint("SetTextI18n")
fun FragmentActivity.showDoubleBtnCommonView(title: String = "", content: String = "", hint: String = "", leftBtn: String = "", rightBtn: String = "",
                                             isShowCountDown: Boolean = false, canceledOnTouchOutside: Boolean = true, isCanClose: Boolean = false, isHighLight: Boolean = true,
                                             back: (NormalDialog, Int) -> Unit = { _, _ -> }): NormalDialog {
    val contentView = layoutInflaterKt(R.layout.view_double_btn_common)
    val dialog = NormalDialogLauncher.newInstance()
    contentView.findViewById<TextView>(R.id.invite_title).apply {
        text = title
        paint.isFakeBoldText = true
        isVisible = title.isNotEmpty()
    }
    contentView.findViewById<TextView>(R.id.invite_content).apply {
        text = content
        isVisible = content.isNotEmpty()
    }
    contentView.findViewById<TextView>(R.id.hint_text).apply {
        text = hint
        isVisible = hint.isNotEmpty()
    }
    val cancelBtn = contentView.findViewById<RoundTextView>(R.id.cancel_dialog_btn).apply {
        text = leftBtn
        isVisible = leftBtn.isNotEmpty()
        setTextColor(if (isHighLight) Cxt.getColor(R.color.color_999999) else Cxt.getColor(R.color.color_333333))
        setOnClickNotDoubleListener {
            contentView.isSelected = true
            back(dialog, 0)
        }
    }
    if (isShowCountDown) {
        Observable.interval(1, TimeUnit.SECONDS)
                .compose(dialog.bindToLifecycle())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy {
                    cancelBtn.text = "$leftBtn(${30 - it})"
                    if (it == 30L) {
                        back(dialog, 0)
                    }
                }
    }
    contentView.findViewById<GradientTextView>(R.id.confirm_dialog_btn).apply {
        text = rightBtn
        isVisible = rightBtn.isNotEmpty()
        delegate.applyViewDelegate {
            if (isHighLight) {
                startColor = ContextCompat.getColor(context, R.color.color_FF7F33)
                endColor = ContextCompat.getColor(context, R.color.color_FF3F36)
                radius = 22.5f.dp2px
            } else {
                startColor = ContextCompat.getColor(context, R.color.color_f8f8f8)
                radius = 20.dp
            }
        }
        setTextColor(if (isHighLight) Color.WHITE else Cxt.getColor(R.color.color_333333))
        setOnClickNotDoubleListener {
            contentView.isSelected = true
            back(dialog, 1)
        }
    }
    contentView.findViewById<ChoiceView>(R.id.choice_view).apply {
        isVisible = isCanClose
        setOnClickNotDoubleListener {
            contentView.isSelected = true
            back(dialog, 0)
        }
    }
    dialog.showDialog(this, DialogData(customView = contentView, canceledOnTouchOutside = canceledOnTouchOutside), back = object : DialogBack {
        override fun handle(state: Int) {
            if (!contentView.isSelected) {
                back(dialog, 2)
            }
        }

    })
    return dialog
}

private var countDownTimer: CountDownTimer? = null

/**
 * 主播收到申请连线的提示弹窗
 */
fun FragmentActivity.showApplyUpstreamHintDialog(lastDialog: NormalDialog?, data: ChickCustomData, autoDismissTime: Long = 0, back: (NormalDialog, Int) -> Unit = { _, _ -> }): NormalDialog {

    val dialog: NormalDialog
    val contentView: View
    if (lastDialog?.dialog?.isShowing == true) {
        dialog = lastDialog
        contentView = dialog.data?.customView
                ?: layoutInflaterKt(R.layout.view_apply_upstream_hint)
    } else {
        dialog = NormalDialogLauncher.newInstance()
        contentView = layoutInflaterKt(R.layout.view_apply_upstream_hint)
        dialog.showDialog(this, DialogData(customView = contentView), back = object : DialogBack {
            override fun handle(state: Int) {
                if (!contentView.isSelected) {
                    back(dialog, 0)
                    countDownTimer?.cancel()
                    countDownTimer = null
                }
            }
        })
    }

    val applyUpstreamHead1 = contentView.findViewById<ImageView>(R.id.apply_upstream_head_1)
    val applyUpstreamHead2 = contentView.findViewById<ImageView>(R.id.apply_upstream_head_2)
    val applyUpstreamHead3 = contentView.findViewById<ImageView>(R.id.apply_upstream_head_3)
    val applyUpstreamHead4 = contentView.findViewById<ImageView>(R.id.apply_upstream_head_4)

    val applyQueue = data.apply?.queue.orEmpty()
    val applyCount = data.apply?.count ?: 0
    val content = data.summary
    if (applyQueue.isNotEmpty()) {
        if (applyQueue.size > 2 && applyCount > 3) {
            applyUpstreamHead1.glideDisplay(R.drawable.default_head_omit)
            applyUpstreamHead2.glideDisplay(applyQueue[0].avatar, applyQueue[0].gender.genderAvatar(applyQueue[0].isPublisher))
            applyUpstreamHead3.glideDisplay(applyQueue[1].avatar, applyQueue[1].gender.genderAvatar(applyQueue[1].isPublisher))
            applyUpstreamHead4.glideDisplay(applyQueue[2].avatar, applyQueue[2].gender.genderAvatar(applyQueue[2].isPublisher))
        } else {
            applyUpstreamHead1?.glideDisplay(applyQueue[0].avatar, applyQueue[0].gender.genderAvatar(applyQueue[0].isPublisher))
            if (applyQueue.size > 1) {
                applyUpstreamHead2.glideDisplay(applyQueue[1].avatar, applyQueue[1].gender.genderAvatar(applyQueue[1].isPublisher))
            }
            if (applyQueue.size > 2) {
                applyUpstreamHead3.glideDisplay(applyQueue[2].avatar, applyQueue[2].gender.genderAvatar(applyQueue[2].isPublisher))
            }
        }
    }
    applyUpstreamHead1.isVisible = applyQueue.isNotEmpty()
    applyUpstreamHead2.isVisible = applyQueue.size > 1
    applyUpstreamHead3.isVisible = applyQueue.size > 2
    applyUpstreamHead4.isVisible = applyQueue.size > 2 && applyCount > 3

    contentView.findViewById<TextView>(R.id.invite_content).apply {
        text = data.content.createSplitSpannable(Color.parseColor("#fc5d00"))
        isVisible = content.isNotEmpty()
    }
    val cancelBtn = contentView.findViewById<TextView>(R.id.cancel_dialog_btn)
    cancelBtn.text = data.leftText

    countDownTimer?.cancel()
    countDownTimer = object : CountDownTimer(autoDismissTime * 1000, 1000) {
        override fun onFinish() {
            contentView.isSelected = true
            countDownTimer?.cancel()
            countDownTimer = null
            back(dialog, 0)
        }

        @SuppressLint("SetTextI18n")
        override fun onTick(millisUntilFinished: Long) {
            cancelBtn.text = "${data.leftText}(${millisUntilFinished / 1000 + 1})"
        }
    }.start()

    val confirmBtn = contentView.findViewById<TextView>(R.id.confirm_dialog_btn)
    confirmBtn.text = data.rightText

    cancelBtn.setOnClickListener {
        contentView.isSelected = true
        countDownTimer?.cancel()
        countDownTimer = null
        back(dialog, 0)
    }
    confirmBtn.setOnClickListener {
        contentView.isSelected = true
        back(dialog, 1)
        countDownTimer?.cancel()
        countDownTimer = null
    }
    return dialog
}

/**
 * 去权限设置界面弹窗
 */
fun FragmentActivity.showGoPermissionSettingDialog(): NormalDialog {
    val dialog = NormalDialogLauncher.newInstance()
    val view = layoutInflaterKt(R.layout.view_go_permission_setting)
    view.setOnClickListener {
        dialog.dismissAllowingStateLoss()
        PermissionCheck.gotoPermissionSetting(this)
    }
    dialog.showDialog(this, DialogData(customView = view), okBack = {})
    return dialog

}

/**
 * 用户余额不足提示弹窗
 */
fun FragmentActivity.showUpstreamResidueDialog(lastDialog: NormalDialog?, isCreator: Boolean, roomStatus: RoomStatus, back: (NormalDialog, Int, Int) -> Unit): NormalDialog {
    val dialog: NormalDialog
    val contentView: View
    if (lastDialog?.dialog?.isShowing == true) {
        dialog = lastDialog
        contentView = dialog.data?.customView
                ?: layoutInflaterKt(R.layout.view_free_upstream_residue)
    } else {
        dialog = NormalDialogLauncher.newInstance()
        contentView = layoutInflaterKt(R.layout.view_free_upstream_residue)
        dialog.showDialog(this, DialogData(customView = contentView, canceledOnTouchOutside = false, backCanceled = false), back = object : DialogBack {
            override fun handle(state: Int) {
                if (!contentView.isSelected) {
                    back(dialog, 0, 0)
                    countDownTimer?.cancel()
                    countDownTimer = null
                }
            }
        })
    }

    contentView.findViewById<ChoiceView>(R.id.back_choice).apply {
        isVisible = roomStatus.alert?.canClose ?: false
        setOnClickListener {
            countDownTimer?.cancel()
            countDownTimer = null
            back(dialog, 0, 0)
        }
    }
    val title = contentView.findViewById<TextView>(R.id.title)
    val splitTitle = roomStatus.alert?.title
    title.text = buildSpannedString {
        appendLink(splitTitle?.text.orEmpty() + " ", splitTitle?.color.parseColor(Cxt.getColor(R.color.color_333333)))
        appendLink(roomStatus.remainDuration.formatTimeToHMS2(), Cxt.getColor(R.color.color_ff8200))
    }

    countDownTimer?.cancel()
    countDownTimer = object : CountDownTimer(roomStatus.remainDuration * 1000L, 1000) {
        override fun onFinish() {
            contentView.isSelected = true
            countDownTimer?.cancel()
            countDownTimer = null
            back(dialog, 0, 0)
        }

        @SuppressLint("SetTextI18n")
        override fun onTick(millisUntilFinished: Long) {
            title.text = buildSpannedString {
                appendLink(splitTitle?.text.orEmpty() + " ", splitTitle?.color.parseColor(Cxt.getColor(R.color.color_333333)))
                appendLink((millisUntilFinished / 1000 + 1).toInt().formatTimeToHMS2(), roomStatus.countdownColor.parseColor(Cxt.getColor(R.color.color_ff8200)))
            }
        }
    }.start()

    contentView.findViewById<TextView>(R.id.sub_title).apply {
        text = roomStatus.alert?.subTitle?.text.orEmpty()
        setTextColor(roomStatus.alert?.subTitle?.color.parseColor(Cxt.getColor(R.color.color_999999)))
    }

    contentView.findViewById<ApplyUpstreamRechargeView>(R.id.recharge_view).apply {
        isVisible = !isCreator
        notifyData(roomStatus.alert?.products.orEmpty(), null, roomStatus.alert?.defaultSelect
                ?: 0, PayFromType.REMINDER_POP) { back, product ->
            if (back == 1) {
                countDownTimer?.cancel()
                countDownTimer = null
                back(dialog, back, product.coinNum)
            }
        }
    }
    return dialog
}

/**
 * 一个按钮的通用弹窗
 */
fun FragmentActivity.showSingleBtnCommonDialog(title: String, content: String = "", hint: String = "", btnText: String = "知道了", canceledOnTouchOutside: Boolean = true, back: (NormalDialog) -> Unit = {}): NormalDialog {
    val dialog = NormalDialogLauncher.newInstance()
    val contentView = layoutInflaterKt(R.layout.view_wait_apply)
    contentView.findViewById<TextView>(R.id.invite_title).text = title
    contentView.findViewById<TextView>(R.id.invite_content).apply {
        text = content
        isVisible = content.isNotEmpty()
    }
    contentView.findViewById<TextView>(R.id.invite_hint).apply {
        text = hint
        isVisible = content.isNotEmpty()
    }

    contentView.findViewById<TextView>(R.id.confirm_btn).apply {
        text = btnText
        setOnClickListener {
            back(dialog)
        }
    }
    dialog.showDialog(this, DialogData(customView = contentView, canceledOnTouchOutside = canceledOnTouchOutside), okBack = {})
    return dialog
}

