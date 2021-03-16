package com.mei.base.util.web

import android.annotation.SuppressLint
import android.os.Build
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import androidx.fragment.app.FragmentActivity
import com.mei.orc.ext.screenHeight
import com.mei.orc.ext.screenWidth
import com.mei.provider.ProjectExt
import com.mei.wood.BuildConfig
import com.mei.wood.web.OpenFileWebChromeClient

/**
 *
 * @author Created by Ling on 2019-07-22
 * web帮助
 */

@SuppressLint("SetJavaScriptEnabled")
fun WebView.initWebViewSetting() {
    settings.apply {
        javaScriptEnabled = true
        textZoom = 100
        userAgentString = (settings.userAgentString + ProjectExt.WEBVIEWUserAgent + BuildConfig.VERSION_NAME
                + " screen-size:" + screenWidth + "*" + screenHeight)

        //https情况下资源不加载的处理
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        }
    }
}

fun WebView.setWebChromeClient() {
    try {
        webChromeClient = WebChromeClient()
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun WebView.setOpenFileWebChromeClient(context: FragmentActivity): OpenFileWebChromeClient {
    val client = OpenFileWebChromeClient(context)
    webChromeClient = client
    return client
}

fun WebView.setWebChromeClientSupportFile(activity: FragmentActivity) {
    webChromeClient = OpenFileWebChromeClient(activity)
}
