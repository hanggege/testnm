package com.mei.intimate.view

import android.content.Context
import android.graphics.Bitmap
import android.webkit.WebSettings
import android.webkit.WebView
import android.widget.FrameLayout
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import com.mei.base.common.RESET_WEB_VERSION
import com.mei.base.ui.nav.Nav
import com.mei.base.util.web.initWebViewSetting
import com.mei.base.util.web.setWebChromeClient
import com.mei.orc.event.postAction
import com.mei.orc.ext.dip
import com.mei.orc.ext.layoutInflaterKtToParentAttach
import com.mei.orc.util.string.decodeUrl
import com.mei.wood.R
import com.mei.wood.web.MelkorWebViewClient
import com.net.model.chick.room.RoomList
import kotlinx.android.synthetic.main.view_intimate_head.view.*

/**
 * Created by hang on 2020/9/1.
 */
class IntimateHeadView(context: Context) : FrameLayout(context) {

    private var version = -1
    private var seqId = -1

    init {
        layoutInflaterKtToParentAttach(R.layout.view_intimate_head)

        initWebView()
    }

    private fun initWebView() {
        intimate_head_web.settings.apply {
            javaScriptCanOpenWindowsAutomatically = true
            useWideViewPort = true
            loadWithOverviewMode = true
            cacheMode = WebSettings.LOAD_NO_CACHE
        }
        intimate_head_web.apply {
            initWebViewSetting()
            setWebChromeClient()
            isHorizontalScrollBarEnabled = false
            isVerticalScrollBarEnabled = false
            webViewClient = object : MelkorWebViewClient() {
                override fun handleUrlLoading(view: WebView, url: String): Boolean {
                    val decodeUrl = url.decodeUrl(true)
                    when {
                        isWebAppLink(url) -> {
                            Nav.toAmanLink(context, decodeUrl)
                        }
                        else -> {
                            return false
                        }
                    }
                    return true
                }

                override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                    super.onPageStarted(view, url, favicon)
                }

                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
                    isVisible = true
                    intimate_head_web.settings.loadsImagesAutomatically = true
                }

                override fun handleLoadError() {
                    super.handleLoadError()
                    isVisible = false
                    postAction(RESET_WEB_VERSION, Pair(seqId, -1))
                }
            }
        }
    }

    fun convertData(data: RoomList.HomePageNavigateBar?) {
        if (data != null) {
            if (data.version > version) {
                intimate_head_web.updateLayoutParams {
                    height = dip(data.height)
                }
                intimate_head_web.settings.loadsImagesAutomatically = false

                if (!data.url.isNullOrEmpty()) {
                    intimate_head_web.loadUrl(data.url)
                } else if (!data.html.isNullOrEmpty()) {
                    intimate_head_web.loadDataWithBaseURL("", data.html, "text/html", "UTF-8", null)
                }

                version = data.version
                seqId = data.seqId
            }
        }
    }
}