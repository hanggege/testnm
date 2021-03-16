package com.mei.userinfocomplement.step

import android.graphics.Rect
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mei.orc.ext.dp
import com.mei.orc.ext.dp2px
import com.mei.orc.imageload.glide.glideDisplay
import com.mei.widget.gradient.GradientFrameLayout
import com.mei.widget.gradient.applyViewDelegate
import com.mei.wood.R
import com.mei.wood.extensions.executeToastKt
import com.net.model.room.ProductCategory
import com.net.network.room.NewProductCategoryRequest
import kotlinx.android.synthetic.main.fragment_complement_step_three.*
import kotlinx.android.synthetic.main.include_net_error_layout.*


/**
 *  Created by zzw on 2019-11-27
 *  Des:
 */

class ComplementStepThreeFragment : ComplementBaseFragment() {

    override val step = 3
    override val title = "请选择你感兴趣的内容"
    override val layoutId = R.layout.fragment_complement_step_three
    override fun titleView(): TextView {
        return complement_step_three_title
    }

    private val selList = arrayListOf<Int>()
    private val mData = arrayListOf<ProductCategory.ProductCategoryBean>()
    private val mAdapter by lazy {
        InterestAdapter(mData).apply {
            setOnItemClickListener { _, _, position ->
                val item = mData[position]
                if (selList.contains(item.id)) {
                    selList.remove(item.id)
                } else {
                    selList.add(item.id)
                }
                changeNextViewStyle()
                notifyItemChanged(position)
            }
        }
    }

    var categoryBack: (List<Int>, View) -> Unit = { _, _ -> }

    override fun initView() {
        complement_step_three_next.paint.isFakeBoldText = true
        complement_step_three_next.text = if (complementStep == 3) "进入知心" else "点击下一步"

        interested_recy.adapter = mAdapter
        interested_recy.addItemDecoration(Decoration())
    }

    override fun initListener() {
        net_error_layout.setOnBtnClick { requestData() }
        complement_step_three_next.setOnClickListener {
            if (selList.isNotEmpty()) {
                categoryBack(selList, complement_step_three_next)
            }
        }
    }

    override fun requestData() {
        net_error_layout.isVisible = false
        showLoadingCover()
        apiSpiceMgr.executeToastKt(NewProductCategoryRequest(), success = {
            if (it.isSuccess) {
                mData.clear()
                mData.addAll(it.data?.list.orEmpty())
                mAdapter.notifyDataSetChanged()
            }
            net_error_layout.isGone = mData.isNotEmpty()
        }, failure = {
            net_error_layout.isVisible = true
        }, finish = {
            showLoading(false)
        })
    }

    private fun changeNextViewStyle() {
        val hasSelect = selList.isNotEmpty()
        complement_step_three_next.apply {
            setTextColor(ContextCompat.getColor(context, if (hasSelect) R.color.colorWhite else R.color.color_c8c8c8))
            delegate.applyViewDelegate {
                radius = 27.5f.dp2px
                if (hasSelect) {
                    startColor = ContextCompat.getColor(context, R.color.color_FF7F33)
                    endColor = ContextCompat.getColor(context, R.color.color_FF3F36)
                } else {
                    startColor = ContextCompat.getColor(context, R.color.color_f6f6f6)
                }
            }
        }
    }

    inner class InterestAdapter(list: MutableList<ProductCategory.ProductCategoryBean>)
        : BaseQuickAdapter<ProductCategory.ProductCategoryBean, BaseViewHolder>(R.layout.item_interest_complement, list) {
        override fun convert(holder: BaseViewHolder, item: ProductCategory.ProductCategoryBean) {
            val isSelected = selList.contains(item.id)
            holder.getView<GradientFrameLayout>(R.id.item_interest).delegate.applyViewDelegate {
                radius = 3.dp
                if (isSelected) {
                    startColor = ContextCompat.getColor(context, R.color.color_FD955A)
                    endColor = ContextCompat.getColor(context, R.color.color_FD645D)
                } else {
                    startColor = ContextCompat.getColor(context, R.color.color_F8F4F8)
                }
            }
            holder.getView<ImageView>(R.id.item_interest_icon).glideDisplay(if (isSelected) R.drawable.icon_complement_interest_selected else item.icon)
            holder.getView<TextView>(R.id.item_interest_category).apply {
                text = item.name
                setTextColor(ContextCompat.getColor(context, if (isSelected) R.color.colorWhite else R.color.color_c8c8c8))
            }
        }
    }

    inner class Decoration : RecyclerView.ItemDecoration() {
        private val margin15dpHalf = 7.5f.dp2px.toInt()
        private val margin15dp = 15.dp.toInt()
        override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
            val position = (view.layoutParams as RecyclerView.LayoutParams).viewLayoutPosition
            if (position > 1) outRect.top = margin15dp
            if (position % 2 == 0) outRect.right = margin15dpHalf
            else outRect.left = margin15dpHalf
        }
    }
}
