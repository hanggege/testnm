package com.mei.intimate

import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.mei.base.common.SHORT_VIDEO_LIST_REFRESH
import com.mei.intimate.adapter.ShortVideoListAdapter
import com.mei.orc.ActivityLauncher
import com.mei.orc.event.bindAction
import com.mei.orc.ext.dip
import com.mei.orc.util.json.json2Obj
import com.mei.wood.R
import com.mei.wood.extensions.executeToastKt
import com.mei.wood.ui.fragment.LazyLoadFragment
import com.net.LoadType
import com.net.model.chick.room.RoomList
import com.net.model.chick.video.ShortVideoList
import com.net.network.chick.video.ShortVideoListRequest
import kotlinx.android.synthetic.main.fragment_short_video_list.*
import launcher.Boom
import launcher.MulField

/**
 * Created by hang on 2020/7/14.
 */
class ShortVideoListFragment : LazyLoadFragment() {

    @Boom
    var tagId = 0

    @MulField
    @Boom
    var videoListStr = ""

    @MulField
    @Boom
    var fromType = "广场"

    private val videoList = arrayListOf<RoomList.ShortVideoItemBean>()

    private val adapter by lazy {
        ShortVideoListAdapter(videoList).apply {
            tagId = this@ShortVideoListFragment.tagId
            this.statFromType = fromType
        }
    }

    private val itemDecoration = object : RecyclerView.ItemDecoration() {

        override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
            when ((view.layoutParams as? StaggeredGridLayoutManager.LayoutParams)?.spanIndex) {
                0 -> outRect.set(dip(5), dip(5), dip(2.5f), 0)
                1 -> outRect.set(dip(2.5f), dip(5), dip(5), 0)
                else -> outRect.set(dip(2.5f), dip(2.5f), dip(5), dip(2.5f))
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_short_video_list, container, false)

    override fun lazyLoadData() {
        val videoList = videoListStr.json2Obj(ShortVideoList.ShortVideoItem::class.java)
        if (videoList != null && videoList.list.orEmpty().isNotEmpty()) {
            adapter.setList(videoList.list)
            adapter.loadMoreModule.isEnableLoadMore = videoList.list.size > 0

            if (videoList.hasMore) {
                adapter.loadMoreModule.loadMoreComplete()
                //加载成功后如果还有下一页
                pageNo = videoList.nextPageNo
            } else {
                adapter.loadMoreModule.loadMoreEnd()
            }
        } else {
            requestData(LoadType.First)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ActivityLauncher.bind(this)

        short_video_list.adapter = adapter
        short_video_list.addItemDecoration(itemDecoration)

        swipe_refresh.setOnRefreshListener {
            refreshData()
        }
        adapter.loadMoreModule.setOnLoadMoreListener {
            requestData(LoadType.load_more)
        }
        bindAction<Int>(SHORT_VIDEO_LIST_REFRESH) { targetTagId ->
            if (tagId == targetTagId) {
                showLoading(true)
                refreshData()
                short_video_list.layoutManager?.scrollToPosition(0)
            }
        }
    }

    private fun refreshData() {
        pageNo = 1
        requestData(LoadType.pull_refresh)
    }

    private var pageNo = 1
    private fun requestData(type: LoadType) {
        if (type == LoadType.First) {
            showLoading(true)
        }
        apiSpiceMgr.executeToastKt(ShortVideoListRequest(tagId, pageNo), success = {
            val videos = it.data?.videos
            if (it.isSuccess && videos != null) {
                if (pageNo == 1) {
                    videoList.clear()
                }
                videoList.addAll(videos.list.orEmpty())
                adapter.loadMoreModule.isEnableLoadMore = videoList.size > 0

                if (videoList.size == videos.list.size) {
                    adapter.notifyDataSetChanged()
                } else {
                    adapter.notifyItemRangeInserted(videoList.size - videos.list.size, videos.list.size)
                }

                if (videos.hasMore) {
                    adapter.loadMoreModule.loadMoreComplete()
                    //加载成功后如果还有下一页
                    pageNo = videos.nextPageNo
                } else {
                    adapter.loadMoreModule.loadMoreEnd()
                }
            } else {
                adapter.loadMoreModule.loadMoreFail()
            }
            empty_view.setEmptyImage(R.drawable.short_video_empty_page)
                    .setEmptyText("这里空空的")
                    .setBtnVisible(false)

            empty_view.isVisible = videoList.isEmpty()
        }, failure = {
            empty_view.setEmptyImage(R.drawable.home_error_page)
                    .setEmptyText("网络开小差了")
                    .setBtnVisible(true)
                    .setBtnClickListener(View.OnClickListener {
                        pageNo = 1
                        requestData(LoadType.First)
                    })
            if (pageNo > 1) {
                adapter.loadMoreModule.loadMoreFail()
            }
            empty_view.isVisible = videoList.isEmpty()
        }, finish = {
            showLoading(false)
            swipe_refresh.isRefreshing = false
        })
    }

}