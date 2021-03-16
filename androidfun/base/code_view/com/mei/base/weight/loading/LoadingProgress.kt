package com.mei.base.weight.loading

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.RelativeLayout
import androidx.annotation.StringRes
import androidx.core.view.isVisible
import com.mei.wood.R
import kotlinx.android.synthetic.main.footer_no_more_layout.view.*

/**
 * Created by Ling on 2018/6/6.
 *
 * @ 描述："加载中..."和"没有更多了"的加载提示
 */
class LoadingProgress @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : RelativeLayout(context, attrs, defStyleAttr) {

    private var mLoadedText: String? = null
    private var mLoadingText: String? = null

    init {
        LayoutInflater.from(context).inflate(R.layout.footer_no_more_layout, this)
    }

    fun setOnLoading() {
        loading_container.isVisible = true
        progress.isVisible = true
        loaded_container.isVisible = false
        if (mLoadingText.isNullOrEmpty()) {
            load_on.setText(R.string.pull_loading)
        } else {
            load_on.text = mLoadingText
        }
    }

    fun setOnLoadError() {
        loading_container.isVisible = true
        progress.isVisible = false
        loaded_container.isVisible = false
        load_on.text = "点击查看更多"
    }

    fun setOnLoaded() {
        loaded_container.isVisible = true
        loading_container.isVisible = false
    }

    fun setLoadedText(text: String) {
        mLoadedText = text
    }

    fun setLoadingText(text: String) {
        mLoadingText = text
    }

    fun setLoadingOrLoaded(loading: Boolean) {
        if (loading) {
            setOnLoading()
        } else {
            setOnLoaded()
        }
    }

    fun setLoadedText(@StringRes textId: Int) {
        mLoadedText = resources.getString(textId)
    }

    fun setLoadingText(@StringRes textId: Int) {
        mLoadingText = resources.getString(textId)
    }
}
