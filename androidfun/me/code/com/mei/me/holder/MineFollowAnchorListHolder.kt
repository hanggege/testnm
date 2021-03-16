package com.mei.me.holder

import android.view.Gravity
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.rubensousa.gravitysnaphelper.GravitySnapHelper
import com.mei.base.baseadapter.multi.MultiViewHolder
import com.mei.live.LiveEnterType
import com.mei.live.ui.VideoLiveRoomActivityLauncher
import com.mei.me.adapter.MeFollowItemDecoration
import com.mei.me.adapter.MeFollowListAdapter
import com.mei.wood.R
import com.net.model.chick.friends.MyFollowListBean

/**
 * Created by hang on 2020/7/24.
 */
@Suppress("UNCHECKED_CAST")
class MineFollowAnchorListHolder(view: View) : MultiViewHolder(view) {

    private val followList = arrayListOf<MyFollowListBean>()
    private val followAdapter by lazy {
        MeFollowListAdapter(followList).apply {
            setOnItemClickListener { adapter, _, position ->
                (adapter.getItemOrNull(position) as? MyFollowListBean)?.let {
                    VideoLiveRoomActivityLauncher.startActivity(itemView.context, it.roomInfo?.roomId.orEmpty(), LiveEnterType.tab_me_normal_enter)
                }
            }
        }
    }

    init {
        getView<RecyclerView>(R.id.recycler_view).apply {
            layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
            adapter = followAdapter
            addItemDecoration(MeFollowItemDecoration())
            GravitySnapHelper(Gravity.START).attachToRecyclerView(this)
        }
    }

    override fun refresh(data: Any?) {
        if (data is MineFollowAnchorListData) {
            followList.clear()
            followList.addAll(data.list)
            followAdapter.notifyDataSetChanged()
        }
    }
}

class MineFollowAnchorListData(val list: MutableList<MyFollowListBean>)