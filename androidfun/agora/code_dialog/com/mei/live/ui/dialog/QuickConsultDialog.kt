package com.mei.live.ui.dialog

import android.Manifest
import android.annotation.SuppressLint
import android.os.CountDownTimer
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentActivity
import com.joker.im.custom.chick.ChickCustomData
import com.mei.GrowingUtil
import com.mei.live.action.findHasInviteDialog
import com.mei.live.manager.genderAvatar
import com.mei.orc.ext.layoutInflaterKt
import com.mei.orc.imageload.glide.glideDisplay
import com.mei.orc.rxpermission.requestMulPermissionZip
import com.mei.orc.unit.TimeUnit
import com.mei.orc.util.click.setOnClickNotDoubleListener
import com.mei.wood.R
import com.mei.wood.dialog.DialogBack
import com.mei.wood.dialog.DialogData
import com.mei.wood.dialog.NormalDialog
import com.mei.wood.dialog.NormalDialogLauncher
import com.mei.wood.event.honorMedalMsgQueue
import com.mei.wood.event.screenCurrentDialog
import com.mei.wood.event.startShowDialogTask
import com.mei.wood.extensions.executeKt
import com.mei.wood.ui.MeiCustomBarActivity
import com.net.network.chat.RejectMatchRequest
import kotlin.math.round

/**
 * QuickConsultDialog
 *
 * 快速咨询-抢单弹窗
 *
 * @author kun
 * @version 1.0.0
 * @since 2020-10-13
 */
fun FragmentActivity.showQuickConsultDialog(data: ChickCustomData, back: (dialog: NormalDialog) -> Unit) {
    val dialog = NormalDialogLauncher.newInstance()
    val contentView = layoutInflaterKt(R.layout.dialog_quick_consult)

    contentView.apply {
        findViewById<TextView>(R.id.quick_consult_name).paint.isFakeBoldText = true
        findViewById<TextView>(R.id.quick_consult_snap_up).paint.isFakeBoldText = true
    }

    val nameView = contentView.findViewById<TextView>(R.id.quick_consult_name)
    val avatarView = contentView.findViewById<ImageView>(R.id.quick_consult_avatar)
    val descriptionView = contentView.findViewById<TextView>(R.id.quick_consult_description)
    val refuseView = contentView.findViewById<TextView>(R.id.quick_consult_refuse)
    val snapUpView = contentView.findViewById<TextView>(R.id.quick_consult_snap_up)

    // start 正在使用优惠券显示与隐藏处理
    contentView.findViewById<View>(R.id.invite_now_apply_coupon_ll).apply {
        isVisible = data.couponMessageItem?.rightButtonTipsText?.isNotEmpty() == true
    }
    contentView.findViewById<TextView>(R.id.invite_now_apply_coupon_title).apply {
        text = data.couponMessageItem?.couponName.orEmpty()
    }
    contentView.findViewById<TextView>(R.id.invite_now_apply_coupon_content_hint).apply {
        text = data.couponMessageItem?.rightButtonTipsText.orEmpty()
    }
    // end   正在使用优惠券显示与隐藏处理
    contentView.findViewById<TextView>(R.id.quick_live_content).apply {
        text = data.couponMessageItem?.title.orEmpty()
        isVisible = data.couponMessageItem?.title?.isNotEmpty() == true
    }
    refuseView.setOnClickListener {
        (this as? MeiCustomBarActivity)?.apiSpiceMgr?.executeKt(RejectMatchRequest().apply {
            userId = data.couponMessageItem?.user?.userId ?: 0
        })
        GrowingUtil.track("push_popup_click",
                "popup_type", "快速咨询抢单",
                "popup_click_type", "拒绝")
        dialog.dismiss()
    }

    snapUpView.setOnClickNotDoubleListener(tag = "quick_consult") {
        GrowingUtil.track("push_popup_click",
                "popup_type", "快速咨询抢单",
                "popup_click_type", "立即抢单")
        this@showQuickConsultDialog.requestMulPermissionZip(Manifest.permission.RECORD_AUDIO, Manifest.permission.CAMERA) { has ->
            if (!has) {
                this@showQuickConsultDialog.showGoPermissionSettingDialog()
            } else {
                dialog.dismiss()
                back(dialog)
            }
        }
    }

    data.couponMessageItem?.let { couponMessage ->
        nameView.text = couponMessage.user?.nickName.orEmpty()
        avatarView.glideDisplay(couponMessage.user?.avatar.orEmpty(), couponMessage.user?.gender.genderAvatar())
        descriptionView.text = couponMessage.request
    }
    refuseView.text = data.leftText
    snapUpView.text = data.rightText

    val countDownTimer = object : CountDownTimer(data.timeOut * TimeUnit.SECOND, TimeUnit.SECOND) {
        @SuppressLint("SetTextI18n")
        override fun onTick(millisUntilFinished: Long) {
            if (dialog.isAdded) {
                refuseView.text = "${data.leftText}(${round(millisUntilFinished * 1f / TimeUnit.SECOND).toInt()})"
            }
        }

        override fun onFinish() {
            cancel()
            refuseView.performClick()
        }
    }
    countDownTimer.start()

    dialog.showDialog(this, DialogData(customView = contentView, canceledOnTouchOutside = false, key = "QuickConsultDialog", backCanceled = false), back = object : DialogBack {
        override fun handle(state: Int) {
            countDownTimer.cancel()
            val isShow = screenCurrentDialog("QuickConsultDialog") || !findHasInviteDialog()
            if (honorMedalMsgQueue.isNotEmpty() && isShow) {
                startShowDialogTask()
            }
        }
    })

    GrowingUtil.track("push_popup_view",
            "popup_type", "快速咨询抢单")
}