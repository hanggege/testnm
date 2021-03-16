package com.mei.live.ui

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Looper
import android.view.ViewGroup
import android.webkit.WebSettings
import android.webkit.WebView
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentActivity
import com.mei.base.util.web.initWebViewSetting
import com.mei.base.weight.loading.LoadingCreator
import com.mei.orc.ActivityLauncher
import com.mei.orc.ext.getStatusBarHeight
import com.mei.orc.ext.px2dip
import com.mei.orc.ext.transparentStatusBar
import com.mei.wood.R
import com.mei.wood.dialog.NormalDialogLauncher
import com.mei.wood.ext.AmanLink
import com.mei.wood.util.MeiUtil
import com.mei.wood.util.parseValue
import com.mei.wood.web.MelkorWebViewClient
import com.mei.wood.web.OpenFileWebChromeClient
import kotlinx.android.synthetic.main.activity_level_webview.*
import kotlinx.android.synthetic.main.include_net_error_layout.*
import launcher.Boom

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2020/4/20
 */
@Deprecated("等级已经转用通用web页面展示，目前已经没有用到，使用内链跳转，本地的链接已经没用")
class LevelWebViewActivity : FragmentActivity() {
    @Boom
    var url: String = ""

    /**web内部页码**/
    var index: Int = 0

    /**web内部有改变，退出需要弹框提示**/
    private var webHasChange: Boolean = false
    private val loadingView: LoadingCreator by lazy { LoadingCreator(provideOverView()) }

    /**清除WebView的历史记录，当需要清除时需要赋值true*/
    private var clearHistory = false

    @Suppress("NON_FINAL_MEMBER_IN_FINAL_CLASS")
    open fun provideOverView(): ViewGroup? = findViewById(android.R.id.content) as? ViewGroup
    var first = true

    private val openFileClient: OpenFileWebChromeClient by lazy { OpenFileWebChromeClient(this) }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ActivityLauncher.bind(this)
        transparentStatusBar()
        setContentView(R.layout.activity_level_webview)
        initWebView()
    }

    private fun initWebView() {
        webview.initWebViewSetting()
        WebView.setWebContentsDebuggingEnabled(true)
        webview.settings.apply {
            javaScriptCanOpenWindowsAutomatically = true
            useWideViewPort = true
            loadWithOverviewMode = true
            domStorageEnabled = true
            cacheMode = WebSettings.LOAD_NO_CACHE
        }

        webview.webChromeClient = openFileClient
        webview.webViewClient = object : MelkorWebViewClient() {
            override fun handleLoadError() {
                super.handleLoadError()
                net_error_layout.isVisible = true

            }

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                if (first) {
                    showLoadingCover()
                } else {
                    showLoading(true)
                }

                net_error_layout.isGone = true
            }

            override fun handleUrlLoading(view: WebView?, url: String): Boolean {
                return when {
                    url.startsWith(AmanLink.URL.LOAD_FINISH) -> {
                        val type = url.parseValue("type", 0)
                        if (type == 2) {
                            //webView加载完数据会通知
                            webview.loadUrl("javaScript:setBarStatusHeight(${px2dip(getStatusBarHeight()).toInt()})")
                        }
                        true
                    }
                    else -> handleLink(url)
                }

            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                if (first) {
                    webview?.postDelayed({
                        loadFinish()
                        first = false
                    }, 500)
                } else {
                    loadFinish()
                }


            }
        }
        webview.loadUrl(MeiUtil.appendGeneraUrl(url))
    }

    override fun onBackPressed() {
        when {
            webHasChange -> NormalDialogLauncher.newInstance().showDialog(this, "填写内容未提示,是否退出？", okBack = { finish() })
            index > 0 -> {
                if (net_error_layout.isGone) webview.loadUrl("javascript:goBack()")
            }
            webview.canGoBack() -> webview.goBack()
            else -> super.onBackPressed()
        }
    }


    private fun loadFinish() {
        if (!isFinishing) {
            showLoading(false)
            if (clearHistory) {
                webview.clearHistory()
                clearHistory = false
            }
        }
    }

    fun showLoading(show: Boolean) {
        if (Thread.currentThread() == Looper.getMainLooper().thread) {
            loadingView.showLoading(show)
        } else {
            runOnUiThread { loadingView.showLoading(show) }
        }
    }

    fun showLoadingCover() {
        if (Thread.currentThread() == Looper.getMainLooper().thread) {
            loadingView.showLoadingCover()
        } else {
            runOnUiThread { loadingView.showLoadingCover() }
        }
    }

    fun handleLink(url: String): Boolean {
        if (url.matches(AmanLink.URL.close_webview.toRegex())) {
            finish()
        }
        return true
    }
}

