package com.mei.live.ui.dialog

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.text.buildSpannedString
import androidx.core.view.isGone
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentActivity
import com.mei.GrowingUtil
import com.mei.dialog.PayFromType
import com.mei.live.ext.parseColor
import com.mei.orc.Cxt
import com.mei.orc.dialog.MeiSupportDialogFragment
import com.mei.orc.imageload.glide.glideDisplay
import com.mei.wood.R
import com.mei.wood.extensions.appendLink
import com.net.model.room.QueueMy
import kotlinx.android.synthetic.main.dialog_apply_private_upstream.*
import kotlinx.android.synthetic.main.layout_enough_apply_private_upstream_bottom.*
import kotlinx.android.synthetic.main.layout_lack_apply_private_upstream_bottom.*

/**
 * Created by hang on 2020/6/15.
 * 申请私密连线弹窗
 */
fun FragmentActivity.showApplyPrivateUpstreamDialog(option: QueueMy.Option, fromType: PayFromType, back: (Int, Boolean) -> Unit) {
    ApplyPrivateUpstreamDialog().apply {
        mOption = option
        mFromType = fromType
        mBack = back
    }.show(supportFragmentManager, ApplyPrivateUpstreamDialog::class.java.simpleName)
}

class ApplyPrivateUpstreamDialog : MeiSupportDialogFragment() {

    var mOption: QueueMy.Option? = null
    var mBack: (Int, Boolean) -> Unit = { _, _ -> }
    var mFromType: PayFromType = PayFromType.UP_APPLY


    override fun onSetInflaterLayout(): Int = R.layout.dialog_apply_private_upstream

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
        initEvent()

        growingTrack("push_popup_view", "")
    }

    private fun isCloseVideo() = if (isAdded) {
        if (mOption?.needShowRecharge == true) {
            not_hint_check.isSelected
        } else {
            enough_not_hint_check.isSelected
        }
    } else {
        false
    }


    private fun initEvent() {
        back_choice.setOnClickListener {
            growingTrack("push_popup_click", "×")
            mBack(0, isCloseVideo())
            dismissAllowingStateLoss()
        }

        recharge_btn.setOnClickListener {
            growingTrack("push_popup_click", "充值")
            activity?.showApplyUpstreamRechargeDialog(mOption?.products.orEmpty(), mOption?.extra, mOption?.defaultSelect
                    ?: -1, mFromType)
        }

        immediate_apply.setOnClickListener {
            growingTrack("push_popup_click", "立即申请")
            mBack(1, isCloseVideo())
            dismissAllowingStateLoss()
        }

        not_hint.setOnClickListener {
            not_hint_check.isSelected = !not_hint_check.isSelected
            not_hint_text.apply {
                isSelected = !isSelected
                if (isSelected) {
                    setTextColor(ContextCompat.getColor(context, R.color.color_999999))
                } else {
                    setTextColor(ContextCompat.getColor(context, R.color.color_c8c8c8))
                }
            }
        }
        enough_not_hint.setOnClickListener {
            enough_not_hint_check.isSelected = !enough_not_hint_check.isSelected
            enough_not_hint_text.apply {
                isSelected = !isSelected
                if (isSelected) {
                    setTextColor(ContextCompat.getColor(context, R.color.color_999999))
                } else {
                    setTextColor(ContextCompat.getColor(context, R.color.color_c8c8c8))
                }
            }
        }
    }

    private fun initView() {
        dialog?.setCanceledOnTouchOutside(false)

        icon_privacy_protect.glideDisplay(mOption?.protectIcon.orEmpty())

        immediate_apply.text = mOption?.applyBtnText ?: "立即申请"

        title.paint.isFakeBoldText = true
        upstream_residue_hint.paint.isFakeBoldText = true

        lack_apply_private_upstream_layout.isVisible = mOption?.needShowRecharge ?: true
        enough_apply_private_upstream_layout.isGone = mOption?.needShowRecharge ?: true

        recharge_btn.isVisible = mOption?.isFree != true

        not_hint.isInvisible = mOption?.supportAudioChat == false
        enough_not_hint.isInvisible = mOption?.supportAudioChat == false

        refreshNowApplyCoupon()

        title.text = buildSpannedString {
            appendLink(mOption?.title?.text.orEmpty(), Color.WHITE, mOption?.title?.fontScale
                    ?: 0.0f)
        }
        cost_private_upstream.text = buildSpannedString {
            appendLink(mOption?.subTitle?.text.orEmpty(), Color.WHITE, mOption?.subTitle?.fontScale
                    ?: 0.0f)
        }

        upstream_residue_hint.text = buildSpannedString {
            mOption?.tips.orEmpty().forEach {
                appendLink(it.text, it.color.parseColor(Cxt.getColor(R.color.color_ff8200)), it.fontScale)
            }
        }
        enough_upstream_residue_hint.text = buildSpannedString {
            mOption?.tips.orEmpty().forEach {
                appendLink(it.text, it.color.parseColor(Cxt.getColor(R.color.color_ff8200)), it.fontScale)
            }
        }


        residue_money.text = buildSpannedString {
            appendLink(mOption?.userBalanceText.orEmpty(), Cxt.getColor(R.color.color_999999))
            appendLink(mOption?.balance.toString(), Color.BLACK)
        }
        enough_residue_money.text = buildSpannedString {
            appendLink(mOption?.userBalanceText.orEmpty(), Cxt.getColor(R.color.color_999999))
            appendLink(mOption?.balance.toString(), Color.BLACK)
        }

        apply_upstream_recharge_view.notifyData(mOption?.products.orEmpty(), mOption?.extra, mOption?.defaultSelect
                ?: -1, mFromType) { back, _ ->
            mBack(back, isCloseVideo())
            if (back == 1) {
                dismissAllowingStateLoss()
            }
        }
    }

    private fun refreshNowApplyCoupon() {
        val extra = mOption?.extra
        enough_upstream_residue_hint.isVisible = extra == null
        now_apply_coupon_enough.isVisible = extra != null
        now_apply_coupon_prefix_tv_enough.text = extra?.prefix.orEmpty()
        now_apply_coupon_title_enough.text = extra?.name.orEmpty()
        now_apply_coupon_content_hint_enough.text = extra?.desc.orEmpty()
    }

    private fun growingTrack(event: String, clickType: String) {
        GrowingUtil.track(event, "popup_type", "用户端申请上麦", "popup_click_type", clickType, "room_type", "专属房",
                "time_stamp", (System.currentTimeMillis() / com.mei.orc.unit.TimeUnit.SECOND).toString())
    }
}

