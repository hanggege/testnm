package com.mei.wood.ui

import android.os.Looper
import android.view.ViewGroup
import com.mei.base.network.holder.SpiceHolder
import com.mei.base.network.holder.SpiceHolderImp
import com.mei.base.weight.loading.LoadingCreator
import com.mei.orc.ext.runOnUiThread
import com.mei.orc.http.retrofit.RetrofitClient
import com.mei.wood.rx.MeiUiFrame
import com.trello.rxlifecycle3.components.support.RxFragment

/**
 * Created by guoyufeng on 22/5/15.
 */
abstract class CustomSupportFragment : RxFragment(), MeiUiFrame {
    val loadingView: LoadingCreator by lazy { LoadingCreator(provideOverView()) }

    private val spiceHolder: SpiceHolder by lazy { SpiceHolderImp() }

    /**
     * 提供需要loading的父view
     */
    @Suppress("MemberVisibilityCanBePrivate")
    open fun provideOverView(): ViewGroup? {
        return view as? ViewGroup
    }

    override fun showLoading(show: Boolean) {
        if (Thread.currentThread() == Looper.getMainLooper().thread && isAdded) {
            loadingView.showLoading(show)
        } else {
            runOnUiThread {
                if (isAdded) {
                    loadingView.showLoading(show)
                }
            }
        }
    }

    override fun showLoadingCover() {
        if (Thread.currentThread() == Looper.getMainLooper().thread && isAdded) {
            loadingView.showLoadingCover()
        } else {
            runOnUiThread {
                if (isAdded) {
                    loadingView.showLoadingCover()
                }
            }
        }
    }

    override fun getApiSpiceMgr(): RetrofitClient = spiceHolder.apiSpiceMgr

    override fun recycleManager() {
        spiceHolder.recycleManager()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        recycleManager()
    }

}
