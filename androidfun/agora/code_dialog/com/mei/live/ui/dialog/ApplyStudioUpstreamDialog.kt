package com.mei.live.ui.dialog

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.core.text.buildSpannedString
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.joker.PayType
import com.mei.GrowingUtil
import com.mei.dialog.PayFromType
import com.mei.dialog.toPay
import com.mei.live.ext.parseColor
import com.mei.live.views.ReChargeAdapter
import com.mei.orc.Cxt
import com.mei.orc.dialog.MeiSupportDialogFragment
import com.mei.wood.R
import com.mei.wood.extensions.appendLink
import com.net.model.room.QueueMy
import kotlinx.android.synthetic.main.dialog_apply_studio_upstream.*
import kotlinx.android.synthetic.main.layout_enough_apply_studio_bottom.*
import kotlinx.android.synthetic.main.layout_lack_apply_studio_bottom.*

/**
 * Created by hang on 2020/7/28.
 * 申请工作室连麦
 */
fun FragmentActivity.showApplyStudioUpstreamDialog(option: QueueMy.Option, back: (Int) -> Unit) {
    ApplyStudioUpstreamDialog().apply {
        mOption = option
        mBack = back
    }.show(supportFragmentManager, ApplyStudioUpstreamDialog::class.java.simpleName)
}

class ApplyStudioUpstreamDialog : MeiSupportDialogFragment() {
    var mOption: QueueMy.Option? = null
    var mBack: (Int) -> Unit = {}
    var mSelectedPosition = -1

    private val mAdapter by lazy {
        ReChargeAdapter(mOption?.products.orEmpty().toMutableList()).apply {
            setOnItemClickListener { _, _, position ->
                mSelectedPosition = position
                this@ApplyStudioUpstreamDialog.mSelectedPosition = position
                notifyDataSetChanged()
            }
        }
    }

    override fun onSetInflaterLayout() = R.layout.dialog_apply_studio_upstream

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
        initEvent()

        growingTrack("push_popup_view", "")
    }

    private fun initEvent() {
        back_choice.setOnClickListener {
            growingTrack("push_popup_click", "×")
            mBack(0)
            dismissAllowingStateLoss()
        }

        recharge_btn.setOnClickListener {
            growingTrack("push_popup_click", "充值")
            activity?.showApplyUpstreamRechargeDialog(mOption?.products.orEmpty(), mOption?.extra, mOption?.defaultSelect
                    ?: -1, PayFromType.VOICE_UP_APPLY)
        }

        immediate_apply.setOnClickListener {
            growingTrack("push_popup_click", "立即申请")
            mBack(1)
            dismissAllowingStateLoss()
        }

        wechat_pay.setOnClickListener {
            growingTrack("push_popup_click", "微信支付")
            pay(PayType.weixin)
        }

        ali_pay.setOnClickListener {
            growingTrack("push_popup_click", "支付宝支付")
            pay(PayType.alipay)
        }
    }

    private fun initView() {
        title.paint.isFakeBoldText = true
        upstream_residue_hint.paint.isFakeBoldText = true

        immediate_apply.text = mOption?.applyBtnText ?: "立即申请"
        layout_lack_apply_studio_bottom.isVisible = mOption?.needShowRecharge ?: true
        layout_enough_apply_studio_bottom.isGone = mOption?.needShowRecharge ?: true

        recharge_btn.isVisible = mOption?.isFree != true

        title.text = buildSpannedString {
            appendLink(mOption?.title?.text.orEmpty(), Color.WHITE, mOption?.title?.fontScale
                    ?: 0.0f)
        }
        cost_upstream.text = buildSpannedString {
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

        if (mOption?.products.orEmpty().size == 1) {
            recharge_item_container.isVisible = true
            recharge_list.isVisible = false
            mSelectedPosition = 0

            recharge_item.paint.isFakeBoldText = true
            val product = mOption?.products?.getOrNull(0)
            recharge_item.text = buildSpannedString {
                appendLink(product?.minutesText.orEmpty(), Cxt.getColor(R.color.color_333333), 1.6f)
                appendLink("  (${product?.coinNum ?: 0}心币)    ", Cxt.getColor(R.color.color_666666))
                appendLink(product?.costText.orEmpty(), Cxt.getColor(R.color.color_FC5D00), 1.5f)
            }
        } else if (mOption?.products.orEmpty().size > 1) {
            recharge_item_container.isVisible = false
            recharge_list.isVisible = true

            recharge_list.layoutManager = GridLayoutManager(context, 3)
            mSelectedPosition = mOption?.defaultSelect ?: -1
            mAdapter.mSelectedPosition = mSelectedPosition
            recharge_list.adapter = mAdapter
        }
    }

    private fun pay(payType: Int) {
        val product = mOption?.products?.getOrNull(mSelectedPosition)
        if (product != null) {
            activity?.toPay(product, payType, PayFromType.VOICE_UP_APPLY) {
                if (it) {
                    dismissAllowingStateLoss()
                    mBack(1)
                } else {
                    mBack(2)
                }
            }
        }
    }

    private fun growingTrack(event: String, clickType: String) {
        GrowingUtil.track(event, "popup_type", "工作室语音倾诉弹窗",
                "view_type", if (mOption?.needShowRecharge == true) "申请语音倾诉余额不足" else "申请语音倾诉",
                "popup_click_type", clickType,
                "room_type", "工作室大直播间",
                "time_stamp", (System.currentTimeMillis()/ com.mei.orc.unit.TimeUnit.SECOND).toString())
    }
}