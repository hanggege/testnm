package com.mei.message.wiget

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import com.mei.orc.ext.layoutInflaterKtToParentAttach
import com.mei.wood.R
import kotlinx.android.synthetic.main.include_menu_item_view.view.*

/**
 *
 * @author Created by lenna on 2020/6/15
 */
class IconTextMenuView @JvmOverloads constructor(context: Context, attributeSet: AttributeSet? = null, defStyleAttr: Int = 0) : LinearLayout(context, attributeSet, defStyleAttr) {
    private var mIconId: Int
    private var mTextContent: String? = ""

    init {
        layoutInflaterKtToParentAttach(R.layout.include_menu_item_view)
        val ta = context.obtainStyledAttributes(attributeSet, R.styleable.IconTextMenuView)
        mTextContent = ta.getString(R.styleable.IconTextMenuView_text_content)
        mIconId = ta.getResourceId(R.styleable.IconTextMenuView_icon_drawable, R.drawable.message_my_fans)
        ta.recycle()
        icon_iv.setImageResource(mIconId)
        text_tv.text = mTextContent
    }

}