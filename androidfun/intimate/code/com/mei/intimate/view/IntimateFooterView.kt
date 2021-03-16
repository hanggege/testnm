package com.mei.intimate.view

import android.content.Context
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import com.mei.orc.ext.layoutInflaterKtToParentAttach
import com.mei.orc.imageload.glide.glideDisplay
import com.mei.wood.R
import com.net.model.chick.room.RoomList.FooterLabel
import kotlinx.android.synthetic.main.view_intimate_footer.view.*

/**
 * IntimateFooterView
 *
 * @author kun
 * @version 1.0.0
 * @since 2020-11-04
 */
class IntimateFooterView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
        ConstraintLayout(context, attrs, defStyleAttr)  {

    init {
        layoutInflaterKtToParentAttach(R.layout.view_intimate_footer)
    }

    fun convertData(footerLabel: FooterLabel?) {
        footerLabel?.let {
            intimate_footer_logo.glideDisplay(it.imageUrl.orEmpty())
            intimate_footer_line_text.text = it.text.orEmpty()
        }
    }

}