package com.mei.wood.ui.web

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebSettings
import android.webkit.WebView
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.mei.base.common.CHANG_LOGIN
import com.mei.base.network.netchange.NetWorkListener
import com.mei.base.network.netchange.netChangeToAvailable
import com.mei.base.ui.nav.Nav
import com.mei.base.util.web.initWebViewSetting
import com.mei.base.util.web.setWebChromeClient
import com.mei.login.checkLogin
import com.mei.orc.event.bindAction
import com.mei.orc.ext.dip
import com.mei.orc.ext.getStatusBarHeight
import com.mei.orc.net.NetInfo
import com.mei.orc.tab.TabItemContent
import com.mei.orc.util.click.isOnDoubleClick
import com.mei.orc.util.string.decodeUrl
import com.mei.orc.util.ui.setViewGone
import com.mei.orc.util.ui.setViewVisibleOrGone
import com.mei.wood.R
import com.mei.wood.ext.AmanLink
import com.mei.wood.ui.fragment.TabItemFragment
import com.mei.wood.util.MeiUtil
import com.mei.wood.web.MelkorWebViewClient
import com.net.model.chick.tab.TabItem
import kotlinx.android.synthetic.main.wap_tab_layout.*

@SuppressLint("ValidFragment")
/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2018/8/1
 */
abstract class BaseWebFragment : TabItemFragment(), NetWorkListener,
        TabItemContent, SwipeRefreshLayout.OnRefreshListener {

    private var mIsRefreshPart = false

    abstract var item: TabItem?

    private var first = true

    private var refreshMethod = ""


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.wap_tab_layout, container, false)
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
        swipe_refresh.setOnRefreshListener(this)
        reload_btn.setOnClickListener { loadUrl() }
        web_view.settings.apply {
            javaScriptCanOpenWindowsAutomatically = true
            useWideViewPort = true
            loadWithOverviewMode = true
            cacheMode = WebSettings.LOAD_NO_CACHE
        }
        web_view.apply {
            initWebViewSetting()
            setWebChromeClient()
            webViewClient = object : MelkorWebViewClient() {
                override fun handleUrlLoading(view: WebView, url: String): Boolean {
                    val decodeUrl = url.decodeUrl(true)
                    if (decodeUrl.matches(AmanLink.URL.USER_LOGIN.toRegex()) || decodeUrl.matches(AmanLink.URL.USER_LOGIN_NEW.toRegex())) {
                        activity.checkLogin { if (it) loadUrl() }
                    } else if (decodeUrl.matches(AmanLink.URL.set_refresh_callback.toRegex())) {
                        refreshMethod = MeiUtil.getOneID(decodeUrl, AmanLink.URL.set_refresh_callback)
                    } else if (isWebAppLink(url)) {
                        Nav.toAmanLink(activity, decodeUrl)
                    } else {
                        return false
                    }
                    return true
                }

                override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                    super.onPageStarted(view, url, favicon)
                    if (first) {
                        showLoading(true)
                    }
                    if (isAdded) {
                        setViewGone(true, empty_layout)
                    }
                }

                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
                    if (first) {
                        first = false
                        showLoading(false)
                    }
                    if (isAdded) {
                        swipe_refresh.isRefreshing = false
                    }
                }

                override fun handleLoadError() {
                    super.handleLoadError()
                    if (isAdded) {
                        setViewVisibleOrGone(true, empty_layout)
                    }
                }
            }
        }

        item?.let {
            web_title.text = it.linkTitle
        }
        top_layout.isVisible = !item?.linkTitle.isNullOrEmpty()
        loadUrl()

        empty_image.updateLayoutParams<ViewGroup.MarginLayoutParams> {
            activity?.let {
                topMargin = dip(123) - getStatusBarHeight()
            }
        }
    }

    override fun willAppear() {
        super.willAppear()
        web_view.loadUrl("javascript:isRunTask(1)")
    }

    override fun willHidden() {
        super.willHidden()
        web_view.loadUrl("javascript:isRunTask(0)")
    }

    fun loadUrl() {
        if (item?.link?.isNotEmpty() == true) {
            web_view.loadUrl(MeiUtil.appendGeneraUrl(item?.link))
        } else {
            setViewVisibleOrGone(true, empty_layout)
        }
    }

    override fun onRefresh() {
        if (isAdded) {
            if (refreshMethod.isNotEmpty()) {
                web_view.loadUrl("javascript:${refreshMethod}()")
                swipe_refresh.isRefreshing = false
            } else {
                web_view.reload()
            }
        }
    }


    override fun onSelect() {
    }

    override fun onReSelect() {
        if (isOnDoubleClick() && isAdded) {
            web_view.scrollTo(0, 0)
        }
    }

    override fun onDeSelect() {
    }

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun netChange(info: NetInfo) {
        if (info.netChangeToAvailable() && empty_layout.isVisible) {
            loadUrl()
        }
    }
}