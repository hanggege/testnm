package com.mei.live.ui.dialog.adapter

import android.graphics.Color
import android.text.SpannedString
import androidx.core.text.buildSpannedString
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.joker.im.custom.chick.SplitText
import com.mei.live.ext.createSplitSpannable
import com.mei.live.ext.parseColor
import com.mei.orc.Cxt
import com.mei.wood.R
import com.mei.wood.extensions.appendLink
import com.net.model.room.CouponLabelInfo

/**
 * UserInfoCardCouponAdapter
 *
 * @author kun
 * @version 1.0.0
 * @since 2020-09-09
 */
class UserInfoCardCouponAdapter(list: MutableList<CouponLabelInfo>) : BaseQuickAdapter<CouponLabelInfo, BaseViewHolder>(R.layout.item_user_info_card_coupon, list) {
    override fun convert(holder: BaseViewHolder, item: CouponLabelInfo) {
        holder.setText(R.id.user_info_left_top_text, getText(item.couponTags))
                .setText(R.id.user_info_discount_text, item.discountName.createSplitSpannable(Color.parseColor("#333333")))
                .setText(R.id.user_info_coupon_title, getText(item.applicationScopeText))
                .setText(R.id.user_info_deadline_text, getText(item.timeLimit))
    }

    private fun getText(splitText: SplitText) : SpannedString {
        return buildSpannedString {
            appendLink(splitText.text, splitText.color.parseColor(Cxt.getColor(R.color.color_333333)))
        }
    }
}