package com.mei.im.ui.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.view.updateLayoutParams
import com.joker.im.custom.chick.ServiceInfo
import com.mei.base.util.shadow.setListShadowDefault
import com.mei.chat.ui.view.startPrivateUpstream
import com.mei.orc.ext.checkChange
import com.mei.orc.ext.dip
import com.mei.widget.recyclerview.banner.SimpleBannerHolder
import com.mei.wood.R
import com.mei.wood.ui.MeiCustomActivity
import kotlinx.android.synthetic.main.include_top_header_banner.view.*


/**
 *
 * @author Created by lenna on 2020/5/26
 * C2C页面显示的服务列表banner
 */
class ImServiceListBannerView @JvmOverloads constructor(context: Context, attr: AttributeSet? = null, defStyleAttr: Int = 0)
    : LinearLayout(context, attr, defStyleAttr) {
    private var mBannerList = arrayListOf<ServiceInfo>()

    init {
        LayoutInflater.from(context).inflate(R.layout.include_top_header_banner, this)
    }

    /**
     * 刷新item
     */
    fun notifyBannerItem(list: List<ServiceInfo>, chatId: Int) {
        if (mBannerList.checkChange(list) { t1, t2 -> t1.specialServiceOrderId != t2.specialServiceOrderId || t1.timeRemaining != t2.timeRemaining }) {
            mBannerList.clear()
            mBannerList.addAll(list)
            service_header_indicator_view.buildIndicator(mBannerList.size, R.drawable.selector_service_banner_indicator)
            service_header_banner.setSelectedListener { _, position -> service_header_indicator_view.selectIndex(position) }
                    .setDuration(10)
                    .buildPager(mBannerList, object : SimpleBannerHolder<ServiceInfo>() {
                        override fun createView(context: Context?): View {
                            return LayoutInflater.from(context).inflate(R.layout.im_item_top_header_banner, null).apply {


                            }
                        }

                        override fun updateUI(view: View?, position: Int, data: ServiceInfo) {
                            view?.findViewById<RelativeLayout>(R.id.banner_con_rl)?.updateLayoutParams<RelativeLayout.LayoutParams> {
                                leftMargin = dip(7.5f)
                                rightMargin = dip(7.5f)
                            }
                            view?.findViewById<RelativeLayout>(R.id.banner_con_rl)?.let { setListShadowDefault(it) }
                            view?.findViewById<TextView>(R.id.im_service_name_tv)?.text = data.serviceName
                            view?.findViewById<TextView>(R.id.im_service_price_tv)?.text = "剩余时长：${data.timeRemaining}分钟"
                            view?.setOnClickListener {
                                (it.context as? MeiCustomActivity)?.startPrivateUpstream(chatId, data.specialServiceOrderId)
                            }
                        }

                    })


        } else {
            service_header_banner.notifyItem()
        }
    }
}