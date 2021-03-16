package com.mei.widget.recyclerview.observable

import android.content.Context
import android.content.res.Configuration
import android.util.AttributeSet

import com.github.ksoichiro.android.observablescrollview.ObservableWebView
import kotlin.math.abs

/**
 * @author Created by Ling on 2019-09-20
 * Android5.1.1 Lollipop (API 22)上会有Caused by: android.content.res.Resources$NotFoundException: String resource ID #0x2040003
 * 通过一下修复
 */
open class ObservableInitWebView : ObservableWebView {
    constructor(context: Context) : super(getFixedContext(context))

    constructor(context: Context, attrs: AttributeSet) : super(getFixedContext(context), attrs)

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(getFixedContext(context), attrs, defStyle)

    companion object {

        fun getFixedContext(context: Context): Context {
            return context.createConfigurationContext(Configuration())
        }
    }

    /**
     * 是否滑动到底部了
     */
    @Suppress("DEPRECATION")
    fun isScrollToBottom(): Boolean {
        val contentHeight = contentHeight * scale
        val currHeight = height + scrollY
        return abs(contentHeight - currHeight) < 20
    }
}
