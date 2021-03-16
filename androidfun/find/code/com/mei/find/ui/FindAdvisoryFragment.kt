package com.mei.find.ui

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebSettings
import android.webkit.WebView
import androidx.core.view.isVisible
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks
import com.github.ksoichiro.android.observablescrollview.ScrollState
import com.github.rubensousa.gravitysnaphelper.GravitySnapHelper
import com.mei.GrowingUtil
import com.mei.base.common.CHANG_LOGIN
import com.mei.base.network.netchange.NetWorkListener
import com.mei.base.network.netchange.netChangeToAvailable
import com.mei.base.ui.nav.Nav
import com.mei.base.ui.nav.NavLinkHandler
import com.mei.base.util.web.initWebViewSetting
import com.mei.base.util.web.setWebChromeClient
import com.mei.find.adapter.FindContentTabAdapter
import com.mei.login.checkLogin
import com.mei.orc.ActivityLauncher
import com.mei.orc.event.bindAction
import com.mei.orc.net.NetInfo
import com.mei.orc.tab.TabItemContent
import com.mei.orc.util.click.isOnDoubleClick
import com.mei.orc.util.string.decodeUrl
import com.mei.orc.util.string.getInt
import com.mei.orc.util.ui.setViewVisibleOrGone
import com.mei.wood.R
import com.mei.wood.ext.AmanLink
import com.mei.wood.extensions.executeKt
import com.mei.wood.ui.fragment.TabItemFragment
import com.mei.wood.util.MeiUtil
import com.mei.wood.util.parseValue
import com.mei.wood.web.MelkorWebViewClient
import com.net.model.chick.find.FindCourseTab
import com.net.model.chick.tab.TabItem
import com.net.network.chick.find.FindCourseTabRequest
import kotlinx.android.synthetic.main.find_advisory_layout.*
import launcher.Boom
import kotlin.math.min

/**
 * FindAdvisoryFragment
 *
 * @author kun
 * @version 1.0.0
 * @since 2020-08-13
 */
class FindAdvisoryFragment : TabItemFragment(), NetWorkListener, TabItemContent, SwipeRefreshLayout.OnRefreshListener, IFindTabContent {

    @Boom
    var item: TabItem? = null

    var tabHeight = 0

    private var first = true

    private var refreshMethod = ""

    private var isContentLoaded = false

    private val mData = arrayListOf<FindCourseTab.TabInfo>()
    private val contentTabAdapter by lazy {
        FindContentTabAdapter(mData).apply {
            percent = 1f
            setOnItemClickListener { _, _, position ->
                selectedPos = position
                notifyDataSetChanged()
                find_advisory_web_view.loadUrl("javaScript:tabsChange(${mData[position].partitionId})")
                GrowingUtil.track("consult_page_click",
                        "page_type", "${item?.name}页",
                        "main_app_gn_pro", mData[position].partitionName,
                        "view_type", "二屏发现页",
                        "click_module", "品类分区",
                        "click_content", "")
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        ActivityLauncher.bind(this)
        return inflater.inflate(R.layout.find_advisory_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()

        initContentView()
        requestData(item?.id ?: "advisory")

        bindAction<Boolean>(CHANG_LOGIN) {
            loadUrl()
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    fun initView() {
        find_advisory_swipe_refresh.setOnRefreshListener(this)
        find_advisory_web_view.settings.apply {
            javaScriptCanOpenWindowsAutomatically = true
            useWideViewPort = true
            loadWithOverviewMode = true
            cacheMode = WebSettings.LOAD_NO_CACHE
        }
        find_advisory_web_view.apply {
            initWebViewSetting()
            setWebChromeClient()
            webViewClient = object : MelkorWebViewClient() {
                override fun handleUrlLoading(view: WebView, url: String): Boolean {
                    val decodeUrl = url.decodeUrl(true)
                    if (decodeUrl.matches(AmanLink.URL.USER_LOGIN.toRegex()) || decodeUrl.matches(AmanLink.URL.USER_LOGIN_NEW.toRegex())) {
                        activity.checkLogin { if (it) loadUrl() }
                    } else if (decodeUrl.matches(AmanLink.URL.set_refresh_callback.toRegex())) {
                        refreshMethod = MeiUtil.getOneID(decodeUrl, AmanLink.URL.set_refresh_callback)
                    } else if (decodeUrl.startsWith(AmanLink.URL.find_page_height)) {
                        tabHeight = decodeUrl.parseValue("tabHeight").getInt(0)
                    } else if (NavLinkHandler.handLink(activity ?: context, url)) {

                    } else if (isWebAppLink(url)) {
                        Nav.toAmanLink(activity, decodeUrl)
                    }
                    return true
                }

                override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                    super.onPageStarted(view, url, favicon)
                    isContentLoaded = false
                    if (first) {
                        showLoading(true)
                    }
                    if (isAdded) find_advisory_empty_layout.isVisible = false
                }

                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
                    isContentLoaded = true
                    if (first) {
                        first = false
                        showLoading(false)
                    }
                    if (isAdded) {
                        find_advisory_swipe_refresh.isRefreshing = false
                    }
                }

                override fun handleLoadError() {
                    super.handleLoadError()
                    if (isAdded) find_advisory_empty_layout.isVisible = false
                }
            }
        }
        find_advisory_web_view.setScrollViewCallbacks(object : ObservableScrollViewCallbacks {
            override fun onUpOrCancelMotionEvent(scrollState: ScrollState?) {}

            override fun onDownMotionEvent() {}

            override fun onScrollChanged(scrollY: Int, firstScroll: Boolean, dragging: Boolean) {
                val heightLimit = 2 * tabHeight
                val value = min(scrollY, heightLimit)
                val contentAlpha = value * 1f / heightLimit
                find_advisory_content_tab_rv.isVisible = contentAlpha > 0.2f || find_advisory_web_view.isScrollToBottom()
                if (find_advisory_web_view.isScrollToBottom()) {
                    find_advisory_content_tab_rv.alpha = 1f
                } else {
                    find_advisory_content_tab_rv.alpha = contentAlpha
                }
            }
        })

        if (item?.needLoadFirst == 1) {
            loadUrl()
        }

        find_advisory_empty_layout.setBtnClickListener(View.OnClickListener {
            loadUrl()
        })
    }

    override fun willAppear() {
        super.willAppear()
        if (!isContentLoaded) {
            loadUrl()
        }
    }

    private fun initContentView() {
        find_advisory_content_tab_rv.adapter = contentTabAdapter
        GravitySnapHelper(Gravity.START).apply { snapToPadding = true }.attachToRecyclerView(find_advisory_content_tab_rv)
    }

    private fun requestData(type: String) {
        apiSpiceMgr.executeKt(FindCourseTabRequest(type), success = {
            if (!it.data?.partitions.isNullOrEmpty()) {
                notifyTitle(it.data.partitions)
            }
        })
    }

    private fun notifyTitle(infoList: List<FindCourseTab.TabInfo>) {
        mData.addAll(infoList)
        contentTabAdapter.notifyDataSetChanged()
    }

    private fun loadUrl() {
        if (item?.link?.isNotEmpty() == true) {
            find_advisory_web_view.loadUrl(MeiUtil.appendGeneraUrl(item?.link))
        } else {
            setViewVisibleOrGone(true, find_advisory_empty_layout)
        }
    }

    override fun onRefresh() {
        if (isAdded) {
            if (refreshMethod.isNotEmpty()) {
                find_advisory_web_view.loadUrl("javascript:${refreshMethod}()")
                find_advisory_swipe_refresh.isRefreshing = false
            } else {
                find_advisory_web_view.reload()
            }
        }
    }

    override fun onSelect() {
    }

    override fun onReSelect() {
        if (isOnDoubleClick() && isAdded) {
            find_advisory_web_view.scrollTo(0, 0)
        }
    }

    override fun onDeSelect() {
    }

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun netChange(info: NetInfo) {
        if (info.netChangeToAvailable() && find_advisory_empty_layout.isVisible) {
            loadUrl()
        }
    }

    /**
     * 切换fragment时通知web
     */
    override fun tabChanged() {
        find_advisory_web_view.loadUrl("javascript:showTabPage()")
    }

    override fun getContentTab() = mData[contentTabAdapter.selectedPos].partitionName
}