package com.mei.wood.dialog.share.activity

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.webkit.WebSettings
import android.webkit.WebView
import androidx.core.view.isVisible
import com.mei.base.network.netchange.NetWorkListener
import com.mei.base.network.netchange.netChangeToAvailable
import com.mei.base.ui.nav.Nav
import com.mei.base.util.web.initWebViewSetting
import com.mei.base.util.web.setWebChromeClientSupportFile
import com.mei.orc.net.NetInfo
import com.mei.orc.util.click.setOnClickNotDoubleListener
import com.mei.wood.R
import com.mei.wood.dialog.share.ShareData
import com.mei.wood.dialog.share.ShareManager
import com.mei.wood.ext.AmanLink
import com.mei.wood.ui.MeiCustomActivity
import com.mei.wood.util.MeiUtil
import com.mei.wood.web.MelkorWebViewClient
import kotlinx.android.synthetic.main.activity_share_web.*
import kotlinx.android.synthetic.main.include_net_error_layout.*

/**
 * Created by LingYun on 2017/6/22.
 * 带有分享按钮的web页面
 */
class ShareWebActivity : MeiCustomActivity(), NetWorkListener {

    var mTitle = ""
    var mContent = ""
    var mImgUrl = ""
    var mShareUrl = ""
    var amanLink = ""

    private var netWorkIsErr = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_share_web)
        top_share.isVisible = false
        readIntent()
        initView()
    }

    private fun initView() {
        top_title.text = mTitle
        top_share.setOnClickNotDoubleListener { shareToThird() }
        top_back.setOnClickListener {
            finish()
        }
        net_error_layout.setOnBtnClick(View.OnClickListener {
            tryAgain()
        })
    }

    private fun tryAgain() {
        showLoading(true)
        web_view.reload()
    }

    private fun shareToThird() {
        val data = ShareData(mTitle, mContent, mImgUrl, mShareUrl, amanLink)
        ShareManager.instance.show(this, data)
    }

    @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
    private fun readIntent() {
        showLoading(true)
        val dataUrl = intent.dataString.orEmpty()
        if (dataUrl.isNotEmpty()) {
            val data = MeiUtil.getJsonObject<SHARE_WEBVIEW_DATA>(dataUrl, AmanLink.URL.SHARE_WEBVIEW)
            amanLink = dataUrl
            mTitle = data?.title ?: ""
            mShareUrl = data?.url ?: ""
            mImgUrl = data?.img_url ?: ""
            mContent = data?.content ?: ""
        }
        if (!TextUtils.isEmpty(mShareUrl)) {
            top_share.isVisible = true
            val loadUrl = MeiUtil.appendGeneraUrl(mShareUrl)
            setWebView(loadUrl)
        } else {
            showLoading(false)
            finish()
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setWebView(loadUrl: String) {
        web_view.initWebViewSetting()
        web_view.loadUrl(loadUrl)

        val client = object : MelkorWebViewClient() {
            override fun handleUrlLoading(view: WebView, url: String): Boolean {
                when {
                    url.matches(AmanLink.URL.HAS_FINISHED_LOAD_WEB.toRegex()) -> showLoading(false)
                    isAppLink(url) -> Nav.toAmanLink(activity, url)
                    else -> return false
                }
                return true
            }

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                showLoading(true)
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                if (!netWorkIsErr) net_error_layout.isVisible = true else netWorkIsErr = false
                showLoading(false)
            }

            @Suppress("DEPRECATION", "OverridingDeprecatedMember")
            override fun onReceivedError(view: WebView?, errorCode: Int, description: String?, failingUrl: String?) {
                super.onReceivedError(view, errorCode, description, failingUrl)
                netWorkIsErr = true
                net_error_layout.isVisible = true
            }
        }
        web_view.webViewClient = client
        web_view.setWebChromeClientSupportFile(this)
    }

    override fun netChange(netInfo: NetInfo) {
        if (netInfo.netChangeToAvailable() && net_error_layout.isVisible) {
            tryAgain()
        }
        val hasNet = netInfo.currTye != NetInfo.NetType.NONE
        web_view.settings.cacheMode = if (hasNet) WebSettings.LOAD_DEFAULT else WebSettings.LOAD_CACHE_ONLY
    }
}