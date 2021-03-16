package com.mei.wood.ui

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Looper
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.gyf.immersionbar.ImmersionBar
import com.joker.support.WeixinEvent
import com.joker.support.listener.TdHandlerListener
import com.joker.support.listener.TdLifecycleListener
import com.mei.base.network.holder.SpiceHolderImp
import com.mei.base.weight.loading.LoadingCreator
import com.mei.orc.activity.MeiBaseBarActivity
import com.mei.orc.ext.dip
import com.mei.orc.http.retrofit.RetrofitClient
import com.mei.wood.R
import com.mei.wood.rx.MeiUiFrame
import java.util.*

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2018/9/25
 */
abstract class MeiCustomBarActivity : MeiBaseBarActivity(), TdHandlerListener, MeiUiFrame {
    private val spiceHolder by lazy { SpiceHolderImp() }
    val loadingView: LoadingCreator by lazy { LoadingCreator(provideOverView()) }
    private val lifeLists = ArrayList<TdLifecycleListener>()

    /**
     * 提供需要loading的父view
     */
    @Suppress("MemberVisibilityCanBePrivate")
    open fun provideOverView(): ViewGroup? = findViewById(android.R.id.content) as? ViewGroup

    override fun showActionBar(): Boolean = true

    /**
     * 是否自定义状态栏
     */
    open fun customStatusBar() = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        meiTitleBar.leftContainer.removeAllViews()
        meiTitleBar.leftContainer.addView(ImageView(this).apply {
            setPadding(dip(20), dip(16), dip(20), dip(16))
            setImageResource(R.drawable.bg_black_back_arrow)
        })
    }

    override fun setContentView(layoutResID: Int) {
        super.setContentView(layoutResID)
        if (!customStatusBar())
            ImmersionBar.with(this).apply {
                statusBarColorInt(Color.WHITE)
                statusBarDarkFont(true)
                fitsSystemWindows(true)
                supportActionBar(showActionBar())
            }.init()

    }

    override fun setContentView(view: View?) {
        super.setContentView(view)
        if (!customStatusBar())
            ImmersionBar.with(this).apply {
                statusBarColorInt(Color.WHITE)
                statusBarDarkFont(true)
                fitsSystemWindows(true)
                supportActionBar(showActionBar())
            }.init()
    }

    @Suppress("DEPRECATION")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        runUI {
            synchronized(lifeLists) {
                if (lifeLists.size > 0) {
                    for (life in lifeLists) {
                        life.onActivityResult(requestCode, resultCode, data)
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        recycleManager()
    }

    override fun registerLifecycle(listener: TdLifecycleListener) {
        runUI {
            synchronized(lifeLists) {
                WeixinEvent.getInstance().clear()
                if (!lifeLists.contains(listener))
                    lifeLists.add(listener)
            }
        }
    }

    override fun unregisterLifecycle(listener: TdLifecycleListener) {
        runUI {
            synchronized(lifeLists) {
                lifeLists.remove(listener)
            }
        }
    }

    override fun isRegisted(listener: TdLifecycleListener): Boolean {
        synchronized(lifeLists) {
            return lifeLists.contains(listener)
        }
    }


    override fun getActivity(): MeiBaseBarActivity {
        return this
    }

    override fun showLoading(show: Boolean) {
        if (Thread.currentThread() == Looper.getMainLooper().thread) {
            loadingView.showLoading(show)
        } else {
            runUI { loadingView.showLoading(show) }
        }
    }

    override fun showLoadingCover() {
        if (Thread.currentThread() == Looper.getMainLooper().thread) {
            loadingView.showLoadingCover()
        } else {
            runUI { loadingView.showLoadingCover() }
        }
    }

    override fun getApiSpiceMgr(): RetrofitClient {
        return spiceHolder.getApiSpiceMgr()
    }

    override fun recycleManager() {
        spiceHolder.recycleManager()
    }

    private fun runUI(run: () -> Unit) {
        runOnUiThread {
            try {
                run()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

}
