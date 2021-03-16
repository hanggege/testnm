package com.mei.intimate.view

import android.content.Context
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.core.view.updateLayoutParams
import com.growingio.android.sdk.collection.GrowingIO
import com.growingio.android.sdk.collection.ImpressionMark
import com.mei.GrowingUtil
import com.mei.base.ui.nav.Nav
import com.mei.base.util.glide.GlideDisPlayDefaultID
import com.mei.login.checkLogin
import com.mei.orc.ext.checkChange
import com.mei.orc.ext.dip
import com.mei.orc.ext.getIndexOrNull
import com.mei.orc.ext.screenWidth
import com.mei.orc.imageload.glide.glideDisplay
import com.mei.widget.recyclerview.banner.SimpleBannerHolder
import com.mei.wood.R
import com.net.model.chick.room.RoomList.BannerItemBean
import kotlinx.android.synthetic.main.view_intimate_head_banner.view.*
import org.json.JSONObject

/**
 * Created by hang on 2020-02-14.
 */
class IntimateHeadBannerView(context: Context) : FrameLayout(context) {
    private val bannerList = arrayListOf<BannerItemBean>()

    init {
        LayoutInflater.from(context).inflate(R.layout.view_intimate_head_banner, this)
    }

    fun notifyData(list: List<BannerItemBean>) {
        if (list.size == 1) {
            try {
                GrowingIO.getInstance().markViewImpression(ImpressionMark(service_exclusive_banner_view, "function_view")
                        .setGlobalId("0")
                        .setVisibleScale(0.5f)
                        .setVariable(JSONObject().apply {
                            put("function_name", "banner")
                            put("function_page", "首页banner")
                            put("click_type", "")
                            put("status", list.getIndexOrNull(0)?.action.orEmpty())
                        }))
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        if (bannerList.checkChange(list) { t1, t2 -> !TextUtils.equals(t1.image, t2.image) || !TextUtils.equals(t1.action, t2.action) }) {
            service_exclusive_indicator_view.buildIndicator(list.size, R.drawable.notify_header_indicator_bg, dip(7))
            service_exclusive_banner_view.setSelectedListener { view, position ->
                service_exclusive_indicator_view.selectIndex(position)
                println("IntimateHeadView:当前选中position：$position")
                try {
                    if (list.isNotEmpty()) {
                        GrowingIO.getInstance().markViewImpression(ImpressionMark(view, "function_view")
                                .setGlobalId("0")
                                .setVisibleScale(0.5f)
                                .setVariable(JSONObject().apply {
                                    put("function_name", "banner")
                                    put("function_page", "首页banner")
                                    put("click_type", "")
                                    put("status", list.getIndexOrNull(position % list.size)?.action.orEmpty())
                                }))
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }.buildPager(list, object : SimpleBannerHolder<BannerItemBean>() {
                override fun createView(context: Context?): View {
                    return ImageView(context).apply {
                        layoutParams = LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                        scaleType = ImageView.ScaleType.FIT_XY
                    }
                }

                override fun updateUI(item: View, position: Int, data: BannerItemBean) {
                    item.updateLayoutParams {
                        width = screenWidth
                        height = width * 7 / 15
                    }
                    (item as? ImageView)?.glideDisplay(data.image, GlideDisPlayDefaultID.default_recommend_banner)
                    item.setOnClickListener {
                        context?.checkLogin {
                            if (it) {
                                GrowingUtil.track("function_click", "function_name", "banner",
                                        "function_page", "首页banner", "click_type", list.getIndexOrNull(position)?.action.orEmpty(), "status", list.getIndexOrNull(position)?.action.orEmpty())
                                Nav.toAmanLink(context, data.action)
                            }
                        }
                    }
                }

            })

            bannerList.clear()
            bannerList.addAll(list)
        } else if (list.isNotEmpty()) {
            service_exclusive_banner_view.notifyItem()
        }
    }
}