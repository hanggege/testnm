package com.mei.live.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mei.live.ui.VideoLiveRoomActivity
import com.mei.wood.R
import com.mei.wood.extensions.executeToastKt
import com.mei.wood.ui.CustomSupportFragment
import com.net.LoadType
import com.net.model.room.RoomUserQueue
import com.net.model.room.RoomUserQueue.RoomUserQueueItem
import com.net.model.room.RoomUserTypeEnum
import com.net.network.room.RoomUserQueueRequest
import com.net.network.room.SwitchStatusRequest
import kotlinx.android.synthetic.main.fragment_apply_up_mic.*

/**
 * Created by hang on 2020/8/17.
 */
abstract class BaseUserListFragment : CustomSupportFragment() {

    var roomId: String = ""
    var callBack: () -> Unit = {}
    var roomUserType: RoomUserTypeEnum = RoomUserTypeEnum.ROOM_APPLY_USER
    var emptyTips = ""
    var userListQueue: RoomUserQueue? = null
    var hasApplySwitch = false

    private val mList = arrayListOf<RoomUserQueueItem>()
    private val mAdapter by lazy {
        getListAdapter(mList).apply {
            loadMoreModule.setOnLoadMoreListener {
                requestData(LoadType.load_more)
            }
        }
    }

    private var pageNo = 1

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_apply_up_mic, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
        val userQueue = userListQueue
        if (userQueue == null || userQueue.userItems.isNullOrEmpty()) {
            requestData()
        } else {
            mList.addAll(userQueue.userItems)
            successLoadStatus(userQueue.hasMore, userQueue.nextPageNo)
            mAdapter.notifyDataSetChanged()
        }
    }

    private fun initView() {
        up_mic_recycler.adapter = mAdapter
        switch_button.isChecked = (activity as? VideoLiveRoomActivity)?.roomInfo?.applyUpstreamEnable
                ?: true
        switch_layout.isVisible = hasApplySwitch

        switch_button.setOnCheckedChangeListener { _, isChecked ->
            showLoading(true)
            apiSpiceMgr.executeToastKt(SwitchStatusRequest(roomId, isChecked), success = {
                if (!it.isSuccess) {
                    switch_button.setChecked(!isChecked, false)
                }
            }, failure = {
                switch_button.setChecked(!isChecked, false)
            }, finish = {
                showLoading(false)
            })
        }
    }

    fun requestData(type: LoadType = LoadType.First) {
        if(type == LoadType.First) {
            pageNo = 1
            showLoading(true)
        }
        if(type == LoadType.pull_refresh){
            pageNo = 1
        }

        apiSpiceMgr.executeToastKt(RoomUserQueueRequest(roomId, roomUserType, pageNo), success = {
            val data = it.data
            if (it.isSuccess && data != null) {
                if (pageNo == 1) {
                    mList.clear()
                }
                mList.addAll(data.userItems.orEmpty())
                successLoadStatus(data.hasMore, data.nextPageNo)

                mAdapter.notifyDataSetChanged()
            } else {
                mAdapter.loadMoreModule.loadMoreFail()
            }
            empty_text.isVisible = mList.size == 0
            empty_text.text = emptyTips
        }, failure = {
            empty_text.isVisible = mList.size == 0
            empty_text.text = "加载失败"
            mAdapter.loadMoreModule.loadMoreFail()
        }, finish = {
            showLoading(false)
        })

    }

    private fun successLoadStatus(hasMore: Boolean, nextPageNo: Int) {
        mAdapter.loadMoreModule.isEnableLoadMore = mList.size > 5
        if (hasMore) {
            mAdapter.loadMoreModule.loadMoreComplete()
            //加载成功后如果还有下一页
            pageNo = nextPageNo
        } else {
            mAdapter.loadMoreModule.loadMoreEnd()
        }
    }

    abstract fun getListAdapter(list: MutableList<RoomUserQueueItem>): BaseQuickAdapter<RoomUserQueueItem, BaseViewHolder>

}