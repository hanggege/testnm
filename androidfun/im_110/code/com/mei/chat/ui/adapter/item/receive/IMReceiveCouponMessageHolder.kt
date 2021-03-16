package com.mei.chat.ui.adapter.item.receive

import android.graphics.Color
import android.view.View
import android.widget.TextView
import com.joker.im.Message
import com.joker.im.custom.chick.ChickCustomData
import com.joker.im.custom.chick.CouponInfo
import com.mei.base.common.COUPON_TO_LIVE
import com.mei.base.ui.nav.Nav
import com.mei.live.ext.createSplitSpannable
import com.mei.live.ui.VideoLiveRoomActivity
import com.mei.orc.event.postAction
import com.mei.orc.util.click.ClickMovementMethod
import com.mei.widget.holder.activity
import com.mei.wood.R
import com.mei.wood.util.MeiUtil
import com.mei.wood.web.MeiWebActivityLauncher
import com.mei.wood.web.MeiWebData

/**
 * IMReceiveCouponMessage
 *
 * @author kun
 * @version 1.0.0
 * @since 2020-09-09
 */
class IMReceiveCouponMessageHolder(itemView: View) : IMReceiveBaseMessageHolder(itemView) {
    private var mCouponData = CouponInfo()
    override fun refreshChild(msg: Message) {
        val data = getCustomData()?.data as? ChickCustomData
        val contentTextView = getView<TextView>(R.id.im_msg_content)
        contentTextView.setOnTouchListener(ClickMovementMethod.newInstance())
        contentTextView.text = data?.content.orEmpty().createSplitSpannable(Color.parseColor("#333333"), click = {
            Nav.toAmanLink(itemView.context, it)
        })
        contentTextView.makeCustomLink(msg.getSummary())
        itemView.findViewById<View>(R.id.avatar_img)?.setOnClickListener(null)
        data?.couponInfo?.let { couponData ->
            mCouponData = couponData
            setText(R.id.im_msg_discount, mCouponData.discountText)
            setText(R.id.im_msg_coupon_show_more, mCouponData.btnText)
            getView<View>(R.id.im_message_container).setOnClickListener {
                if (activity is VideoLiveRoomActivity) { // 直播页显示半屏弹窗，私聊页显示全屏
                    (activity as VideoLiveRoomActivity).apply {
                        postAction(COUPON_TO_LIVE, MeiUtil.appendParamsUrl(MeiUtil.appendGeneraUrl(mCouponData.action),
                                "room_id" to roomId,
                                "publisher_id" to roomInfo.publisherId.toString()))
                    }
                } else {
                    MeiWebActivityLauncher.startActivity(activity, MeiWebData(mCouponData.action, 0))
                }
            }
        }
    }
}