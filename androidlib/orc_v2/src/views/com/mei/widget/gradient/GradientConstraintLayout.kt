package com.mei.widget.gradient

import android.content.Context
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2020/8/5
 *
app:gd_LB_radius="50dp" // 左下角圆角
app:gd_RB_radius="20dp" //右下角圆角
app:gd_LT_radius="5dp" // 左上角圆角
app:gd_RT_radius="10dp" //右上角圆角
app:gd_radius="20dp" // 全局圆角
app:gd_start_color="@android:color/holo_blue_light" //开始渐变颜色
app:gd_center_color="@android:color/holo_red_light" //中间渐变颜色
app:gd_end_color="@android:color/holo_green_light"// 结束颜色
app:gd_direction="leftToRight" //渐变方向

app:gd_stroke_start_color="@android:color/background_dark" //边框开始颜色
app:gd_stroke_center_color="@android:color/holo_orange_dark" //边框 中间颜色
app:gd_stroke_end_color="@android:color/darker_gray" //边框结束颜色
app:gd_stroke_direction="topToBottom"//边框颜色渐变方向 ,没有设置的话取上面的gd_direction
app:gd_stroke_width="10dp" //边框宽度

 */
class GradientConstraintLayout @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
    : ConstraintLayout(context, attrs, defStyleAttr) {

    val delegate: GradientDelegate = GradientDelegate(context, this, attrs)

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        super.onLayout(changed, l, t, r, b)
        delegate.refresh()
    }


}