package com.mei.chat.ui.view


import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import com.mei.orc.ext.dip
import com.mei.orc.ext.screenHeight
import com.mei.orc.ext.screenWidth
import com.mei.wood.R


/**
 *  Created by zzw on 2019/3/14
 *  Des: 消息item长按  选项气泡
 */

fun View.showBubblePopWindow(data: List<String>,
                             onItemClick: (pos: Int) -> Boolean = { _ -> true }) {
    if (data.isEmpty()) return

    val popupWindow = PopupWindow(context).apply {
        width = WRAP_CONTENT
        height = WRAP_CONTENT
        isFocusable = true
        this.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        contentView = contentView(
                context,
                data,
                onItemClick = {
                    val isDismiss = onItemClick(it)
                    if (isDismiss) dismiss()
                })
    }

    popupWindow.contentView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED).let {
        //必须contentView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)才能获取宽和高
        val popWidth = popupWindow.contentView.measuredWidth
        val popHeight = popupWindow.contentView.measuredHeight

        val location = IntArray(2)
        getLocationOnScreen(location)

        var x = location[0] + width - dip(17) - popWidth
        var y = location[1] + dip(5) - popHeight

        if (x < 0) {
            x = 0
        }
        if (x + popWidth > screenWidth) {
            x = screenWidth - popWidth
        }

        if (y < 0) {
            y = 0
        }
        if (y + popHeight > screenHeight) {
            y = screenHeight - popHeight
        }

        popupWindow.showAtLocation(this, Gravity.NO_GRAVITY, x, y)
    }

}


@SuppressLint("InflateParams")
private fun contentView(ctx: Context, data: List<String>, onItemClick: (pos: Int) -> Unit = { _ -> }): View {
    val contentView = LayoutInflater.from(ctx).inflate(R.layout.im_bubble_pop_layout, null, false)
    contentView.findViewById<LinearLayout>(R.id.im_bubble_pop_ll).apply {
        for ((index, value) in data.withIndex()) {
            //添加线
            if (index > 0) {
                this.addView(View(context).apply {
                    layoutParams = ViewGroup.LayoutParams(1, MATCH_PARENT)
                    setBackgroundColor(Color.WHITE)
                })
            }

            //添加item
            this.addView(itemView(context, value).apply {
                setOnClickListener {
                    onItemClick(index)
                }
            })
        }
    }
    return contentView
}

private fun itemView(context: Context, s: String): View {
    return TextView(context).apply {
        layoutParams = ViewGroup.LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
        setPadding(dip(15), dip(7), dip(15), dip(7))
        gravity = Gravity.CENTER_VERTICAL
        textSize = 12.0f
        setTextColor(Color.WHITE)
        text = s
    }
}



