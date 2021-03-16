package com.mei.base.weight.recyclerview

import android.content.Context

import android.util.AttributeSet
import android.view.View
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView

import com.mei.widget.recyclerview.observable.ObservableRecyclerView

/**
 * Created by LingYun on 2017/8/16.
 * 备注：可设置为空界面的RecyclerView
 * 当使用Adapter继承RecyclerView.Adapter<RecyclerView.ViewHolder>()时可用，继承BaseQuickAdapter时推荐其自己的setEmptyView
</RecyclerView.ViewHolder> */
class EmptyRecyclerView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0) : ObservableRecyclerView(context, attrs, defStyle) {

    private var emptyView: View? = null

    private val observer = object : RecyclerView.AdapterDataObserver() {
        override fun onChanged() {
            checkIfEmpty()
        }

        override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
            checkIfEmpty()
        }

        override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
            checkIfEmpty()
        }
    }


    private fun checkIfEmpty() {
        val emptyViewVisible = adapter?.itemCount == 0
        emptyView?.isVisible = emptyViewVisible
        isVisible = !emptyViewVisible
    }

    override fun setAdapter(adapter: RecyclerView.Adapter<*>?) {
        val oldAdapter = getAdapter()
        oldAdapter?.unregisterAdapterDataObserver(observer)
        super.setAdapter(adapter)
        adapter?.registerAdapterDataObserver(observer)
        checkIfEmpty()
    }

    fun setEmptyView(emptyView: View) {
        this.emptyView = emptyView
        checkIfEmpty()
    }
}
