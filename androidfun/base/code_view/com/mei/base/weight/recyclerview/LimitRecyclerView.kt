package com.mei.base.weight.recyclerview

import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.RecyclerView
import com.mei.wood.R

/**
 * Created by LingYun on 2017/10/10.
 *
 * @描述：限制有最宽或者最高的RecyclerView，适用于局部
 */

class LimitRecyclerView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0)
    : RecyclerView(context, attrs, defStyle) {
    private var maxHeight: Int = 0
    private var maxWidth: Int = 0

    init {
        if (attrs != null) {
            val a = context.obtainStyledAttributes(attrs, R.styleable.LimitRecyclerView)
            maxHeight = a.getDimensionPixelSize(R.styleable.LimitRecyclerView_RecycleView_maxHeight, 0)
            maxWidth = a.getDimensionPixelSize(R.styleable.LimitRecyclerView_RecycleView_maxWidth, 0)
            a.recycle()
        }
    }

    override fun onMeasure(widthSpec: Int, heightSpec: Int) {
        super.onMeasure(if (maxWidth > 0 && MeasureSpec.getSize(widthSpec) > maxWidth)
            MeasureSpec.makeMeasureSpec(maxWidth, MeasureSpec.getMode(widthSpec)) else widthSpec,
                if (maxHeight > 0 && MeasureSpec.getSize(heightSpec) > maxHeight)
                    MeasureSpec.makeMeasureSpec(maxHeight, MeasureSpec.getMode(heightSpec)) else heightSpec)
    }
}
