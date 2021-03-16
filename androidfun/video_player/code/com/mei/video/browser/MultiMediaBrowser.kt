package com.mei.video.browser

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import androidx.core.view.forEach
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mei.browser.view.PagerRecyclerView
import com.mei.video.browser.adapter.MultiMediaAdapter
import com.mei.video.browser.adapter.MultiMediaData
import com.mei.video.browser.adapter.MultiMediaRefresh

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2020/7/23
 */
class MultiMediaBrowser @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
    : FrameLayout(context, attrs, defStyleAttr) {

    val dataList = arrayListOf<MultiMediaData>()
    val mediaAdapter: MultiMediaAdapter by lazy { MultiMediaAdapter(dataList) }
    val pagerRecycler: PagerRecyclerView by lazy {
        PagerRecyclerView(context).apply {
            overScrollMode = View.OVER_SCROLL_NEVER
            layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
            adapter = mediaAdapter
            setOnViewPagerListener { old, new, holder ->
                forEach { (it as? MultiMediaRefresh)?.performSelected(new) }
                pagerSelected(old, new, holder)
            }
        }
    }
    val currentPosition: Int
        get() = pagerRecycler.currentPosition
    private var pagerSelected: (old: Int, new: Int, holder: RecyclerView.ViewHolder?) -> Unit = { _, _, _ -> }

    fun setOnViewPagerListener(pagerSelected: (old: Int, new: Int, holder: RecyclerView.ViewHolder?) -> Unit) {
        this.pagerSelected = pagerSelected
    }

    init {
        addView(pagerRecycler, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT))

    }


    fun resetData(list: List<MultiMediaData>, index: Int = 0) {
        dataList.clear()
        dataList.addAll(list)
        val initPosition = if (index in 0 until dataList.size) index else 0
        mediaAdapter.initPosition = initPosition
        mediaAdapter.notifyDataSetChanged()
        pagerRecycler.scrollToPosition(initPosition)
    }

    fun removeAt(index: Int) {
        if (index.checkRange()) {
            dataList.removeAt(index)
            val position = if (index > dataList.size - 1) {
                dataList.size - 1
            } else index
            mediaAdapter.initPosition = position
            mediaAdapter.notifyItemRemoved(index)
            pagerRecycler.scrollToPosition(position)
            mediaAdapter.notifyItemChanged(position)
            if ((position + 1).checkRange()) mediaAdapter.notifyItemChanged(position + 1)
            if ((position - 1).checkRange()) mediaAdapter.notifyItemChanged(position - 1)
        }
    }

    fun addElement(data: MultiMediaData, index: Int = -1) {
        if (index.checkRange()) {
            dataList.add(index, data)
            mediaAdapter.notifyItemInserted(index)
        } else {
            dataList.add(data)
            mediaAdapter.notifyItemInserted(dataList.size - 1)
        }
    }

    private fun Int.checkRange() = this in 0 until mediaAdapter.itemCount

}