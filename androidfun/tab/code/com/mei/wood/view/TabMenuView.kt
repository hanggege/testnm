package com.mei.wood.view

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import com.mei.orc.ext.dip
import com.mei.widget.gradient.GradientFrameLayout
import com.mei.widget.gradient.applyViewDelegate
import com.mei.wood.R

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2019/3/28
 */
class TabMenuView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
    : FrameLayout(context, attrs, defStyleAttr) {

    var tabSelectedListener: OnTabItemSelectedListener? = null

    var selectedIndex: Int = -1
    var itemViews = arrayListOf<NewTabItemView>()
        set(value) {
            menuView.removeAllViews()
            field = value
            value.forEachIndexed { index, tabItemView ->
                menuView.addView(tabItemView, LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1f))
                tabItemView.setOnClickListener {
                    setCurrentItem(index)
                }
            }
        }

    val menuView by lazy {
        LinearLayout(context)
    }

    val gradientView by lazy {
        GradientFrameLayout(context).apply {
            delegate.applyViewDelegate {
                direction = 1
                startColor = ContextCompat.getColor(context, R.color.alpha0_black)
                endColor = ContextCompat.getColor(context, R.color.color_0A000000)
            }
        }
    }

    val bgView by lazy {
        View(context).apply {
            setBackgroundColor(Color.WHITE)
        }
    }

    init {
        layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dip(65))
        addView(bgView, LayoutParams(LayoutParams.MATCH_PARENT, dip(50)).apply {
            gravity = Gravity.BOTTOM
        })
        addView(gradientView, ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dip(15)))
        addView(menuView, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT).apply {
            gravity = Gravity.BOTTOM
        })
    }


    /**
     * 选中当前位置
     */
    fun setCurrentItem(index: Int) {
        if (index >= 0 && index < itemViews.size) {
            if (selectedIndex == index) {
                /** 重复选择 **/
                itemViews.forEachIndexed { i, tabItemView ->
                    tabItemView.isSelected = i == index
                }
                tabSelectedListener?.onRepeat(index)
            } else if (tabSelectedListener?.onInterceptor(index) != true) {
                /** 是否进行拦截 **/
                itemViews.forEachIndexed { i, tabItemView ->
                    tabItemView.isSelected = i == index
                }
                tabSelectedListener?.onSelected(index, selectedIndex)
                selectedIndex = index
            }
        }
    }

}

