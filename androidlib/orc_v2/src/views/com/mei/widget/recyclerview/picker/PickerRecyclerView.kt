package com.mei.widget.recyclerview.picker

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mei.orc.ext.dip
import com.mei.widget.recyclerview.manager.PickerLayoutManager

/**
 *  Created by zzw on 2019-12-02
 *  Des:
 */


class PickerRecyclerView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0)
    : RecyclerView(context, attrs, defStyle) {


    private val mLinePaint by lazy {
        Paint().apply {
            color = Color.parseColor("#e4e4e4")
            //1px
            strokeWidth = 1.0f
        }
    }


    override fun dispatchDraw(canvas: Canvas?) {
        super.dispatchDraw(canvas)

        val manager = layoutManager
        if (manager is PickerLayoutManager
                && manager.itemViewHeight > 0
                && manager.itemCount > 0
                && height > 0) {
            val lineTop = height / 2 - manager.itemViewHeight / 2
            val lineBottom = height / 2 + manager.itemViewHeight / 2
            canvas?.drawLine(0f, lineTop.toFloat(), width.toFloat(), lineTop.toFloat(), mLinePaint)
            canvas?.drawLine(0f, lineBottom.toFloat(), width.toFloat(), lineBottom.toFloat(), mLinePaint)
        }
    }


    class PickerAdapter(private val mList: List<String>) : RecyclerView.Adapter<PickerAdapter.ViewHolder>() {
        override fun getItemCount(): Int = mList.size

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = TextView(parent.context).apply {
                layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dip(45))
                setTextColor(Color.parseColor("#333333"))
                textSize = 18.0f
                gravity = Gravity.CENTER
                typeface = Typeface.defaultFromStyle(Typeface.BOLD)
            }
            return ViewHolder(view)
        }

        override fun onBindViewHolder(helper: ViewHolder, position: Int) {
            helper.tvText.text = mList[position]
        }

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            var tvText: TextView = itemView as TextView

        }
    }

}