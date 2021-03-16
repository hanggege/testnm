package com.mei.me.activity

import android.app.Activity
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mei.orc.ext.dp
import com.mei.orc.ext.dp2px
import com.mei.orc.imageload.glide.glideDisplay
import com.mei.orc.ui.toast.UIToast
import com.mei.widget.gradient.GradientFrameLayout
import com.mei.widget.gradient.applyViewDelegate
import com.mei.wood.R
import com.mei.wood.extensions.executeToastKt
import com.mei.wood.ui.MeiCustomActivity
import com.net.model.room.ProductCategory
import com.net.network.room.NewProductCategoryRequest
import kotlinx.android.synthetic.main.activity_interest_select.*

/**
 * author : Song Jian
 * date   : 2020/1/18
 * desc   : 兴趣选择
 */
const val INTEREST_INFO_KEY = "interest_info_key"

class InterestSelectActivity : MeiCustomActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_interest_select)
        initView()
        initListener()
        showLoadingCover()
        requestData()
    }


    private var selMap = HashMap<Int, String>()


    private val mData by lazy {
        arrayListOf<ProductCategory.ProductCategoryBean>()
    }
    private val mAdapter by lazy {
        InterestAdapter(mData).apply {
            setOnItemClickListener { _, _, position ->
                val item = mData[position]
                if (selMap.keys.contains(item.id)) {
                    selMap.keys.remove(item.id)
                } else {
                    selMap[item.id] = item.name
                }
                changeButtonStyle()
                notifyItemChanged(position)
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun initView() {
        (intent.getSerializableExtra("sel") as? HashMap<Int, String>)?.let {
            selMap = it
        }
        interested_recy.layoutManager = GridLayoutManager(this, 2)
        interested_recy.addItemDecoration(Decoration())
        interested_recy.adapter = mAdapter

    }

    private fun initListener() {
        black_list_back.setOnClickListener { finish() }
        three_next.setOnClickListener {
            submit()
        }
    }

    private fun requestData() {
        showLoadingCover()
        apiSpiceMgr.executeToastKt(NewProductCategoryRequest(), success = {
            if (it.isSuccess) {
                it.data?.list.orEmpty().let { list ->
                    mData.clear()
                    mData.addAll(list)
                    mAdapter.notifyDataSetChanged()
                }
            }
        }, finish = {
            showLoading(false)
        })
    }

    private fun submit() {

        if (selMap.isEmpty()) {
            UIToast.toast("请选择你感兴趣的内容")
            return
        }
        setResult(Activity.RESULT_OK, Intent().apply {
            putExtra(INTEREST_INFO_KEY, selMap)
        })
        finish()
    }

    private fun changeButtonStyle() {
        val hasSelect = selMap.isNotEmpty()
        three_next.apply {
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

    /**
     * 兴趣选择器
     */
    inner class InterestAdapter(list: MutableList<ProductCategory.ProductCategoryBean>) : BaseQuickAdapter<ProductCategory.ProductCategoryBean, BaseViewHolder>(R.layout.item_interest_complement, list) {
        override fun convert(holder: BaseViewHolder, item: ProductCategory.ProductCategoryBean) {
            val isSelected = selMap.keys.contains(item.id)
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