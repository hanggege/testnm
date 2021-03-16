package com.mei.intimate.view

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.MotionEvent
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.github.rubensousa.gravitysnaphelper.GravitySnapHelper
import com.mei.GrowingUtil
import com.mei.base.ui.nav.Nav
import com.mei.orc.ext.layoutInflaterKtToParentAttach
import com.mei.orc.util.click.setOnClickNotDoubleListener
import com.mei.wood.R
import com.mei.wood.util.logDebug
import com.net.model.chick.room.RoomList
import kotlinx.android.synthetic.main.view_intimate_title.view.*
import kotlin.math.abs

/**
 * IntimateTitleView
 *
 * @author kun
 * @version 1.0.0
 * @since 2020-08-20
 */
class IntimateTitleView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
        ConstraintLayout(context, attrs, defStyleAttr) {

    private var mData: RoomList.ShortVideoTitle? = null

    private val mTagList = arrayListOf<RoomList.ShortVideoItemBean.VideoTag>()
    private val mTagAdapter by lazy {
        VideoTagAdapter(mTagList).apply {
            setOnItemClickListener { _, _, position ->
                GrowingUtil.track("main_app_gn_use_data",
                        "screen_name", "首页",
                        "main_app_gn_type", "短视频",
                        "main_app_gn_label", "顶部标签",
                        "position", "",
                        "time_stamp", (System.currentTimeMillis()/ com.mei.orc.unit.TimeUnit.SECOND).toString())
                Nav.toAmanLink(context, mTagList[position].action.orEmpty())
            }
        }
    }

    init {
        layoutInflaterKtToParentAttach(R.layout.view_intimate_title)
        val layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
        layoutManager.initialPrefetchItemCount = 5
        title_tag_rv.adapter = mTagAdapter
        title_tag_rv.layoutManager = layoutManager
        GravitySnapHelper(Gravity.START).attachToRecyclerView(title_tag_rv)

        title_more.setOnClickNotDoubleListener(tag = "video_tag") {
            Nav.toAmanLink(context, mData?.more?.action.orEmpty())
        }
        title_arrow.setOnClickNotDoubleListener(tag = "video_tag") {
            Nav.toAmanLink(context, mData?.more?.action.orEmpty())
        }
        title_tag_rv.addOnItemTouchListener(object : RecyclerView.OnItemTouchListener {
            override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {
                when (e.action) {
                    MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> rv.parent.requestDisallowInterceptTouchEvent(true)
                    MotionEvent.ACTION_UP -> rv.parent.requestDisallowInterceptTouchEvent(false)
                }
            }

            var startX = 0f
            var startY = 0f
            override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
                var intercept = false
                val linearLayoutManager = title_tag_rv.layoutManager as LinearLayoutManager
                val lastPosition = linearLayoutManager.findLastCompletelyVisibleItemPosition()
                val firstPosition = linearLayoutManager.findFirstCompletelyVisibleItemPosition()
                when (e.action) {
                    MotionEvent.ACTION_DOWN -> {
                        startX = e.x
                        startY = e.y
                    }
                    MotionEvent.ACTION_MOVE -> {
                        val offsetX = e.x - startX
                        val offsetY = e.y - startY
                        logDebug("onInterceptTouchEvent", "offsetX:$offsetX ----lastPosition$lastPosition ---- firstPosition:$firstPosition ")
                        intercept = (offsetX < 0 && lastPosition == mTagList.size - 1) || (offsetX > 0 && firstPosition == 0) || abs(offsetY) > abs(offsetX)
                    }
                }
                return intercept
            }

            override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {

            }

        })
    }

    fun convertData(data: RoomList.ShortVideoTitle?) {
        mData = data
        title.text = data?.title.orEmpty()
        title_more.text = data?.more?.text.orEmpty()
        refreshTagList(data?.tags)
    }

    private fun refreshTagList(list: List<RoomList.ShortVideoItemBean.VideoTag>?) {
        if (list.isNullOrEmpty()) return
        mTagList.clear()
        mTagList.addAll(list)
        mTagAdapter.notifyDataSetChanged()
    }

    inner class VideoTagAdapter(list: MutableList<RoomList.ShortVideoItemBean.VideoTag>) : BaseQuickAdapter<RoomList.ShortVideoItemBean.VideoTag, BaseViewHolder>(R.layout.item_intimate_video_tag, list) {
        override fun convert(holder: BaseViewHolder, item: RoomList.ShortVideoItemBean.VideoTag) {
            holder.setText(R.id.tag_video_text, item.name)
        }
    }
}