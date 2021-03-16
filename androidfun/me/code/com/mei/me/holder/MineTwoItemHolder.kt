package com.mei.me.holder;

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.text.buildSpannedString
import com.mei.base.baseadapter.multi.MultiViewHolder
import com.mei.base.ui.nav.Nav
import com.mei.live.ext.parseColor
import com.mei.login.checkLogin
import com.mei.orc.Cxt
import com.mei.orc.imageload.glide.glideDisplay
import com.mei.wood.R
import com.mei.wood.extensions.appendLink
import com.net.model.user.MyPageInfo

/**
 * MineRadioHolder
 *
 * @author kun
 * @version 1.0.0
 * @since 2020-09-24
 */
class MineTwoItemHolder(view: View) : MultiViewHolder(view) {

    private var listData = mutableListOf<MyPageInfo.MeTabItem>()

    private val firstImageView by lazy { getView<ImageView>(R.id.user_first_img) }
    private val firstTextView by lazy { getView<TextView>(R.id.user_first_text) }

    private val secondImageView by lazy { getView<ImageView>(R.id.user_second_img) }
    private val secondTextView by lazy { getView<TextView>(R.id.user_second_text) }
    
    init {
        firstImageView.setOnClickListener { 
            turnTo(listData[0])
        }
        secondImageView.setOnClickListener {
            turnTo(listData[1])
        }
        
        firstTextView.setOnClickListener { firstImageView.performClick() }
        secondTextView.setOnClickListener { secondImageView.performClick() }
    }

    override fun refresh(data: Any?) {
        if (data is MineMutableData && data.list?.size == 2) {
            listData = data.list

            firstImageView.glideDisplay(listData[0].icon.orEmpty())
            firstTextView.apply {
                text = buildSpannedString {
                    appendLink(listData[0].name?.text.orEmpty(), listData[0].name?.color.parseColor(Cxt.getColor(R.color.color_333333)))
                }
                paint.isFakeBoldText = true
            }

            secondImageView.glideDisplay(listData[1].icon.orEmpty())
            secondTextView.apply {
                text = buildSpannedString {
                    appendLink(listData[1].name?.text.orEmpty(), listData[1].name?.color.parseColor(Cxt.getColor(R.color.color_333333)))
                }
                paint.isFakeBoldText = true
            }
        }
    }

    private fun turnTo(tabItem: MyPageInfo.MeTabItem) {
        if (!tabItem.needLogin || itemView.context.checkLogin()) {
            Nav.toAmanLink(itemView.context, tabItem.action.orEmpty())
        }
    }
}