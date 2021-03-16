package com.mei.orc.dialog

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.StateListDrawable
import android.os.Bundle
import android.view.*
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.annotation.IntRange
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mei.orc.callback.Callback
import com.mei.orc.ext.dip
import java.util.*

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2018/6/5
 *
 * 通用底部弹框
 */


var defaultColor = Color.parseColor("#303030")
var blue = Color.parseColor("#037BFF")
var darkGray = Color.parseColor("#333333")

private fun Context.dip(value: Int): Int = (value * resources.displayMetrics.density).toInt()

class BottomSelectFragment : BottomDialogFragment() {
    private var callBack: Callback<Item>? = null
    private var canceledOnTouchOutside: Boolean = true
    private var headerTitle: String = ""
    private var headerTextSize: Float = 12f
    private var headerTextColor: Int = Color.parseColor("#8F8F8F")
    private var headerBgColor: Int = Color.WHITE
    private var headerHeight: Int = dip(40)

    private var footerTitle: String = "取消"
    private var footerTextSize: Float = 12f
    private var footerTextColor: Int = Color.parseColor("#666666")
    private var footerBgColor: Int = Color.WHITE
    private var footerHeight: Int = dip(57)


    private var bgColor: Drawable = ColorDrawable(Color.TRANSPARENT)

    private val itemList = LinkedList<Item>()
    private val dataAdapter: DataAdapter by lazy {
        val temp = DataAdapter(itemList)
        temp.setOnItemClickListener { _, _, position ->
            dismissAllowingStateLoss()
            callBack?.onCallback(temp.getItem(position))
        }
        temp
    }
    private val containerView: LinearLayout by lazy {
        val temp = LinearLayout(activity)
        temp.apply {
            gravity = Gravity.BOTTOM
            orientation = LinearLayout.VERTICAL
            background = bgColor

            /**
             * 添加头部view
             */

            if (headerTitle.isNotEmpty()) {
                val header = context.getItemView(headerTextSize, ColorDrawable(headerBgColor), headerTextColor, headerHeight)
                header.findViewById<TextView>(android.R.id.text1).text = headerTitle

                val headerParams = LinearLayout.LayoutParams(resources.displayMetrics.widthPixels, LinearLayout.LayoutParams.WRAP_CONTENT)
                headerParams.setMargins(0, 0, 0, context.dip(1))
                addView(header, headerParams)
            }

            val params = LinearLayout.LayoutParams(resources.displayMetrics.widthPixels, LinearLayout.LayoutParams.WRAP_CONTENT)
            recyclerView?.let { addView(it, params) }


            /**
             * 添加取消view
             */
            val footer = context.getItemView(footerTextSize, ColorDrawable(footerBgColor), footerTextColor, footerHeight)
            footer.findViewById<TextView>(android.R.id.text1).text = footerTitle
            footer.setOnClickListener { dismissAllowingStateLoss() }

            val footerParams = LinearLayout.LayoutParams(resources.displayMetrics.widthPixels, LinearLayout.LayoutParams.WRAP_CONTENT)
            footerParams.setMargins(0, context.dip(5), 0, 0)
            addView(footer, footerParams)
        }
        temp
    }
    private val recyclerView: RecyclerView? by lazy {
        val temp = activity?.let {
            RecyclerView(it).apply {
                layoutManager = LinearLayoutManager(context)
                adapter = dataAdapter
                overScrollMode = View.OVER_SCROLL_NEVER
            }
        }
        temp
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        return containerView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (itemList.size > 6) {
            recyclerView?.layoutParams = LinearLayout.LayoutParams(resources.displayMetrics.widthPixels, (context?.dip(58)
                    ?: 0) * 7)
        }
    }

    /**
     * 添加头部
     */
    fun addHeader(title: String): BottomSelectFragment {
        headerTitle = title
        return this
    }

    fun addHeader(title: String, textSize: Float, textColor: Int, height: Int, bgColor: Int = Color.WHITE): BottomSelectFragment {
        headerTitle = title
        headerTextSize = textSize
        headerTextColor = textColor
        headerHeight = height
        headerBgColor = bgColor
        return this
    }


    fun setFooter(title: String = "取消",
                  textSize: Float = 12.0f,
                  textColor: Int = Color.parseColor("#666666"),
                  height: Int = dip(57),
                  bgColor: Int = Color.WHITE): BottomSelectFragment {
        footerTitle = title
        footerTextSize = textSize
        footerTextColor = textColor
        footerBgColor = bgColor
        footerHeight = height
        return this
    }


    /**
     * 添加条目
     */
    fun addItem(item: Item): BottomSelectFragment {
        itemList.add(item)
        return this
    }

    fun addItemIf(item: Item, condition: Boolean): BottomSelectFragment {
        if (condition)
            itemList.add(item)
        return this
    }

    fun addItem(item: String): BottomSelectFragment {
        itemList.add(Item(itemList.size, item))
        return this
    }

    fun addItemIf(item: String, condition: Boolean): BottomSelectFragment {
        if (condition)
            itemList.add(Item(itemList.size, item))
        return this
    }

    /**
     * 批量添加
     */
    fun <T> addItemList(list: List<T>, transform: (Int, T) -> Item = { i, t -> Item(i, t.toString(), defaultColor) }): BottomSelectFragment {
        list.forEachIndexed { index, t -> itemList.add(transform(index, t)) }
        return this
    }

    /**
     * 批量添加
     */
    fun <T> addItemList(vararg data: T, transform: (Int, T) -> Item = { i, t -> Item(i, t.toString(), defaultColor) }): BottomSelectFragment {
        data.forEachIndexed { index, t -> itemList.add(transform(index, t)) }
        return this
    }

    /**
     * 点击其它地方是否隐藏
     */
    fun setCanceledOnTouchOutside(cancel: Boolean): BottomSelectFragment {
        canceledOnTouchOutside = cancel
        return this
    }

    /**
     * 点击回调
     */
    fun setItemClickListener(back: Callback<Item>): BottomSelectFragment {
        callBack = back
        return this
    }

    /**
     * 设置背景色
     */
    fun setBgColor(bgColor: Drawable): BottomSelectFragment {
        this.bgColor = bgColor
        return this
    }


    fun show(manager: FragmentManager?) {
        manager?.let { show(manager, BottomSelectFragment::class.java.simpleName) }
    }


    override fun show(manager: FragmentManager, tag: String?) {
        try {
            super.show(manager, tag)
            dataAdapter.notifyDataSetChanged()
            dialog?.setCanceledOnTouchOutside(canceledOnTouchOutside)
        } catch (e: Exception) {
        }
    }

}


data class Item(
        @IntRange(from = 0) var type: Int,
        var display: String,
        @ColorInt var textColor: Int = defaultColor,
        var textSize: Float = 16.0f,//sp
        @ColorInt var bgColor: Int = Color.parseColor("#F8F8F8"),
        var selector: Drawable = getSelector()
) {

}

class DataAdapter(var list: MutableList<Item>) : BaseQuickAdapter<Item, BaseViewHolder>(0, list) {


    override fun onCreateDefViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return BaseViewHolder(parent.context.getItemView(16f, getSelector()))
    }

    override fun convert(holder: BaseViewHolder, item: Item) {
        holder.itemView.setBackgroundColor(item.bgColor)

        holder.getView<TextView>(android.R.id.text1).apply {
            setTextColor(item.textColor)
            textSize = item.textSize
            background = item.selector
            text = item.display
        }


        holder.itemView.setPadding(0, 0, 0,
                if (holder.bindingAdapterPosition < itemCount - 1) holder.itemView.context.dip(1) else 0)
    }

}

private fun Context.getItemView(textSize: Float, bg: Drawable, @ColorInt color: Int = defaultColor, height: Int = dip(57)): LinearLayout {
    val container = LinearLayout(this)
    container.background = bg
    container.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    val text = TextView(this)
    text.id = android.R.id.text1
    text.textSize = textSize
    text.gravity = Gravity.CENTER
    text.setTextColor(color)
    container.addView(text, LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, height))
    return container
}

private fun getSelector(): StateListDrawable {
    val drawable = StateListDrawable()
    drawable.addState(intArrayOf(android.R.attr.state_pressed), ColorDrawable(Color.parseColor("#F8F8F8")))
    drawable.addState(intArrayOf(), ColorDrawable(Color.WHITE))
    return drawable
}
