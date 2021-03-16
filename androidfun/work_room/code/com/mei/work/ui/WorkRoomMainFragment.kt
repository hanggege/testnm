package com.mei.work.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import com.joker.im.custom.chick.ServiceInfo
import com.mei.orc.ActivityLauncher
import com.mei.orc.ext.dip
import com.mei.wood.R
import com.mei.wood.extensions.executeToastKt
import com.mei.wood.ui.CustomSupportFragment
import com.mei.work.adapter.WorkRoomAdapter
import com.mei.work.adapter.item.WorkRoomAvatarData
import com.mei.work.adapter.item.WorkRoomLiveInfo
import com.net.model.chick.friends.UserHomePagerResult
import com.net.model.chick.recommend.BatRoomStatusResult
import com.net.network.chick.friends.UserHomePagerRequest
import kotlinx.android.synthetic.main.activity_work_room_main_toolbar.*
import launcher.Boom
import launcher.MulField

/**
 * WorkRoomUserActivity
 *
 * @author kun
 * @version 1.0.0
 * @since 2020-07-23
 */

class WorkRoomMainFragment : CustomSupportFragment() {


    @Boom
    var mUserId: Int = 0

    @Boom
    @MulField
    var mSelectPosition = 0   //默认选择tab

    var pagerResult = UserHomePagerResult()
    val maxOffset: Int by lazy { dip(160) }

    val smoothScroller: LinearSmoothScroller by lazy {
        object : LinearSmoothScroller(context) {
            override fun getVerticalSnapPreference(): Int = SNAP_TO_START
            override fun calculateDyToMakeVisible(view: View?, snapPreference: Int): Int {
                return super.calculateDyToMakeVisible(view, snapPreference) + room_main_toolbar.measuredHeight
            }
        }

    }

    val mData = arrayListOf<Any>()
    val mAdapter by lazy {
        WorkRoomAdapter(mData).apply {
            setOnItemChildClickListener { _, _, _ ->
                //去IM
                room_main_toolbar_label_text.performClick()
            }
        }
    }
    val tabIndexList: MutableList<Int> = arrayListOf()
    private val dataObserver by lazy {
        object : RecyclerView.AdapterDataObserver() {
            override fun onChanged() {
                tabIndexList.apply {
                    clear()
                    add(0)
                    add(mData.indexOfFirst { it is WorkRoomAvatarData } - 1)
                    add(mData.indexOfFirst { it is ServiceInfo } - 1)
                    add(mData.indexOfFirst { it is WorkRoomLiveInfo } - 1)
                }
            }
        }
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        ActivityLauncher.bind(this)
        return inflater.inflate(R.layout.fragment_work_room_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initEvent()
        mAdapter.registerAdapterDataObserver(dataObserver)
        if (pagerResult.info?.userId != mUserId) loadUserInfo() else refreshData()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mAdapter.unregisterAdapterDataObserver(dataObserver)
    }

    fun loadUserInfo() {
        apiSpiceMgr.executeToastKt(UserHomePagerRequest(mUserId), success = {
            if (it.isSuccess && it.data != null) {
                pagerResult = it.data
                refreshData()
            }
        }, failure = {

        }, finish = {
            showLoading(false)
        })
    }
}