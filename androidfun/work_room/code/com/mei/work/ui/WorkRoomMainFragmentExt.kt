package com.mei.work.ui

import android.animation.ArgbEvaluator
import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import androidx.core.view.updatePadding
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.tabs.TabLayout
import com.joker.im.custom.chick.CourseInfo
import com.joker.im.custom.chick.ServiceInfo
import com.mei.base.common.COURSE_PAY_SUCCESS
import com.mei.base.common.WORK_ROOM_CHANGED
import com.mei.chat.toImChat
import com.mei.dialog.showSelectedReportReason
import com.mei.live.LiveEnterType
import com.mei.live.ui.VideoLiveRoomActivity
import com.mei.live.ui.VideoLiveRoomActivityLauncher
import com.mei.live.ui.ext.getCurrLiveState
import com.mei.orc.event.bindAction
import com.mei.orc.ext.getStatusBarHeight
import com.mei.orc.imageload.glide.glideDisplay
import com.mei.orc.ui.toast.UIToast
import com.mei.orc.util.click.setOnClickNotDoubleListener
import com.mei.wood.R
import com.mei.wood.cache.requestImUserInfo
import com.mei.wood.extensions.executeKt
import com.mei.wood.extensions.executeToastKt
import com.mei.wood.rx.ObserverSilentSubscriber
import com.mei.wood.util.AppManager
import com.mei.work.adapter.decoration.RoomMainDecoration
import com.mei.work.adapter.item.*
import com.net.MeiUser
import com.net.model.chick.friends.UserHomePagerResult
import com.net.model.chick.report.ReportBean
import com.net.model.chick.tab.tabbarConfig
import com.net.network.chick.recommend.BatRoomStatusRequest
import com.net.network.chick.report.ReportRequest
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.activity_work_room_main_footer.*
import kotlinx.android.synthetic.main.activity_work_room_main_toolbar.*
import kotlinx.android.synthetic.main.fragment_work_room_main.*
import java.util.concurrent.TimeUnit

/**
 * WorkRoomUserFragmentExt
 *
 * @author kun
 * @version 1.0.1
 * @since 2020-07-24
 */

// ---- 初始化视图 ----
fun WorkRoomMainFragment.initView() {
    room_main_toolbar.updatePadding(top = getStatusBarHeight())
    room_main_back_arrow.updateLayoutParams<ViewGroup.MarginLayoutParams> {
        topMargin = getStatusBarHeight()
    }
    initTabList()
    initRv()
}

fun WorkRoomMainFragment.initTabList(tabs: Array<String> = arrayOf("工作室介绍", "团队介绍", "服务课程", "收到的感谢")) {
    room_main_page_tab.removeAllTabs()
    room_main_page_tab.clearOnTabSelectedListeners()
    tabs.forEach { room_main_page_tab.addTab(room_main_page_tab.newTab().setText(it)) }
    room_main_page_tab.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
        override fun onTabReselected(tab: TabLayout.Tab) {
            clickSelectTab(tab)
        }

        override fun onTabUnselected(tab: TabLayout.Tab) {

        }

        override fun onTabSelected(tab: TabLayout.Tab) {
            clickSelectTab(tab)
        }
    })
}

fun WorkRoomMainFragment.initRv() {
    room_main_rv.adapter = mAdapter
    room_main_rv.addOnScrollListener(object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            if (isAdded) {
                val layoutManager = recyclerView.layoutManager
                if (layoutManager is GridLayoutManager && room_main_page_tab.tag != true) {
                    val lastCompleteItem = layoutManager.findLastCompletelyVisibleItemPosition()
                    if (lastCompleteItem == mAdapter.itemCount - 1) {
                        room_main_page_tab.setScrollPosition(room_main_page_tab.tabCount - 1, 0f, true)
                    } else {
                        val firstPosition = (0..layoutManager.childCount).mapNotNull { layoutManager.getChildAt(it) }
                                .filter { it.top < room_main_toolbar.bottom && it.bottom > room_main_toolbar.bottom }
                                .map { layoutManager.getPosition(it) }
                                .firstOrNull()
                                ?: layoutManager.findFirstCompletelyVisibleItemPosition()

                        /** 当前选中的tab **/
                        val tabSelected = (0 until room_main_page_tab.tabCount).mapNotNull {
                            room_main_page_tab.getTabAt(it)?.view
                        }.indexOfFirst { it.isSelected }
                        val targetPosition = getChangeTargetPosition(firstPosition)
                        if (tabSelected != targetPosition && targetPosition > -1) {
                            room_main_page_tab.setScrollPosition(targetPosition, 0f, true)
                        }
                    }
                }
                val firstPosition = (layoutManager as? LinearLayoutManager)?.findFirstVisibleItemPosition()
                        ?: 1
                val offset = recyclerView.computeVerticalScrollOffset()
                val fraction = if (offset > maxOffset || firstPosition > 0) 1f else offset.toFloat() / maxOffset
                val color = (ArgbEvaluator().evaluate(fraction, Color.TRANSPARENT, Color.WHITE) as? Int)
                room_main_toolbar.setBackgroundColor(color ?: Color.TRANSPARENT)
                room_main_toolbar.alpha = fraction
                room_main_toolbar.isVisible = fraction > 0.1f
            }
        }

        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if (isAdded) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    room_main_page_tab.tag = false
                }
            }
        }
    })
    room_main_rv.addItemDecoration(RoomMainDecoration(room_main_rv.context))
}

// ---- 初始化视图 ----


// ----- 初始化Listener -----
fun WorkRoomMainFragment.initEvent() {
    Observable.interval(0, 10, TimeUnit.SECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .compose(bindToLifecycle())
            .subscribe(object : ObserverSilentSubscriber<Long>() {
                override fun onNext(long: Long) {
                    apiSpiceMgr.executeKt(BatRoomStatusRequest(arrayListOf(mUserId)), success = { r ->
                        if (r.isSuccess) pagerResult.roomStatus = r.data?.get(pagerResult.info?.groupId.toString())
                        refreshLivingState()
                    })
                }
            })
    room_main_back_arrow.setOnClickListener { activity?.finish() }
    room_main_toolbar_back_arrow.setOnClickListener { room_main_back_arrow.performClick() }
    room_main_toolbar_menupoint.setOnClickListener {
        activity?.window?.attributes = activity?.window?.attributes?.apply { alpha = 0.4f }
        showReportPopWindow(it)
    }
    room_main_toolbar_living_label.setOnClickNotDoubleListener(tag = "work_room_live_id") {
        //跳转直播间
        when (getCurrLiveState()) {
            1 -> UIToast.toast("正在直播中，无法查看")
            2 -> UIToast.toast("正在上麦中，无法查看")
            else -> VideoLiveRoomActivityLauncher.startActivity(activity, pagerResult.info?.roomId, LiveEnterType.user_home_page)
        }
    }
    room_main_toolbar_label_text.setOnClickNotDoubleListener {
        if (MeiUser.getSharedUser().info?.groupRole ?: 0 > 0 || MeiUser.getSharedUser().info?.isPublisher == true) {
            UIToast.toast("暂不支持知心达人之间的私聊功能")
            return@setOnClickNotDoubleListener
        }
        pagerResult.bindPromoter?.let { info ->
            apiSpiceMgr.requestImUserInfo(arrayOf(info.userId), needRefresh = true, back = {
                val userInfo = it.firstOrNull()
                when {
                    userInfo?.isSelfToBlackList == true -> {
                        UIToast.toast(tabbarConfig.groupBlacklistUser)
                    }
                    userInfo?.isTheOtherToBlackList == true -> {
                        UIToast.toast(tabbarConfig.hasBlacklistTips)
                    }
                    else -> {
                        activity?.toImChat(info.userId.toString())
                    }
                }
            })
        }
    }
    bindAction<Boolean>(WORK_ROOM_CHANGED) {
        loadUserInfo()
    }
    bindAction<Boolean>(COURSE_PAY_SUCCESS) {
        loadUserInfo()
    }
}
// ---- 初始化视图 ----


// ---- 数据刷新 ----
fun WorkRoomMainFragment.refreshData() {
    mData.clear()
    pagerResult.apply {
        refreshToolbar(this)
        refreshFooter(bindPromoter)
        refreshHeader(this)
        mData.add(info ?: UserHomePagerResult.InfoBean()) // summary
        refreshConsultList(consultList, bindPromoter?.userId, isGroupMember)
        refreshAnalyst(promoterList, isGroupMember)
        refreshService(specialServices?.list, info?.groupId ?: 0, info?.roomId)
        refreshCourse(courses?.list, info?.groupId ?: 0, info?.roomId)
        refreshLiveInfo(WorkRoomLiveInfo(receivedCoinCount, fansCount, broadcastCount))
        refreshGift(receiveGift)
        mData.add(Footer())
        if (specialServices?.list.orEmpty().isEmpty() && courses?.list.orEmpty().isEmpty()) {
            initTabList(arrayOf("工作室介绍", "团队介绍", "收到的感谢"))
        }
        mAdapter.notifyDataSetChanged()
    }
}

fun WorkRoomMainFragment.refreshToolbar(data: UserHomePagerResult) {
    room_main_toolbar_title.text = data.info?.groupInfo?.nickname.orEmpty()
    room_main_toolbar_menupoint.isGone = data.isGroupMember
    (data.roomStatus != null).apply {
        room_main_toolbar_label_text.isGone = this || data.isGroupMember
        room_main_toolbar_living_label.isVisible = this && !data.isGroupMember
        room_main_toolbar_living_label_avatar.isVisible = this && !data.isGroupMember
        if (this) {
            room_main_toolbar_living_label_avatar.glideDisplay(data.roomStatus?.personalImage, data.roomStatus?.gender.genderPersonalImageLivingTop())
        }
    }
}

@SuppressLint("SetTextI18n")
fun WorkRoomMainFragment.refreshFooter(data: UserHomePagerResult.Promoter?) {
    if (data == null) {
        room_main_footer.isVisible = false
        return
    }
    room_main_footer.isVisible = true
    room_main_footer_avatar.glideDisplay(data.personalImage, data.gender.genderPersonalImagePromoter())
    room_main_footer_title.text = "我是${data.nickname}\n有什么问题都可以找我~"
    room_main_footer_next.setOnClickNotDoubleListener(tag = "room_main_footer") {

        apiSpiceMgr.requestImUserInfo(arrayOf(data.userId), needRefresh = true, back = {
            val userInfo = it.firstOrNull()
            when {
                userInfo?.isSelfToBlackList == true -> {
                    UIToast.toast(tabbarConfig.groupBlacklistUser)
                }
                userInfo?.isTheOtherToBlackList == true -> {
                    UIToast.toast(tabbarConfig.hasBlacklistTips)
                }
                else -> {
                    activity?.toImChat(data.userId.toString())
                }
            }
        })
    }
}

fun WorkRoomMainFragment.refreshHeader(data: UserHomePagerResult, replaceIndex: Int = -1) {
    if (replaceIndex != -1) {
        mData[replaceIndex] = WorkRoomHeaderData(data)
    } else {
        mData.add(WorkRoomHeaderData(data))
    }
}

fun WorkRoomMainFragment.refreshConsultList(data: List<UserHomePagerResult.ConsultInfo>?, promoterId: Int? = 0, isGroupMember: Boolean = false) {
    if (data.isNullOrEmpty()) return
    mData.add(WorkRoomTitleData(hasInterval = true).apply {
        title = "咨询师"
        size = data.size
    })
    val array = AvatarBg.values()
    data.forEachIndexed { index, consultInfo ->
        consultInfo.bindPromoterId = promoterId ?: 0 // 工作室成员怎么跳转
        consultInfo.isGroupMember = isGroupMember
        mData.add(WorkRoomAvatarData(consultInfo, array[index % array.size]))
    }
}

fun WorkRoomMainFragment.refreshAnalyst(data: List<UserHomePagerResult.ConsultInfo>?, isGroupMember: Boolean = false) {
    if (data.isNullOrEmpty()) return
    mData.add(WorkRoomTitleData().apply {
        title = "分析师"
        size = data.size
    })
    data.forEach { it.isGroupMember = isGroupMember }
    mData.add(WorkRoomAvatarListData(data))
}

fun WorkRoomMainFragment.refreshService(data: List<ServiceInfo>?, groupId: Int, roomId: String? = null) {
    if (data.isNullOrEmpty()) return
    mData.add(WorkRoomTitleData(hasInterval = true, hasShowAll = true).apply {
        title = "专属服务"
        this.groupId = groupId
        this.roomId = roomId
    })
    data.forEach {
        mData.add(it.apply {
            it.roomId = roomId
            pagerResult.bindPromoter?.let { promoter -> publisherId = promoter.userId }
        })
    }
}

fun WorkRoomMainFragment.refreshCourse(data: List<CourseInfo>?, groupId: Int, roomId: String? = null) {
    if (data.isNullOrEmpty()) return
    mData.add(WorkRoomTitleData(hasShowAll = true).apply {
        title = "课程"
        this.groupId = groupId
        this.roomId = roomId
    })
    data.forEach {
        mData.add(it.apply {
            pagerResult.bindPromoter?.let { promoter -> userId = promoter.userId }
            isGroupMember = pagerResult.isGroupMember
            this.roomId = roomId
            this.groupId = groupId
        })
    }
}

fun WorkRoomMainFragment.refreshLiveInfo(data: WorkRoomLiveInfo) {
    mData.add(WorkRoomTitleData(hasInterval = true).apply { title = "收到的感谢礼物" })
    mData.add(data)
}

fun WorkRoomMainFragment.refreshGift(data: List<UserHomePagerResult.ReceiveGiftBean>?) {
    if (data.isNullOrEmpty()) return
    val array = GiftPosition.values()
    data.forEachIndexed { index, receiveGiftBean ->
        mData.add(WorkRoomGiftData(receiveGiftBean, array[index % array.size]))
    }
}

fun WorkRoomMainFragment.refreshLivingState() {
    refreshToolbar(pagerResult)

    var headerIndex = -1
    mData.forEachIndexed { index, any ->
        if (any is WorkRoomHeaderData) {
            headerIndex = index
            return@forEachIndexed
        }
    }
    refreshHeader(pagerResult, headerIndex)
    mAdapter.notifyDataSetChanged()
}

// ---- 数据刷新 ----
@SuppressLint("InflateParams")
fun WorkRoomMainFragment.showReportPopWindow(view: View) {
    val popView = LayoutInflater.from(view.context).inflate(R.layout.home_page_pop_window_layout, null)
    val report = popView.findViewById<TextView>(R.id.tv_hom_page_report)
    val pop = PopupWindow(popView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT).apply {
        setOnDismissListener {
            activity?.window?.attributes = activity?.window?.attributes?.apply { alpha = 1f }
        }
    }
    report.setOnClickListener {
        //举报
        activity?.showSelectedReportReason {
            apiSpiceMgr.executeToastKt(ReportRequest(ReportBean().apply {
                reasonId = it.type
                reportUser = mUserId
            }), success = {
                if (it.isSuccess) UIToast.toast("举报成功")
            })
        }
        activity?.window?.attributes = activity?.window?.attributes?.apply { alpha = 1f }
        pop.dismiss()
    }

    pop.isOutsideTouchable = true
    pop.isFocusable = true
    pop.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    pop.showAsDropDown(view)
}

fun WorkRoomMainFragment.clickSelectTab(tab: TabLayout.Tab) {
    val manager = room_main_rv.layoutManager
    if (manager is GridLayoutManager) {
        val firstItem = manager.findFirstVisibleItemPosition()
        val lastItem = manager.findLastVisibleItemPosition()
        val targetPosition = getRvPosition(tab.position)
        if (firstItem > -1 && lastItem > -1 && targetPosition > -1) {
            room_main_page_tab.tag = true
            smoothScroller.targetPosition = targetPosition
            manager.startSmoothScroll(smoothScroller)
        }
    }
}

fun WorkRoomMainFragment.getRvPosition(tabIndex: Int): Int = when (tabIndex) {
    0 -> 0
    1 -> mData.indexOfFirst { it is WorkRoomAvatarData } - 1
    2 -> mData.indexOfFirst { it is ServiceInfo } - 1
    3 -> mData.indexOfFirst { it is WorkRoomLiveInfo } - 1
    else -> -1
}

fun WorkRoomMainFragment.getChangeTargetPosition(findFirstVisibleItemPosition: Int): Int {
    return tabIndexList.filter { it in 0..findFirstVisibleItemPosition }.size - 1
}

fun getCurWorkRoomLivingRoomId(roomId: String?, groupId: Int): String {
    val liveRoomActivity = (AppManager.getInstance().getTargetActivity(VideoLiveRoomActivity::class.java) as? VideoLiveRoomActivity)
    val livingRoomId = liveRoomActivity?.roomId.orEmpty()
    val livingGroupId = liveRoomActivity?.roomInfo?.groupInfo?.groupId
    return if (livingRoomId == roomId || livingGroupId == groupId) livingRoomId else ""
}

@DrawableRes
fun Int?.genderPersonalImageLivingTop(): Int = when (this) {
    0 -> R.drawable.personal_img_female_living_top
    1 -> R.drawable.personal_img_male_living_top
    else -> R.drawable.personal_img_male_living_top
}

@DrawableRes
fun Int?.genderPersonalImagePromoter(): Int = when (this) {
    0 -> R.drawable.personal_img_female_promoter
    1 -> R.drawable.personal_img_male_promoter
    else -> R.drawable.personal_img_male_promoter
}