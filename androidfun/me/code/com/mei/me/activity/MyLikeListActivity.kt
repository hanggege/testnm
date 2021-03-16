package com.mei.me.activity

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.core.view.isVisible
import com.mei.me.adapter.MyLikeListAdapter
import com.mei.me.adapter.MyLikeListDecorator
import com.mei.orc.imageload.glide.glideDisplay
import com.mei.orc.util.startactivity.startFragmentForResult
import com.mei.video.ShortVideoPlayerNewActivityLauncher
import com.mei.wood.R
import com.mei.wood.extensions.executeKt
import com.mei.wood.ui.MeiCustomBarActivity
import com.net.model.chick.room.RoomList
import com.net.network.chick.video.LikeVideoListRequest
import kotlinx.android.synthetic.main.activity_my_like_list.*

/**
 * MyLikeListActivity
 *
 * @author kun
 * @version 1.0.0
 * @since 2020-08-17
 */
class MyLikeListActivity : MeiCustomBarActivity() {

    private var hasMore = true
    private var nextPageNo = 1

    private val mData = arrayListOf<RoomList.ShortVideoItemBean>()
    private val mAdapter by lazy {
        MyLikeListAdapter(mData).apply {
            setOnItemClickListener { _, _, position ->
                val intent = ShortVideoPlayerNewActivityLauncher.getIntentFrom(activity, mData[position].seqId.orEmpty(), "", true, mData[position].videoCoverUrl, "detail_like")
                startFragmentForResult(intent) { _, _ ->
                    my_like_rv.postDelayed({
                        nextPageNo = 1
                        requestData()
                    }, 1000)
                }
            }
            loadMoreModule.setOnLoadMoreListener {
                requestData()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_like_list)

        initView()

        requestData()
    }

    private fun initView() {
        title = "我赞过的"
        my_like_rv.adapter = mAdapter
        my_like_rv.addItemDecoration(MyLikeListDecorator())
        my_like_network_unavailable.setBtnClickListener(View.OnClickListener {
            requestData()
        })
    }

    private fun requestData() {
        showLoading(true)
        apiSpiceMgr.executeKt(LikeVideoListRequest(nextPageNo, 20), success = {
            if (it.isSuccess) {
                hasMore = it.data.hasMore
                if (!it.data.likeList.isNullOrEmpty()) {
                    my_like_empty_view.isVisible = false
                    my_like_network_unavailable.isVisible = false
                    mAdapter.loadMoreModule.isEnableLoadMore = true
                    refreshListData(it.data.likeList)
                    nextPageNo = it.data.nextPageNo
                } else if (!hasMore) {
                    my_like_empty_view.setEmptyText(it.data.noContentTipsText.orEmpty())
                    my_like_empty_view.findViewById<ImageView>(R.id.empty_image)?.glideDisplay(it.data.noContentTipsImage.orEmpty())
                    my_like_empty_view.isVisible = true
                    my_like_network_unavailable.isVisible = false
                }
            } else {
                mAdapter.loadMoreModule.loadMoreFail()
            }
        }, failure = {
            my_like_empty_view.isVisible = false
            my_like_network_unavailable.isVisible = true
        }, finish = {
            showLoading(false)
        })
    }

    private fun refreshListData(list: List<RoomList.ShortVideoItemBean>) {
        if (nextPageNo == 1) {
            mData.clear()
        }
        mData.addAll(list)

        if (mData.size == list.size) {
            mAdapter.notifyDataSetChanged()
        } else {
            mAdapter.notifyItemRangeInserted(mData.size - list.size, list.size)
        }

        if (hasMore) {
            mAdapter.loadMoreModule.loadMoreComplete()
        } else {
            mAdapter.loadMoreModule.loadMoreEnd()
        }
    }

}