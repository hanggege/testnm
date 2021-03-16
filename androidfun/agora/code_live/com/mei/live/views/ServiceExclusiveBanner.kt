package com.mei.live.views

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import com.joker.im.custom.chick.ServiceInfo
import com.mei.live.ui.VideoLiveRoomActivity
import com.mei.live.ui.dialog.showSingleBtnCommonDialog
import com.mei.live.ui.fragment.applyUpStream
import com.mei.orc.ext.dip
import com.mei.orc.ext.layoutInflaterKt
import com.mei.orc.imageload.glide.glideDisplay
import com.mei.widget.recyclerview.banner.SimpleBannerHolder
import com.mei.wood.R
import com.mei.wood.ui.MeiCustomActivity
import com.net.model.room.RoomType
import kotlinx.android.synthetic.main.fragment_server_live.view.*

/**
 *
 * @ProjectName:    dove
 * @Package:        com.mei.live.views
 * @ClassName:      ServiceExclusiveBanner
 * @Description:
 * @Author:         zxj
 * @CreateDate:     2020/5/29 16:57
 * @UpdateUser:
 * @UpdateDate:     2020/5/29 16:57
 * @UpdateRemark:
 * @Version:
 */
class ServiceExclusiveBanner @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {
    init {
        LayoutInflater.from(context).inflate(R.layout.fragment_server_live, this)
    }

    fun setBannerData(listData: List<ServiceInfo>) {
        service_exclusive_indicator_view.buildIndicator(listData.size, R.drawable.selector_gift_indicators, dip(5))
        service_exclusrice_banner_view.setDuration(10)
                .setSelectedListener { _, position -> service_exclusive_indicator_view.selectIndex(position) }.buildPager(listData, object : SimpleBannerHolder<ServiceInfo>() {

                    override fun createView(context: Context): View {
                        return context.layoutInflaterKt(R.layout.fragment_server_exclusive_live)
                    }

                    @SuppressLint("SetTextI18n")
                    override fun updateUI(item: View, position: Int, data: ServiceInfo) {
                        item.findViewById<TextView>(R.id.tv_title_service_live_sell).text = data.serviceName
                        item.findViewById<TextView>(R.id.tv_live_service_time).text = "剩余时长:${data.timeRemaining}分钟"
                        item.findViewById<ImageView>(R.id.anchor_avatar).apply {
                            isVisible = data.recommendInfo?.personImage.orEmpty().isNotEmpty()
                            glideDisplay(data.recommendInfo?.personImage.orEmpty(), R.drawable.default_female_head)
                        }
                        item.findViewById<TextView>(R.id.start_service).setPadding(if (data.recommendInfo?.personImage.orEmpty().isNotEmpty()) dip(26) else dip(7), 0, dip(7), 0)
                        item.findViewById<ConstraintLayout>(R.id.rb_click_connect).setOnClickListener {
                            (context as? VideoLiveRoomActivity)?.run {
                                applyUpStream(data.recommendInfo?.roomId.orEmpty(), RoomType.EXCLUSIVE, data.specialServiceOrderId) { info ->
                                    if (info != null) {
                                        pendingUpStream = true
                                        showSingleBtnCommonDialog("知心达人已收到申请", "当前共${info.applyCount}人排麦,请耐心等待", "注：退出APP将自动下麦") {
                                            it.dismissAllowingStateLoss()
                                        }
                                    }
                                }
                            }
                        }
                    }
                })
    }
}