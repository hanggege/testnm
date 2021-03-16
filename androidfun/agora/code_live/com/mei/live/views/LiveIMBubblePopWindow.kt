package com.mei.live.views


import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.ImageView
import android.widget.PopupWindow
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.mei.orc.ext.dip
import com.mei.wood.R


/**
 *  Created by zzw on 2019/3/14
 *  Des: 消息item长按  选项气泡
 */

fun View.showLiveIMBubblePopWindow(isShowDelete: Boolean, isShielding: Boolean, onItemClick: (pos: Int) -> Unit = { }) {
    if (!isShowDelete && !isShielding) return

    val popupWindow = PopupWindow(context).apply {
        width = WRAP_CONTENT
        height = WRAP_CONTENT
        isFocusable = true
        this.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        contentView = contentView(isShowDelete, isShielding, context) {
            this.dismiss()
            onItemClick(it)
        }
    }

    popupWindow.contentView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED).let {
        //必须contentView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)才能获取宽和高
        val popWidth = popupWindow.contentView.measuredWidth
        val popHeight = popupWindow.contentView.measuredHeight

        val location = IntArray(2)
        getLocationOnScreen(location)

        val x = location[0] + (measuredWidth - popWidth) / 2
        val y = location[1] - popHeight + dip(1)
        popupWindow.showAtLocation(this, Gravity.NO_GRAVITY, x, y)
    }

}


@SuppressLint("InflateParams")
private fun contentView(isShowDelete: Boolean, isShielding: Boolean, ctx: Context, onItemClick: (pos: Int) -> Unit = { }): View {
    val contentView = LayoutInflater.from(ctx).inflate(R.layout.live_im_bubble_pop_layout, null, false)
    contentView.findViewById<ImageView>(R.id.bg_delete_shielding).apply {
        background = if (isShielding) ContextCompat.getDrawable(context, R.drawable.live_im_bubble_bg_img) else ContextCompat.getDrawable(context, R.drawable.live_im_bubble_bg_img_delete)
    }
    contentView.findViewById<View>(R.id.live_stop_delete_msg_tv).apply {
        isVisible = isShowDelete
        setOnClickListener {
            //删除
            onItemClick(0)
        }
    }
    contentView.findViewById<View>(R.id.live_stop_send_msg_tv).apply {
        //拉黑
        isVisible = isShielding
        setOnClickListener { onItemClick(1) }
    }
    return contentView
}




