package com.mei.live.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.mei.wood.R

/**
 *
 * @ProjectName:    dove
 * @Package:        com.mei.live.views
 * @ClassName:      LevelFrame
 * @Description:
 * @Author:         zxj
 * @CreateDate:     2020/6/24 20:33
 * @UpdateUser:
 * @UpdateDate:     2020/6/24 20:33
 * @UpdateRemark:
 * @Version:
 */

class LevelFrame @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    init {
        LayoutInflater.from(context).inflate(R.layout.view_level, this)
    }


}