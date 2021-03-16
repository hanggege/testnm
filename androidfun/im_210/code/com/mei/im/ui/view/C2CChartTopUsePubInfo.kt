package com.mei.im.ui.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.mei.im.ui.adapter.TagLabelAdapter
import com.mei.wood.R

/**
 * IM顶部栏
 *顶部个人信息
 */
@Suppress("DEPRECATION")
class C2CChartTopUsePubInfo @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : LinearLayout(context, attrs, defStyleAttr) {

    val labelAdapter by lazy {
        TagLabelAdapter(context, labelList)
    }
    val labelList = arrayListOf<String>()

    init {
        LayoutInflater.from(context).inflate(R.layout.chat_c2c_im_top_two_pub_info, this)
    }


}