package com.mei.video.browser.view

import android.content.Context
import android.content.ContextWrapper
import android.graphics.Rect
import android.util.AttributeSet
import android.util.SparseBooleanArray
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import androidx.core.view.doOnLayout
import androidx.core.view.doOnNextLayout
import androidx.core.view.forEach
import androidx.core.view.updateLayoutParams
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mei.base.common.PRAISE_STATE
import com.mei.browser.view.PagerRecyclerView
import com.mei.orc.event.bindAction
import com.mei.video.browser.adapter.ShortVideoPlayerAdapter
import com.mei.video.browser.adapter.ShortVideoRefresh
import com.mei.wood.util.logDebug
import com.net.model.chick.video.ShortVideoInfo
import kotlin.math.abs

/**
 *
 * @author Created by lenna on 2020/8/18
 */
class ShortVideoPlayBrowser @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
    : FrameLayout(context, attrs, defStyleAttr) {
    val data = arrayListOf<ShortVideoInfo.VideoInfo>()
    val currentPosition: Int
        get() = pagerRecycler.currentPosition

    private var mPagerSelected: (old: Int, new: Int, holder: RecyclerView.ViewHolder?) -> Unit = { _, _, _ -> }

    private val videoAdapter by lazy {
        ShortVideoPlayerAdapter(data).apply {
            praiseArray = this@ShortVideoPlayBrowser.praiseArray
        }
    }
    val pagerRecycler: PagerRecyclerView by lazy {
        PagerRecyclerView(context).apply {
            overScrollMode = View.OVER_SCROLL_NEVER
            adapter = videoAdapter
            layoutManager = object : LinearLayoutManager(context) {
                override fun requestChildRectangleOnScreen(parent: RecyclerView, child: View, rect: Rect, immediate: Boolean, focusedChildVisible: Boolean): Boolean {
                    return false
                }
            }
            setOnViewPagerListener { oldPosition, newPosition, holder ->
                forEach {
                    (it as? ShortVideoRefresh)?.performSelected(newPosition)
                }
                mPagerSelected(oldPosition, newPosition, holder)
            }

            addOnItemTouchListener(object : RecyclerView.OnItemTouchListener {
                var startX = 0f
                var startY = 0f
                override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {
                    when (e.action) {
                        MotionEvent.ACTION_DOWN -> {
                            startX = e.x
                            startY = e.y
                        }
                        MotionEvent.ACTION_MOVE -> {
                            val offsetX = abs(e.x - startX)
                            val offsetY = abs(e.y - startY)
                            if (offsetY > offsetX) {
                                rv.parent.requestDisallowInterceptTouchEvent(true)
                            }
                        }
                    }
                }

                override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
                    return false
                }

                override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {

                }

            })
        }
    }

    fun setOnViewPagerListener(pagerSelected: (old: Int, new: Int, holder: RecyclerView.ViewHolder?) -> Unit) {
        this.mPagerSelected = pagerSelected
    }

    val praiseArray = SparseBooleanArray()

    init {
        addView(pagerRecycler, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT))
        this.doOnNextLayout {
            (pagerRecycler.adapter as? ShortVideoPlayerAdapter)?.preHeight = it.measuredHeight
            pagerRecycler.updateLayoutParams<MarginLayoutParams> {
                height = this@ShortVideoPlayBrowser.measuredHeight + 2
                topMargin = -1
            }
        }

        val lifecycleOwner = ((context as? LifecycleOwner)
                ?: ((context as? ContextWrapper)?.baseContext as? LifecycleOwner))
        lifecycleOwner?.bindAction<Pair<Int, Boolean>>(PRAISE_STATE) {
            it?.let {
                praiseArray.put(it.first, it.second)
            }
        }
    }

    /**
     * 重置视频数据
     */
    fun resetVideoData(list: List<ShortVideoInfo.VideoInfo>, index: Int = 0) {
        logDebug("resetVideoData", measuredHeight.toString())
        doOnLayout {
            data.clear()
            data.addAll(list)

            val initPosition = if (index > 0) index else 0
            videoAdapter.initPosition = initPosition
            videoAdapter.preHeight = this.measuredHeight
            if (initPosition > 0) {
                pagerRecycler.scrollToPosition(initPosition)
            }
            videoAdapter.notifyDataSetChanged()
        }
    }

    /**
     *  添加短视频数据
     */
    fun addDataList(list: List<ShortVideoInfo.VideoInfo>) {
        data.addAll(list)

        videoAdapter.notifyItemRangeInserted(data.size - list.size, list.size)
    }
    /**获取当前item数据*/
    fun getCurrentItemData(): ShortVideoInfo.VideoInfo? {
        if (data.size > 0) {
            val targetPosition = currentPosition % data.size
            if (targetPosition.checkData()) {
                return data[targetPosition]
            }
        }
        return null
    }

    /**播放视频*/
    fun playVideo() {
        pagerRecycler.apply {
            forEach {
                if ((it as? ShortVideoView)?.isPositionSelected == true) {
                    (it as? ShortVideoRefresh)?.playVideo()
                }
            }
        }
    }

    /**暂停视频*/
    fun pauseVideo() {
        pagerRecycler.apply {
            forEach {
                if ((it as? ShortVideoView)?.isPositionSelected == true) {
                    (it as? ShortVideoRefresh)?.pauseVideo()
                }
            }

        }
    }

    fun scrollToPositionNextOrReplay() {
        if (getCurrentItemData()?.canAutoNext == true) {
            /**播放下一个*/
            if (data.size > 0) {
                val targetPosition = currentPosition + 1 % data.size
                if (targetPosition.checkData()) {
                    pagerRecycler.scrollToPosition(targetPosition)
                }
            }
        }
    }

    private fun Int.checkData() = videoAdapter.let { this in 0 until it.itemCount }
}