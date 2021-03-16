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
import com.net.model.chick.coupon.CouponListInfo

/**
 * DialogCouponAdapter
 *
 * @author kun
 * @version 1.0.0
 * @since 2020-09-10
 */
class DialogCouponAdapter(list: MutableList<CouponListInfo.CouponListLabelInfo>) : BaseQuickAdapter<CouponListInfo.CouponListLabelInfo, BaseViewHolder>(R.layout.item_coupon_label, list) {
    override fun convert(holder: BaseViewHolder, item: CouponListInfo.CouponListLabelInfo) {
        holder.setText(R.id.coupon_label_type_text, getText(item.cateTag))
                .setText(R.id.coupon_title, getText(item.mainTitle))
                .setText(R.id.coupon_original_cost, item.priceText.orEmpty().createSplitSpannable(Color.parseColor("#333333")))
                .setText(R.id.coupon_now_cost, item.discountPriceText.orEmpty().createSplitSpannable(Color.parseColor("#333333")))
                .setText(R.id.coupon_label_button, getText(item.button))
    }

    private fun getText(splitText: SplitText): SpannedString {
        return buildSpannedString {
            appendLink(splitText.text, splitText.color.parseColor(Cxt.getColor(R.color.color_333333)))
        }
    }
}