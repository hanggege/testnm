package com.mei.message

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.core.view.contains
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.growingio.android.sdk.collection.GrowingIO
import com.growingio.android.sdk.collection.ImpressionMark
import com.mei.GrowingUtil
import com.mei.base.ui.nav.Nav
import com.mei.login.checkLogin
import com.mei.orc.ext.dip
import com.mei.orc.ext.getIndexOrNull
import com.mei.orc.ext.layoutInflaterKt
import com.mei.orc.ext.screenWidth
import com.mei.orc.imageload.glide.glideDisplay
import com.mei.widget.recyclerview.banner.BannerView
import com.mei.widget.recyclerview.banner.PageIndicatorView
import com.mei.widget.recyclerview.banner.SimpleBannerHolder
import com.mei.widget.round.RoundImageView
import com.mei.wood.R
import com.mei.wood.ui.fragment.TabItemFragment
import com.net.model.chick.message.MessageBanner
import org.json.JSONObject

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2020/2/25
 */
abstract class BaseBannerFragment : TabItemFragment() {

    abstract val bannerAdapter: BaseQuickAdapter<*, BaseViewHolder>
    abstract val dataChange: () -> Unit
    abstract fun refreshIMList()

    private val headerView: View by lazy { requireActivity().layoutInflaterKt(R.layout.header_notify_layout) }
    private val bannerView: BannerView by lazy { headerView.findViewById<BannerView>(R.id.notify_header_banner) }
    private val indicatorView: PageIndicatorView by lazy { headerView.findViewById<PageIndicatorView>(R.id.notify_header_indicator_view) }

    var bannerList: List<MessageBanner.Banner> = arrayListOf()

    private val dataObserver by lazy {
        object : RecyclerView.AdapterDataObserver() {
            override fun onChanged() {
                super.onChanged()
                dataChange()
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bannerAdapter.registerAdapterDataObserver(dataObserver)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        bannerAdapter.unregisterAdapterDataObserver(dataObserver)
    }

    internal fun buildHeader() {
        if (isAdded) {
            if (bannerList.isEmpty()) {
                if (bannerAdapter.headerLayout?.contains(headerView) == true) {
                    bannerAdapter.removeHeaderView(headerView)
                }
            } else {
                if (bannerList.size == 1) {
                    try {
                        GrowingIO.getInstance().markViewImpression(ImpressionMark(bannerView, "function_view")
                                .setGlobalId("0")
                                .setVisibleScale(0.5f)
                                .setVariable(JSONObject().apply {
                                    put("function_name", "banner")
                                    put("function_page", "消息页banner")
                                    put("click_type", "")
                                    put("status", bannerList.getIndexOrNull(0)?.action.orEmpty())
                                }))
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                val headerContainer = bannerAdapter.headerLayout
                if (headerContainer == null || !headerContainer.contains(headerView)) {
                    (headerView.parent as? ViewGroup)?.removeAllViews()
                    val childCount = headerContainer?.childCount
                    if (childCount != null && childCount != 0) {
                        bannerAdapter.addHeaderView(headerView, childCount)
                    } else {
                        bannerAdapter.addHeaderView(headerView)
                    }
                }
                bannerView.updateLayoutParams {
                    height = if (bannerList.isNotEmpty()) (screenWidth - dip(40)) * 90 / 335 else 0
                }
                bannerAdapter.notifyDataSetChanged()
                indicatorView.buildIndicator(bannerList.size, R.drawable.notify_header_indicator_bg, dip(7))
                bannerView.setSelectedListener { _, position ->
                    indicatorView.selectIndex(position)
                    try {
                        GrowingIO.getInstance().markViewImpression(ImpressionMark(view, "function_view")
                                .setGlobalId("0")
                                .setVisibleScale(0.5f)
                                .setVariable(JSONObject().apply {
                                    put("function_name", "banner")
                                    put("function_page", "消息页banner")
                                    put("click_type", "")
                                    put("status", bannerList.getIndexOrNull(position)?.action.orEmpty())
                                }))
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                        .buildPager(bannerList,
                                object : SimpleBannerHolder<MessageBanner.Banner>() {
                                    override fun createView(context: Context): View {
                                        return RoundImageView(context).apply {
                                            layoutParams = FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                                            scaleType = ImageView.ScaleType.FIT_XY
                                            radius = dip(7).toFloat()
                                        }
                                    }

                                    override fun updateUI(item: View, position: Int, data: MessageBanner.Banner) {
                                        item.updateLayoutParams {
                                            width = screenWidth - item.dip(40)
                                            height = width * 90 / 335
                                        }
                                        (item as? ImageView)?.glideDisplay(data.image, R.color.color_f8f8f8)
                                        item.setOnClickListener {
                                            activity.checkLogin {
                                                GrowingUtil.track("function_click", "function_name", "banner",
                                                        "function_page", "消息页banner", "click_type", bannerList.getIndexOrNull(position)?.action.orEmpty(),
                                                        "status", bannerList.getIndexOrNull(position)?.action.orEmpty())
                                                if (it) Nav.toAmanLink(activity, data.action)
                                            }

                                        }
                                    }

                                })

            }
        }
    }


}