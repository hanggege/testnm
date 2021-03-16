package com.mei.live.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import androidx.core.view.isVisible
import com.joker.im.custom.chick.CouponInfo
import com.mei.live.ui.VideoLiveRoomActivity
import com.mei.live.ui.dialog.showCommonBottomDialog
import com.mei.orc.Cxt
import com.mei.orc.event.postAction
import com.mei.orc.ext.dip
import com.mei.orc.ext.layoutInflaterKt
import com.mei.orc.imageload.glide.glideDisplay
import com.mei.orc.john.model.JohnUser
import com.mei.orc.util.date.formatTimeDown
import com.mei.widget.recyclerview.banner.SimpleBannerHolder
import com.mei.wood.R
import com.mei.wood.constant.Preference
import com.mei.wood.util.MeiUtil
import com.mei.wood.util.logDebug
import com.net.MeiUser
import com.net.model.room.RoomRedPacket
import com.opensource.svgaplayer.SVGADrawable
import com.opensource.svgaplayer.SVGAImageView
import com.opensource.svgaplayer.SVGAParser
import com.opensource.svgaplayer.SVGAVideoEntity
import kotlinx.android.synthetic.main.fragment_packet_coupon.view.*
import java.net.URL

/**
 * PacketCouponBanner
 *
 * @author kun
 * @version 1.0.0
 * @since 2020-09-11
 */
class PacketCouponBanner @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
        LinearLayout(context, attrs, defStyleAttr) {

    private var videoItem: SVGAVideoEntity? = null

    init {
        orientation = VERTICAL
        LayoutInflater.from(context).inflate(R.layout.fragment_packet_coupon, this)
        packet_coupon_indicator.buildIndicator(2, R.drawable.selector_gift_indicators, dip(5f))
    }

    fun setBannerData(data: List<Any>) {
        data.forEach {
            if (it is CouponInfo) {
                initCouponAnimation(it.effect)
                return@forEach
            }
        }
        packet_coupon_indicator.isVisible = data.size > 1
        packet_coupon_banner.setDuration(6)
                .setScrollDuration(1.5f)
                .setSelectedListener { view, position ->
                    selectItem(view, position, data[position % data.size] is CouponInfo)
                }
                .buildPager(data, object : SimpleBannerHolder<Any>() {
                    override fun createView(context: Context): View {
                        return context.layoutInflaterKt(R.layout.item_fragment_packet_coupon)
                    }

                    override fun updateUI(view: View, position: Int, item: Any) {
                        when (item) {
                            is RoomRedPacket -> {
                                updateRedPacket(view, item)
                            }
                            is CouponInfo -> {
                                updateCoupon(view, item, data.size == 1)
                            }
                        }
                    }

                    override fun updateCertainUI(view: View, position: Int, data: Any) {
                        val redPacketView = view.findViewById<ImageView>(R.id.red_packet_iv)
                        if (redPacketView.isVisible && data is RoomRedPacket) {
                            updateRedPacket(view, data)
                        }
                    }
                })
    }

    fun refreshRedPacket(list: List<Any>, item: RoomRedPacket) {

        packet_coupon_banner.refreshList(list, item)
    }

    private fun initCouponAnimation(svgaUrl: String) {
        var url: URL? = null
        try {
            url = URL(svgaUrl)
        } catch (e: Exception) {
            logDebug(e.toString())
        }
        url?.let {
            SVGAParser.shareParser().decodeFromURL(it, object : SVGAParser.ParseCompletion {
                override fun onComplete(videoItem: SVGAVideoEntity) {
                    this@PacketCouponBanner.videoItem = videoItem
                }

                override fun onError() {}
            })
        }
    }

    private fun selectItem(view: View, position: Int, isCouponInfo: Boolean) {
        val packetView = view.findViewById<ImageView>(R.id.red_packet_iv)
        val textView = view.findViewById<TextView>(R.id.need_duration_tv)
        val couponView = view.findViewById<SVGAImageView>(R.id.live_coupon_svga)
        val couponLayout = view.findViewById<View>(R.id.live_coupon)
        isCouponInfo.apply {
            packetView.isGone = this
            textView.isGone = this
            couponLayout.isVisible = this
            if (this) {
                couponView.startCouponAnimation()
            }
        }
        packet_coupon_indicator.selectIndex(position)
    }

    private fun updateCoupon(item: View, data: CouponInfo, needAnimation: Boolean) {
        item.findViewById<TextView>(R.id.need_duration_tv).isGone = true
        item.findViewById<ImageView>(R.id.red_packet_iv).isGone = true
        item.findViewById<View>(R.id.live_coupon).isVisible = true
        item.setOnClickListener {
            (context as? VideoLiveRoomActivity)?.apply {
                showCommonBottomDialog(MeiUtil.appendParamsUrl(data.action,
                        "user_id" to JohnUser.getSharedUser().userID.toString(),
                        "publisher_id" to roomInfo.publisherId.toString(),
                        "room_id" to roomInfo.roomId))
            }
        }
        val svgaView = item.findViewById<SVGAImageView>(R.id.live_coupon_svga)
        if (videoItem != null) {
            svgaView.setCouponDrawable()
        }
        if (needAnimation) {
            svgaView.startAnimation()
        }
    }

    private fun updateRedPacket(item: View, data: RoomRedPacket) {
        item.findViewById<View>(R.id.live_coupon).isGone = true
        val imageView = item.findViewById<ImageView>(R.id.red_packet_iv).apply { isVisible = true }
        val durationTextView = item.findViewById<TextView>(R.id.need_duration_tv)
        // setClickListener
        item.setOnClickListener {
            postAction(Preference.ROOM_RED_PACKET_CLICK)
        }
        // update text & image
        if (MeiUser.getSharedUser().extra?.bindPhone == true) {
            if (data.duration == 0L) {
                durationTextView.text = "领红包"
            } else {
                durationTextView.text = (data.duration * 1000L).formatTimeDown()
            }
            durationTextView.isVisible = true
            imageView.glideDisplay(data.roomIcon.orEmpty(), R.drawable.icon_live_room_red_packet)
        } else {
            durationTextView.isVisible = false
            imageView.glideDisplay(ContextCompat.getDrawable(Cxt.get(), R.drawable.icon_receive_red_packet))
        }
        // startAnimation
        if (data.needAnimation) {
            item.startAnimation()
        }
    }

    private fun View.startAnimation() {
        val shakeDegrees = 5f
        val rotateAnim: Animation = RotateAnimation(-shakeDegrees, shakeDegrees, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f)
        rotateAnim.duration = 100
        rotateAnim.repeatCount = 5
        this.startAnimation(rotateAnim)
    }

    private fun SVGAImageView.startCouponAnimation() {
        videoItem?.apply {
            val svgaDrawable = SVGADrawable(this)
            clearsAfterDetached = false
            setImageDrawable(svgaDrawable)
            loops = 0
            startAnimation()
        }
    }

    private fun SVGAImageView.setCouponDrawable() {
        videoItem?.apply {
            val svgaDrawable = SVGADrawable(this)
            clearsAfterDetached = false
            setImageDrawable(svgaDrawable)
            stepToFrame(0, false)
        }
    }

    /**
     * 手指超出view的范围50dp，直接发送cancel事件取消掉
     */
    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        if (!pointInView(ev.x, ev.y, dip(50).toFloat())) {
            ev.action = MotionEvent.ACTION_CANCEL
        }
        return super.dispatchTouchEvent(ev)
    }

    private fun pointInView(localX: Float, localY: Float, slop: Float): Boolean {
        return localX >= -slop && localY >= -slop && localX < (right - left + slop) && localY < (bottom - top + slop)
    }
}