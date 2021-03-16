package com.mei.orc.activity

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.core.view.doOnNextLayout
import androidx.core.view.isVisible
import com.jaeger.library.StatusBarUtil
import com.mei.widget.actionbar.MeiActionBar
import com.trello.rxlifecycle3.components.support.RxAppCompatActivity


/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2018/9/12
 * 带ActionBar的基类
 */
abstract class MeiBaseBarActivity : RxAppCompatActivity() {
    val meiTitleBar: MeiActionBar by lazy {
        MeiActionBar(this).apply {
            setBackgroundColor(Color.WHITE)
            leftContainer.setOnClickListener { onClickLeft(it) }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            if (showActionBar()) {
                supportActionBar?.let {
                    it.apply {
                        elevation = 0f
                        setDisplayShowHomeEnabled(false)
                        setDisplayShowCustomEnabled(true)
                        setDisplayShowTitleEnabled(false)
                        customView = customActionBar()
                        (customView?.parent as? Toolbar)?.setContentInsetsAbsolute(0, 0)
                    }
                }
                setTitle(title)
            } else {
                supportActionBar?.hide()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    override fun setContentView(layoutResID: Int) {
        super.setContentView(layoutResID)
        StatusBarUtil.setColor(this, Color.WHITE)
    }

    override fun setContentView(view: View?) {
        super.setContentView(view)
        StatusBarUtil.setColor(this, Color.WHITE)
    }

    override fun setTitle(title: CharSequence?) {
        super.setTitle(title)
        if (meiTitleBar.centerContainer.childCount == 1) {
            (meiTitleBar.centerContainer.getChildAt(0) as? TextView)?.text = title
        }
    }


    /**
     *是否需要隐藏顶部通用title
     * 下面所以 的title操作都是依赖于当前返回false
     */
    open fun showActionBar(): Boolean = true

    /***
     * 依赖[showActionBar]为true时有效 完全自定义头部
     */
    open fun customActionBar(): View {
        return meiTitleBar
    }

    open fun onClickLeft(view: View) {
        finish()
    }

    open fun showBottomLine(show: Boolean) {
        meiTitleBar.bottomLine.isVisible = show
    }

    /**
     * 自定义左中右的布局  -1 左 0 中 1 右
     */
    open fun setCustomView(view: View, position: Int) {
        val container = when (position) {
            -1 -> meiTitleBar.leftContainer
            0 -> meiTitleBar.centerContainer
            1 -> meiTitleBar.rightContainer
            else -> null
        }
        container?.let {
            it.removeAllViews()
            it.addView(view)
            it.doOnNextLayout { meiTitleBar.updateCenterMargin() }
        }

    }

}