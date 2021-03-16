package com.mei.im.ui.view


import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.PopupWindow
import android.widget.TextView
import com.mei.orc.ext.getIndexOrNull
import com.mei.orc.ext.screenHeight
import com.mei.orc.ext.screenWidth
import com.mei.wood.R


/**
 *  Created by zzw on 2019/3/14
 *  Des: 消息item长按  选项气泡
 */

fun View.showIMMenuBubblePopWindow(itemList: List<String> = arrayListOf("删除", "复制"),
                                   offsetY: Int = 0,//y轴偏移量
                                   maxWidth: Int = screenWidth,
                                   maxHeight: Int = screenHeight,
                                   onItemClick: (pos: Int) -> Unit = { }) {
    if (itemList.isEmpty()) return

    val popupWindow = PopupWindow(context).apply {
        width = WRAP_CONTENT
        height = WRAP_CONTENT
        isFocusable = true
        this.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        contentView = contentView(context, itemList) {
            this.dismiss()
            onItemClick(it)
        }
    }

    popupWindow.contentView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED).let {
        //必须contentView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)才能获取宽和高
        val popWidth = popupWindow.contentView.measuredWidth
        val popHeight = popupWindow.contentView.measuredHeight

        val location = IntArray(2)
        getLocationInWindow(location)
        var x = location[0] + (width - popWidth) / 2
        var y = location[1] + height + offsetY

        if (x < 0) {
            x = 0
        }
        if (x + popWidth > maxWidth) {
            x = maxWidth - popWidth
        }

        if (y < 0) {
            y = 0
        }
        if (y + popHeight > maxHeight) {
            y = maxHeight - popHeight
        }

        popupWindow.showAtLocation(this, Gravity.NO_GRAVITY, x, y)
    }

}


@SuppressLint("InflateParams")
private fun contentView(ctx: Context, itemList: List<String>, onItemClick: (pos: Int) -> Unit = { }): View {
    val id = if (itemList.size == 1) R.layout.im_menu_bubble_pop_layout_1 else R.layout.im_menu_bubble_pop_layout_2
    val contentView = LayoutInflater.from(ctx).inflate(id, null, false)
    val views = arrayListOf(R.id.im_menu_delete_tv, R.id.im_menu_copy_tv).mapNotNull { contentView.findViewById(it) as? TextView }
    itemList.forEachIndexed { index, string ->
        views.getIndexOrNull(index)?.apply {
            text = string
            setOnClickListener { onItemClick(index) }
        }
    }
    return contentView
}




