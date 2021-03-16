package com.mei.live.views

import android.content.Context
import android.content.ContextWrapper
import android.graphics.Paint
import android.util.AttributeSet
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.flyco.roundview.RoundLinearLayout
import com.joker.PayType
import com.joker.im.custom.chick.Extra
import com.mei.dialog.PayFromType
import com.mei.dialog.toPay
import com.mei.orc.ext.dip
import com.mei.orc.ext.layoutInflaterKtToParentAttach
import com.mei.orc.ext.screenWidth
import com.mei.wood.R
import com.net.model.chick.pay.ProductBean
import kotlinx.android.synthetic.main.view_apply_upstream_recharge.view.*

/**
 * Created by hang on 2020/6/15.
 */
class ApplyUpstreamRechargeView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
        LinearLayout(context, attrs, defStyleAttr) {

    private var mSelectedPosition = -1

    private var mProducts = arrayListOf<ProductBean>()

    private var mPayFromType: PayFromType = PayFromType.BROADCAST

    var mBack: (Int, ProductBean) -> Unit = { _, _ -> }

    private val mAdapter by lazy {
        ReChargeAdapter(mProducts).apply {
            setOnItemClickListener { _, _, position ->
                mSelectedPosition = position
                this@ApplyUpstreamRechargeView.mSelectedPosition = position
                notifyDataSetChanged()
            }
        }
    }

    init {
        layoutInflaterKtToParentAttach(R.layout.view_apply_upstream_recharge)

        recharge_list.layoutManager = GridLayoutManager(context, 3)
        recharge_list.adapter = mAdapter

        initEvent()

        recharge_list.post {
            recharge_list.updateLayoutParams {
                height = if (measuredWidth == screenWidth) {
                    (measuredWidth - dip(30)) / 3
                } else {
                    (measuredWidth - dip(30)) * 36 / 95
                }
            }
        }
    }

    private fun initEvent() {
        wechat_pay.setOnClickListener {
            pay(PayType.weixin)
        }

        ali_pay.setOnClickListener {
            pay(PayType.alipay)
        }
    }

    private fun pay(payType: Int) {
        val product = mProducts.getOrNull(mSelectedPosition)
        if (product != null) {
            ((context as? FragmentActivity)
                    ?: ((context as? ContextWrapper)?.baseContext as? FragmentActivity))?.let { activity ->
                activity.toPay(product, payType, mPayFromType) {
                    mBack(if (it) 1 else 2, product)
                }
            }
        }
    }

    fun notifyData(products: List<ProductBean>, extra: Extra? = null, selectPosition: Int, payFromType: PayFromType, back: (Int, ProductBean) -> Unit) {
        mProducts.clear()
        mProducts.addAll(products)
        mBack = back
        mPayFromType = payFromType
        /**
         * 只有第一次刷新选中位置
         */
        if (mSelectedPosition < 0) {
            mSelectedPosition = selectPosition
            mAdapter.mSelectedPosition = selectPosition
        }
        refreshNowApplyCoupon(extra)
        mAdapter.notifyDataSetChanged()
    }

    private fun refreshNowApplyCoupon(extra: Extra?) {
        now_apply_coupon_ll.isVisible = extra != null
        now_apply_coupon_prefix_tv.text = extra?.prefix.orEmpty()
        now_apply_coupon_title.text = extra?.name.orEmpty()
        now_apply_coupon_content_hint.text = extra?.desc.orEmpty()
    }

}

class ReChargeAdapter(list: MutableList<ProductBean>) : BaseQuickAdapter<ProductBean, BaseViewHolder>(R.layout.item_upstream_recharge, list) {

    var mSelectedPosition = -1

    override fun convert(holder: BaseViewHolder, item: ProductBean) {
        val isSelected = holder.layoutPosition == mSelectedPosition
        holder.getView<RoundLinearLayout>(R.id.bg_frame_selector).apply {
            this.isSelected = isSelected
            delegate.apply {
                if (isSelected) {
                    strokeWidth = 1
                    strokeColor = ContextCompat.getColor(context, R.color.color_ff8200)
                    backgroundColor = ContextCompat.getColor(context, R.color.color_FFF7EE)
                } else {
                    strokeWidth = 0
                    backgroundColor = ContextCompat.getColor(context, R.color.color_f8f8f8)
                }
            }
        }
        holder.getView<TextView>(R.id.recommend_badge).isSelected = isSelected
        holder.getView<TextView>(R.id.preferential_money).paint.flags = Paint.STRIKE_THRU_TEXT_FLAG
        holder.getView<TextView>(R.id.selected_recharge_time).paint.isFakeBoldText = true

        holder.getView<ImageView>(R.id.icon_badge).isSelected = isSelected

        holder.setGone(R.id.unSelected_recharge_time, isSelected)
                .setGone(R.id.selected_recharge_time, !isSelected)
                .setGone(R.id.expend_currency, !isSelected)
                .setGone(R.id.expend_money_layout, !isSelected)
                .setGone(R.id.recommend_badge, item.copywriting.isNullOrEmpty())
                .setGone(R.id.preferential_money, item.cost == item.crossedPrice || item.crossedPrice == 0)
                .setText(R.id.recommend_badge, item.copywriting.orEmpty())
                .setText(R.id.unSelected_recharge_time, item.minutesText)
                .setText(R.id.selected_recharge_time, item.minutesText)
                .setText(R.id.expend_currency, "${item.coinNum}心币")
                .setText(R.id.expend_money, "¥${item.cost}")
                .setText(R.id.preferential_money, "￥${item.crossedPrice}")
    }

}
