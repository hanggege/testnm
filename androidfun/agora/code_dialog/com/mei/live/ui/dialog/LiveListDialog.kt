package com.mei.live.ui.dialog

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.FragmentActivity
import androidx.viewpager.widget.ViewPager
import com.joker.im.custom.CustomType
import com.joker.im.custom.chick.ChickCustomData
import com.joker.im.listener.IMAllEventManager
import com.joker.im.mapToMeiMessage
import com.joker.im.message.CustomMessage
import com.joker.im.registered
import com.joker.im.unregistered
import com.mei.base.weight.fragmentpager.CustomFragmentPagerAdapter
import com.mei.live.ui.fragment.ApplyUpMicFragment
import com.mei.live.ui.fragment.BaseUserListFragment
import com.mei.live.ui.fragment.OnLineUserListFragment
import com.mei.orc.dialog.BottomDialogFragment
import com.mei.wood.R
import com.mei.wood.extensions.executeToastKt
import com.mei.wood.ui.MeiCustomBarActivity
import com.net.LoadType
import com.net.model.room.RoomUserQueue
import com.net.model.room.RoomUserTypeEnum
import com.net.network.room.RoomUserQueueRequest
import com.tencent.imsdk.TIMMessage
import kotlinx.android.synthetic.main.view_live_list.*

/**
 *  Created by zzw on 2019-11-29
 *  Des: 申请/在线列表
 */
fun FragmentActivity.showLiveListDialog(roomId: String,
                                        type: RoomUserTypeEnum = RoomUserTypeEnum.ROOM_APPLY_USER,
                                        back: (BottomDialogFragment) -> Unit = {},
                                        dismiss: () -> Unit = {}) {
    val activity = this as? MeiCustomBarActivity
    activity?.showLoading(true)
    activity?.apiSpiceMgr?.executeToastKt(RoomUserQueueRequest(roomId, type), success = {
        if (it.isSuccess && it.data != null) {
            val liveListDialog = PayDiLiveListDialog().apply {
                this.back = dismiss
                this.roomId = roomId
                roomUserQueue = it.data
                this.type = type
            }
            liveListDialog.show(supportFragmentManager, "PayDiLiveListDialog")
            back(liveListDialog)
        }
    }, finish = {
        activity.showLoading(false)
    })
}

class PayDiLiveListDialog : BottomDialogFragment() {
    var back: () -> Unit = {}
    var roomId: String = ""
    var roomUserQueue: RoomUserQueue? = null
    var type: RoomUserTypeEnum = RoomUserTypeEnum.ROOM_APPLY_USER

    private val fragmentList = arrayListOf<BaseUserListFragment>()

    private val checkNewEvent: IMAllEventManager by lazy {
        object : IMAllEventManager() {
            /** 监听最新消息 **/
            override fun onNewMessages(msgs: MutableList<TIMMessage>?): Boolean {
                val customList = msgs.orEmpty()
                        .mapNotNull { it.mapToMeiMessage() as? CustomMessage }
                        .filter { !it.isDeleted() }//去掉已删除的消息

                /** 只接收群消息与发送指令的消息 **/
                customList.filter { it.peer == roomId }
                        .forEach { handleIMEvent(it) }
                return super.onNewMessages(msgs)
            }
        }
    }

    private fun handleIMEvent(msg: CustomMessage) {
        val type = msg.customMsgType
        (msg.customInfo?.data as? ChickCustomData)?.also { data ->
            when (type) {
                CustomType.people_changed -> {
                    if (data.reason == 1) {
                        fragmentList.getOrNull(slide_tab_layout.currentTab)?.requestData(LoadType.pull_refresh)
                    }
                }
                CustomType.room_reject_up -> {
                    fragmentList.getOrNull(slide_tab_layout.currentTab)?.requestData(LoadType.pull_refresh)
                }
                else -> {
                }
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.view_live_list, container, false)
        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        return view
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkNewEvent.registered()

        val tabList = roomUserQueue?.tabs.orEmpty()
        if (tabList.isNotEmpty()) {
            var selectIndex = -1

            fragmentList.clear()
            tabList.forEachIndexed { index, roomUserTabItem ->
                val fragment = if (roomUserTabItem.tabTypeName == RoomUserTypeEnum.ROOM_APPLY_USER.type) {
                    ApplyUpMicFragment().apply {
                        roomId = this@PayDiLiveListDialog.roomId
                        hasApplySwitch = roomUserTabItem.hasApplySwitch
                        callBack = { dismissAllowingStateLoss() }
                    }
                } else {
                    OnLineUserListFragment().apply {
                        roomId = this@PayDiLiveListDialog.roomId
                        roomUserType = RoomUserTypeEnum.parseValue(roomUserTabItem.tabTypeName)
                        hasApplySwitch = roomUserTabItem.hasApplySwitch
                        applyOption = roomUserQueue?.applyOption
                    }
                }
                if (roomUserTabItem.tabTypeName == type.type) {
                    fragment.userListQueue = roomUserQueue
                    selectIndex = index
                }
                fragment.emptyTips = roomUserTabItem.emptyTips.orEmpty()
                fragmentList.add(fragment)
            }
            val pagerAdapter = CustomFragmentPagerAdapter(childFragmentManager).apply {
                setFragmentsAndTitles(fragmentList, tabList.map { it.tabName })
            }
            view_pager.adapter = pagerAdapter
            slide_tab_layout.setViewPager(view_pager)

            slide_tab_layout.currentTab = if (selectIndex > -1) selectIndex else 0

            view_pager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
                override fun onPageScrollStateChanged(state: Int) {
                }

                override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                }

                override fun onPageSelected(position: Int) {
                    fragmentList.getOrNull(position)?.requestData(LoadType.pull_refresh)
                }
            })
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        checkNewEvent.unregistered()
        back()
    }
}


