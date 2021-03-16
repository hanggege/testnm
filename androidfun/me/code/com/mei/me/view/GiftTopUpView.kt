package com.mei.me.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.text.buildSpannedString
import androidx.core.view.isVisible
import androidx.recyclerview.widget.GridLayoutManager
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mei.base.weight.relative.RoundConstraintLayout
import com.mei.orc.ext.layoutInflaterKtToParent
import com.mei.wood.R
import com.mei.wood.extensions.appendLink
import com.net.model.chick.pay.ProductBean
import com.net.model.chick.pay.ProductListResult
import kotlinx.android.synthetic.main.item_gift_header.view.*
import kotlinx.android.synthetic.main.view_gift_top_up.view.*

/**
 * Created by hang on 2019-12-02.
 * 充值礼物模块
 */

class GiftTopUpView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
        FrameLayout(context, attrs, defStyleAttr) {

    /**
     * -1 表示首冲 -2 不选中
     */
    private var mSelectedIndex = 0

    private val mList = arrayListOf<ProductBean>()
    private val mAdapter by lazy {
        GiftTopUpAdapter(mList).apply {
            setOnItemClickListener { _, _, position ->
                mSelectedIndex = position
                mHeaderView.bg_selected_state_concession.isVisible = false
                notifyDataSetChanged()
            }
        }
    }

    private val mHeaderView by lazy {
        rose_recycler_view.layoutInflaterKtToParent(R.layout.item_gift_header)
    }

    private val mFooterView by lazy {
        rose_recycler_view.layoutInflaterKtToParent(R.layout.item_gift_footer)
    }

    init {
        LayoutInflater.from(context).inflate(R.layout.view_gift_top_up, this)
        rose_recycler_view.layoutManager = GridLayoutManager(context, 2)
        rose_recycler_view.adapter = mAdapter

        mHeaderView.setOnClickListener {
            mSelectedIndex = -1
            mHeaderView.bg_selected_state_concession.isVisible = true
            mAdapter.notifyDataSetChanged()
        }
    }

    fun getSelectedGiftInfo(): ProductBean? {
        return if (mSelectedIndex == -1) {
            return depositProduct
        } else if (mSelectedIndex > -1 && mSelectedIndex < mList.size)
            mList[mSelectedIndex]
        else null
    }

    //首冲
    private var depositProduct: ProductBean? = null

    @SuppressLint("SetTextI18n")
    fun setData(data: ProductListResult) {
        if (data.firstProducts.orEmpty().isNotEmpty()) {//还未充值过
            depositProduct = data.firstProducts[0]
            if (depositProduct?.defaultSelect == true) {
                mSelectedIndex = -1
                mHeaderView.bg_selected_state_concession.isVisible = true
            }
            mAdapter.setHeaderView(mHeaderView)
            mHeaderView.concession_text.text = buildSpannedString {
                appendLink(" ${depositProduct?.cost}元 ", Color.parseColor("#333333"), 1.5f)
                append("购买")
                appendLink(" ${depositProduct?.depositCoin} ", Color.parseColor("#333333"), 1.5f)
                append("心币")
            }
            mHeaderView.first_top_up_badge.text = depositProduct?.copywriting.orEmpty()
            mHeaderView.first_top_up_badge.isVisible = !depositProduct?.copywriting.isNullOrEmpty()
        } else {
            //已经充值过
            mAdapter.removeHeaderView(mHeaderView)
            mHeaderView.bg_selected_state_concession.isVisible = false
        }

        if (mSelectedIndex != -1) {
            data.products?.forEachIndexed { index, productBean ->
                if (productBean.defaultSelect) {
                    mSelectedIndex = index
                    return@forEachIndexed
                }
            }
        }

        mList.clear()
        mList.addAll(data.products.orEmpty())
        mAdapter.notifyDataSetChanged()
        if (data.coinUseText != null) {
            mAdapter.setFooterView(footerView(data.coinUseText))
        }
    }

    private fun footerView(text: String): View {
        return mFooterView.apply {
            findViewById<TextView>(R.id.gif_footer_text).text = text
        }
    }

    inner class GiftTopUpAdapter(list: MutableList<ProductBean>) :
            BaseQuickAdapter<ProductBean, BaseViewHolder>(R.layout.item_gift_top_up, list) {
        override fun convert(holder: BaseViewHolder, item: ProductBean) {
            val roseCount = holder.getView<TextView>(R.id.rose_count)
            roseCount.paint.isFakeBoldText = true

            val curPosition = if (hasHeaderLayout()) holder.layoutPosition - 1 else holder.layoutPosition

            val isSelected = mSelectedIndex == curPosition
            holder.getView<TextView>(R.id.rest_time).isVisible = isSelected
            holder.getView<RoundConstraintLayout>(R.id.bg_frame_selector).apply {
                this.isSelected = isSelected
                delegate.strokeColor = ContextCompat.getColor(context, if (isSelected) R.color.color_ff8b07 else R.color.color_c8c8c8)
                delegate.backgroundColor = ContextCompat.getColor(context, if (isSelected) R.color.color_FFFAF4 else android.R.color.transparent)
            }

            holder.setText(R.id.rose_count, item.depositCoin.toString())
                    .setText(R.id.expend_money, "${item.cost}元")
                    .setText(R.id.recommend_badge, item.copywriting)
                    .setGone(R.id.recommend_badge, item.copywriting.isNullOrEmpty())
                    .setText(R.id.plus_count, "+${item.depositGive}")
                    .setText(R.id.rest_time, item.upTimeText)
                    .setVisible(R.id.plus_count, item.depositGive > 0)
        }
    }


}