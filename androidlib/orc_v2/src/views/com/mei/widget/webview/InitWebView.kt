package com.mei.widget.webview

import android.content.Context
import android.content.res.Configuration
import android.util.AttributeSet
import android.webkit.WebView


/**
 * Created by zzw on 2019-09-24
 * Des:Android5.1.1 Lollipop (API 22)上会有Caused by: android.content.res.Resources$NotFoundException: String resource ID #0x2040003
 */

open class InitWebView : WebView {
    constructor(context: Context) : super(getFixedContext(context))

    constructor(context: Context, attrs: AttributeSet) : super(getFixedContext(context), attrs)

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(getFixedContext(context), attrs, defStyle)

    companion object {

        fun getFixedContext(context: Context): Context {
            return context.createConfigurationContext(Configuration())
        }
    }
}
