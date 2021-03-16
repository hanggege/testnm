package com.mei.find.ui

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebSettings
import android.webkit.WebView
import androidx.core.view.isVisible
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.mei.base.common.CHANG_LOGIN
import com.mei.base.network.netchange.NetWorkListener
import com.mei.base.network.netchange.netChangeToAvailable
import com.mei.base.ui.nav.Nav
import com.mei.base.ui.nav.NavLinkHandler.handLink
import com.mei.base.util.web.initWebViewSetting
import com.mei.base.util.web.setWebChromeClient
import com.mei.login.checkLogin
import com.mei.orc.ActivityLauncher
import com.mei.orc.event.bindAction
import com.mei.orc.net.NetInfo
import com.mei.orc.tab.TabItemContent
import com.mei.orc.util.click.isOnDoubleClick
import com.mei.orc.util.string.decodeUrl
import com.mei.orc.util.ui.setViewVisibleOrGone
import com.mei.wood.R
import com.mei.wood.ext.AmanLink
import com.mei.wood.ui.fragment.TabItemFragment
import com.mei.wood.util.MeiUtil
import com.mei.wood.util.parseValue
import com.mei.wood.web.MelkorWebViewClient
import com.net.model.chick.tab.TabItem
import kotlinx.android.synthetic.main.find_course_layout.*
import launcher.Boom

/**
 * FindCourseFragment
 *
 * @author kun
 * @version 1.0.0
 * @since 2020-08-13
 */
class FindCourseFragment : TabItemFragment(), NetWorkListener, TabItemContent, SwipeRefreshLayout.OnRefreshListener {

    @Boom
    var item: TabItem? = null

    var contentTabIndex = -1 //记录webview中的tab index

    private var first = true

    private var refreshMethod = ""

    private var isContentLoaded = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        ActivityLauncher.bind(this)
        return inflater.inflate(R.layout.find_course_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()

        bindAction<Boolean>(CHANG_LOGIN) {
            loadUrl()
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    fun initView() {
        find_course_swipe_refresh.setOnRefreshListener(this)
        find_course_web_view.settings.apply {
            javaScriptCanOpenWindowsAutomatically = true
            useWideViewPort = true
            loadWithOverviewMode = true
            cacheMode = WebSettings.LOAD_NO_CACHE
        }
        find_course_web_view.apply {
            initWebViewSetting()
            setWebChromeClient()
            webViewClient = object : MelkorWebViewClient() {
                override fun handleUrlLoading(view: WebView, url: String): Boolean {
                    val decodeUrl = url.decodeUrl(true)
                    if (decodeUrl.matches(AmanLink.URL.USER_LOGIN.toRegex()) || decodeUrl.matches(AmanLink.URL.USER_LOGIN_NEW.toRegex())) {
                        activity.checkLogin { if (it) loadUrl() }
                    } else if (decodeUrl.matches(AmanLink.URL.set_refresh_callback.toRegex())) {
                        refreshMethod = MeiUtil.getOneID(decodeUrl, AmanLink.URL.set_refresh_callback)
                    } else if (decodeUrl.startsWith(AmanLink.URL.find_content_tab)) {
                        val contentTabIndex = decodeUrl.parseValue("index")
                        if (contentTabIndex.isNotEmpty()) this@FindCourseFragment.contentTabIndex = contentTabIndex.toInt()
                        switchCourseContentTab(contentTabIndex)
                    } else if (handLink(activity ?: context, url)) {

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
                    if (isAdded) find_course_empty_layout.isVisible = false
                }

                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
                    isContentLoaded = true
                    if (first) {
                        first = false
                        showLoading(false)
                    }
                    if (isAdded) {
                        find_course_swipe_refresh.isRefreshing = false
                        if (contentTabIndex != -1) selectContentTab()
                    }
                }

                override fun handleLoadError() {
                    super.handleLoadError()
                    if (isAdded) find_course_empty_layout.isVisible = false
                }
            }
        }


        if (item?.needLoadFirst == 1) {
            loadUrl()
        }

        find_course_empty_layout.setBtnClickListener(View.OnClickListener {
            loadUrl()
        })
    }

    override fun willAppear() {
        super.willAppear()
        if (!isContentLoaded) {
            loadUrl()
        }
    }

    private fun switchCourseContentTab(index: String) {
        activity.let {
            if (it is FindCourseActivity) {
                it.modifiedFragmentContentLink(index)
            }
        }
    }

    private fun loadUrl() {
        if (item?.link?.isNotEmpty() == true) {
            find_course_web_view.loadUrl(MeiUtil.appendGeneraUrl(item?.link))
        } else {
            setViewVisibleOrGone(true, find_course_empty_layout)
        }
    }

    override fun onRefresh() {
        if (isAdded) {
            if (refreshMethod.isNotEmpty()) {
                find_course_web_view.loadUrl("javascript:${refreshMethod}()")
                find_course_swipe_refresh.isRefreshing = false
            } else {
                find_course_web_view.reload()
            }
        }
    }

    override fun onSelect() {
    }

    override fun onReSelect() {
        if (isOnDoubleClick() && isAdded) {
            find_course_web_view.scrollTo(0, 0)
        }
    }

    override fun onDeSelect() {
    }

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun netChange(info: NetInfo) {
        if (info.netChangeToAvailable() && find_course_empty_layout.isVisible) {
            loadUrl()
        }
    }

    /**
     * 切换webview里的tab
     */
    fun selectContentTab(index: Int = contentTabIndex) {
        if (isAdded) {
            find_course_web_view.loadUrl("javascript:tabsChange($index)")
        }
    }

    fun eventTracking(title: String) {
        if (isAdded) {
            find_course_web_view.loadUrl("javascript:gioPartitionName(${title})")
        }
    }
}