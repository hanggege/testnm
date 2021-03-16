package com.mei.mentor_home_page.ui

import android.animation.ArgbEvaluator
import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import androidx.core.view.updatePadding
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.tabs.TabLayout
import com.joker.im.custom.chick.CourseInfo
import com.mei.GrowingUtil
import com.mei.base.common.COURSE_PAY_SUCCESS
import com.mei.base.common.MENTOR_PAGE_CHANGED
import com.mei.base.upload.MeiUploader
import com.mei.chat.toImChat
import com.mei.dialog.showSelectedReportReason
import com.mei.im.ui.popup.CommonPopupWindow
import com.mei.live.LiveEnterType
import com.mei.live.manager.genderAvatar
import com.mei.live.ui.VideoLiveRoomActivityLauncher
import com.mei.live.ui.ext.getCurrLiveState
import com.mei.live.views.followDelete
import com.mei.live.views.followFriend
import com.mei.me.activity.PersonalInformationActivity
import com.mei.mentor_home_page.adapter.*
import com.mei.mentor_home_page.model.CourseTitle
import com.mei.mentor_home_page.model.MentorLiveData
import com.mei.mentor_home_page.model.ServiceTitle
import com.mei.mentor_home_page.model.WorkTitle
import com.mei.mentor_home_page.ui.dialog.showMentorBrowser
import com.mei.orc.Cxt
import com.mei.orc.callback.TaskCallback
import com.mei.orc.event.bindAction
import com.mei.orc.ext.*
import com.mei.orc.imageload.glide.glideDisplay
import com.mei.orc.john.model.JohnUser
import com.mei.orc.john.upload.PhotoList
import com.mei.orc.ui.toast.UIToast
import com.mei.orc.util.save.getBooleanMMKV
import com.mei.orc.util.save.putMMKV
import com.mei.orc.util.startactivity.startFragmentForResult
import com.mei.picker.pickerImage
import com.mei.wood.R
import com.mei.wood.cache.requestImUserInfo
import com.mei.wood.dialog.DialogData
import com.mei.wood.dialog.NormalDialogLauncher
import com.mei.wood.extensions.executeKt
import com.mei.wood.extensions.executeToastKt
import com.mei.wood.rx.ObserverSilentSubscriber
import com.mei.wood.ui.MeiCustomActivity
import com.mei.wood.ui.MeiCustomBarActivity
import com.mei.wood.util.logDebug
import com.net.model.chick.friends.ProductBean
import com.net.model.chick.friends.UserHomePagerResult
import com.net.model.chick.friends.WorkListResult
import com.net.model.chick.report.ReportBean
import com.net.model.chick.tab.tabbarConfig
import com.net.network.chick.friends.HomePageCoverRequest
import com.net.network.chick.recommend.BatRoomStatusRequest
import com.net.network.chick.report.ReportRequest
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.mentor_home_page_activity.*
import kotlinx.android.synthetic.main.mentor_home_page_header.*
import java.util.concurrent.TimeUnit
import kotlin.math.abs


fun MentorHomePageFragment.initView() {
    mentor_home_page_upload_cover.isVisible = mUserId == JohnUser.getSharedUser().userID
    title_follow_container.isVisible = mUserId != JohnUser.getSharedUser().userID
    initTabList()
    mentor_toolbar.updatePadding(top = getStatusBarHeight())
    mentor_home_page_rv.adapter = mAdapter
    personal_follow.setOnClickListener { //关注
        optionsFollow()
    }
    mentor_home_page_title_follow.setOnClickListener { //关注
        personal_follow.performClick()
    }
    mentor_home_page_appbar.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
        if (isAdded) {
            val fraction = abs(verticalOffset * 1.0f) / appBarLayout.totalScrollRange
            if (fraction > 0.3f) {
                mentor_home_page_back_iv.setImageResource(R.drawable.bg_black_back_arrow)
                mentor_toolbar_title.isVisible = true
                more_btn.setPointColor(Color.BLACK)
                mentor_home_page_upload_cover.isVisible = false
                activity?.lightMode()
            } else {
                mentor_home_page_back_iv.setImageResource(R.drawable.bg_white_back_arrow)
                mentor_toolbar_title.isVisible = false
                more_btn.setPointColor(Color.WHITE)
                if (mUserId == JohnUser.getSharedUser().userID) {
                    mentor_home_page_upload_cover.isVisible = true
                }
                mentor_home_page_upload_cover.setTextColor(Color.WHITE)
                activity?.darkMode()
            }
            mentor_home_page_title_follow.alpha = fraction
            (ArgbEvaluator().evaluate(fraction, Color.TRANSPARENT, Color.WHITE) as? Int)?.let { mentor_toolbar.setBackgroundColor(it) }
        }
    })
    more_btn.setOnClickListener {
        activity?.window?.apply {
            attributes = attributes.apply { alpha = 0.6f }
        }
        showReportPopWindow(it)
    }
    icon_follow_profile.setOnClickListener {
        activity?.window?.apply {
            attributes = attributes.apply { alpha = 0.6f }
        }
        showUnFollowPop(it)
    }
    //用户头像
    personal_avatar.setOnClickListener {
        if (mUserStatus?.roomId.orEmpty().isNotEmpty() || pagerResult.info?.onlineStatus == 1) {
            details_info02.performClick()
        }
    }
    //直播间id
    details_info02.setOnClickListener {
        toLivePage()
    }
    mentor_home_page_upload_cover.setOnClickListener { uploadHomePageHeadImg() }
    mentor_home_page_back_iv.setOnClickListener {
        activity?.finish()
    }
    living_layout.setOnClickListener {
        //跳转直播间
        toLivePage()
    }
    mentor_home_page_rv.addOnScrollListener(object : RecyclerView.OnScrollListener() {

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            if (isAdded) {
                val rvLayoutManager: RecyclerView.LayoutManager? = recyclerView.layoutManager
                if (rvLayoutManager is GridLayoutManager && mentor_home_page_tab.tag != true) {
                    val firstPosition = rvLayoutManager.findFirstVisibleItemPosition()
                    val tabSelected = (0 until mentor_home_page_tab.tabCount).mapNotNull {
                        mentor_home_page_tab.getTabAt(it)?.view
                    }.indexOfFirst { it.isSelected }
                    val targetPosition = getChangeTargetPosition(firstPosition)
                    if (tabSelected != targetPosition && targetPosition > -1) {
                        mentor_home_page_tab.setScrollPosition(targetPosition, 0f, true)
                    }
                }
            }

        }

        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            //针对课程游览统计
            if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                visitorsStatistics()
                if (isAdded) {
                    mentor_home_page_tab.tag = false
                }
            }
        }
    })
}

fun MentorHomePageFragment.toLivePage() {
    if (getCurrLiveState() != 1) {
        VideoLiveRoomActivityLauncher.startActivity(activity, pagerResult.info?.roomId.orEmpty(), LiveEnterType.user_home_page)
    } else {
        UIToast.toast("当前正在直播中，无法查看")
    }
}

fun MentorHomePageFragment.initEvent() {
    Observable.interval(0, 10, TimeUnit.SECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .compose(bindToLifecycle())
            .subscribe(object : ObserverSilentSubscriber<Long>() {
                override fun onNext(long: Long) {
                    apiSpiceMgr.executeKt(BatRoomStatusRequest(arrayListOf(mUserId)), success = { r ->
                        if (r.isSuccess) mUserStatus = r.data?.get(mUserId.toString())
                        refreshLivingState()
                    })
                }
            })
    bindAction<Boolean>(COURSE_PAY_SUCCESS) {
        loadUserInfo()
    }
    bindAction<Boolean>(MENTOR_PAGE_CHANGED) {
        loadUserInfo()
    }

}

fun MentorHomePageFragment.initTabList(tabs: List<String> = arrayListOf("个人简介", "相册", "收到感谢")) {
    mentor_home_page_tab.removeAllTabs()
    mentor_home_page_tab.clearOnTabSelectedListeners()
    tabs.forEach { mentor_home_page_tab.addTab(mentor_home_page_tab.newTab().setText(it)) }
    mentor_home_page_tab.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
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


@Suppress("UNUSED_PARAMETER")
fun MentorHomePageFragment.onAdapterItemClick(view: View, item: Any?) {
    when (item) {
        is ProductBean -> {
            /** 跳转到图片预览页面 **/
            when (getCurrLiveState()) {
                1 -> UIToast.toast("当前正在直播中，无法查看")
                2 -> UIToast.toast("当前正在连线中，无法查看")
                else -> {
                    val index = mWorkListResult.list.indexOf(item)
                    if (index > -1) {
                        activity?.showMentorBrowser(mUserId, mWorkListResult, index, topWorksLimit = pagerResult.topWorksLimit) { response ->
                            mWorkListResult = response
                            mData.removeAll { it is ProductBean }
                            val insertPosition = mData.indexOfFirst { it is WorkTitle }
                            if (insertPosition > 0) {
                                mData.addAll(insertPosition + 1, response.list.orEmpty())
                            }
                            //判断还有没有更多
                            if (!mWorkListResult.hasMore) mData.remove(ITEM_TYPE_WORK_UNFOLD)
                            mAdapter.notifyDataSetChanged()
                        }
                    }
                }
            }
        }
    }
}

fun MentorHomePageFragment.onAdapterChildClick(view: View, item: Any?) {
    if (view.id == R.id.mentor_home_page_load_more_rrl) {
        when (item) {
            ITEM_TYPE_WORK_UNFOLD -> {
                //加载更多作品
                loadMoreProducts()
            }
            ITEM_TYPE_SERVICE_UNFOLD -> {
                //加载更多服务
                loadMoreServices()
            }
            ITEM_TYPE_COURSE_UNFOLD -> {
                //加载更多课程
                loadMoreCourses()
            }
        }
    }
}


private fun MentorHomePageFragment.visitorsStatistics() {
    //不影响业务处理
    try {
        val firstPosition = (mentor_home_page_rv.layoutManager as? LinearLayoutManager)?.findFirstVisibleItemPosition()
                ?: -1
        val lastPosition = (mentor_home_page_rv.layoutManager as? LinearLayoutManager)?.findLastVisibleItemPosition()
                ?: -1
        if (firstPosition in -1 until mData.size && lastPosition in -1 until mData.size && firstPosition > lastPosition) {
            (firstPosition..lastPosition).mapNotNull { mData.getOrNull(it) as? CourseInfo }.forEach {
                GrowingUtil.track("function_view",
                        "function_name", "课程", "function_page", "个人主页",
                        "status", if (!it.hasBuy) "未购买" else "已购买")
            }
        }

    } catch (e: Exception) {
        e.printStackTrace()
    }
}

/**
 * 上传封面图片
 */
fun MentorHomePageFragment.uploadHomePageHeadImg() {
    activity?.pickerImage(1) { list ->
        list.firstOrNull()?.let {
            showLoading(true)
            MeiUploader(apiSpiceMgr) { key, percent -> logDebug("uploadImg", "$percent   $key") }.uploadImage(list, object : TaskCallback<PhotoList>() {
                override fun onSuccess(result: PhotoList?) {
                    val photoUri = result?.newPhotos.orEmpty().firstOrNull()?.uri.orEmpty()
                    if (photoUri.isNotEmpty()) {
                        apiSpiceMgr.executeToastKt(HomePageCoverRequest(photoUri), success = {
                            mentor_bg_iv.glideDisplay(photoUri, pagerResult.info?.userInfo?.gender.genderAvatar(pagerResult.info?.userInfo?.isPublisher))
                        }, finish = {
                            showLoading(false)
                        })
                    } else {
                        showLoading(false)
                        UIToast.toast(activity, "封面上传失败，请重试")
                    }
                }

                override fun onFailed(exception: Exception?, errCode: Int, vararg objects: Any?) {
                    showLoading(false)
                    UIToast.toast(activity, "封面上传失败，请重试")
                }
            })
        }
    }
}


/**
 * 个性关注状态
 */
fun MentorHomePageFragment.refreshFollowState(isFollow: Boolean) {
    val isSelf = mUserId == JohnUser.getSharedUser().userID
    if (!isSelf) {
        pagerResult.info?.isFollow = isFollow
        icon_follow_profile.updateLayoutParams<LinearLayout.LayoutParams> {
            rightMargin = dip(15)
        }
    }

    icon_follow_profile.isGone = isSelf || !isFollow
    mentor_home_page_title_follow.text = if (!isSelf) "私聊" else "编辑资料"
    mentor_btn_state_tv.text = if (!isSelf) "私聊" else "编辑资料"
    val leftDrawable = if (isSelf) null else activity?.getDrawable(R.drawable.icon_chat_tip)
    mentor_btn_state_tv.setCompoundDrawablesRelativeWithIntrinsicBounds(leftDrawable, null, null, null)
    personal_follow.layoutParams?.apply {
        width = dip(if (isSelf || !isFollow) 215 else 180)
    }
}

fun MentorHomePageFragment.optionsFollow() {
    if (mUserId == JohnUser.getSharedUser().userID) {
        //编辑资料
        activity?.startFragmentForResult(Intent(activity, PersonalInformationActivity::class.java), 120) { code, _ ->
            if (code == 120) {
                loadUserInfo()
            }
        }
    } else {
        apiSpiceMgr.requestImUserInfo(arrayOf(pagerResult.info?.userId
                ?: 0), needRefresh = true, back = {
            val info = it.firstOrNull()
            if (info?.isSelfToBlackList == true) {
                UIToast.toast(tabbarConfig.publisherBlacklistUser)
            } else if (info?.isTheOtherToBlackList == true) {
                UIToast.toast(tabbarConfig.hasBlacklistTips)
            } else {
                if (pagerResult.info?.isFollow == true) {
                    //已关注，跳转到私聊
                    activity?.toImChat(mUserId.toString())
                } else {
                    NormalDialogLauncher.newInstance().showDialog(activity, DialogData("是否关注并私聊知心达人？"), okBack = {
                        (activity as? MeiCustomBarActivity)?.followFriend(mUserId, 0, mUserId.toString()) {
                            if (it) {
                                refreshFollowState(true)
                                activity?.toImChat(mUserId.toString())
                            }
                        }
                    })

                }
            }
        })


    }
}

fun MentorHomePageFragment.clickSelectTab(tab: TabLayout.Tab) {
    mentor_home_page_appbar.setExpanded(false, false)
    val manager = mentor_home_page_rv.layoutManager
    if (manager is GridLayoutManager) {
        val firstItem = manager.findFirstVisibleItemPosition()
        val lastItem = manager.findLastVisibleItemPosition()
        val targetPosition = getRvPosition(tab.position)
        if (firstItem > -1 && lastItem > -1 && targetPosition > -1) {
            mentor_home_page_tab.tag = true
            smoothScroller.targetPosition = targetPosition
            manager.startSmoothScroll(smoothScroller)
        }
    }
}


//转换数据
fun MentorHomePageFragment.conversionData(data: UserHomePagerResult) {
    mData.clear()
    //基本信息
    mData.add(data)
    //课程服务数据
    addCourseService(data)
    //作品数据
    addWorks(data)
    //直播礼物数据
    addLiveData(data)
    mData.add(ITEM_TYPE_FOOTER)
    //刷新数据
    mAdapter.notifyDataSetChanged()
    //定位顶部tab
    if (isAdded) {
        mentor_home_page_rv.post {
            if (isAdded) {
                if (mSelectPosition in 1 until mentor_home_page_tab.tabCount) {
                    mentor_home_page_tab.selectTab(mentor_home_page_tab.getTabAt(mSelectPosition))
                }
            }
        }
    }
}

/**
 * 插入礼物数据
 */
fun MentorHomePageFragment.addLiveData(result: UserHomePagerResult) {
    val mentorLiveData = MentorLiveData()
    mentorLiveData.broadcastCount = result.broadcastCount
    mentorLiveData.fansCount = result.fansCount
    mentorLiveData.receiveGiftCount = result.receiveGiftCount
    mentorLiveData.receivedCoinCount = result.receivedCoinCount
    mData.add(mentorLiveData)
    result.receiveGift?.apply {
        forEach {
            mData.add(it)
        }
    }
}

/**
 * 插入作品数据
 */
fun MentorHomePageFragment.addWorks(result: UserHomePagerResult) {
    val workTitle = WorkTitle(mUserId == JohnUser.getSharedUser().userID)
    mData.add(workTitle)
    mWorkListResult = result.works ?: WorkListResult.WorksBean()
    mWorkListResult.worksCount = result.worksCount
    if (mWorkListResult.list.orEmpty().isEmpty()) {
        mData.add(ITEM_TYPE_WORK_EMPTY)
    } else {
        mData.addAll(mWorkListResult.list)
        mData.addSelfIf(mWorkListResult.hasMore, ITEM_TYPE_WORK_UNFOLD)
        mWorkListResult.list.forEachIndexed { index, bean -> bean.index = index }
    }
}

/**
 * 插入服务数据
 */
fun MentorHomePageFragment.addCourseService(result: UserHomePagerResult) {
    //添加专属服务
    if (hasService) {
        initTabList(arrayListOf("个人简介", "课程服务", "相册", "收到感谢"))
    }
    result.specialServices?.apply {
        if (list.orEmpty().isNotEmpty()) {
            mData.add(ServiceTitle())
            mData.addAll(list)
            pagerResult.specialServices?.nextPageNo = nextPageNo
            if (hasMore) {
                mData.add(ITEM_TYPE_SERVICE_UNFOLD)
            } else {
                mData.remove(ITEM_TYPE_SERVICE_UNFOLD)
            }
        }
    }
    //添加课程
    result.courses?.apply {
        if (list.orEmpty().isNotEmpty()) {
            mData.add(CourseTitle())
            pagerResult.specialServices?.nextPageNo = nextPageNo
            mData.addAll(list)
            if (hasMore) {
                mData.add(ITEM_TYPE_COURSE_UNFOLD)
            } else {
                mData.remove(ITEM_TYPE_COURSE_UNFOLD)
            }
        }
    }
}


/**
 * 查找悬浮菜单对应的内容开始位置
 */
fun MentorHomePageFragment.getRvPosition(tabIndex: Int): Int {
    return findIndexList.getOrNull(tabIndex) ?: -1
}

/**
 * 通过当前显示的位置打tab的选择项
 */
fun MentorHomePageFragment.getChangeTargetPosition(findFirstVisibleItemPosition: Int): Int {
    return findIndexList.filter { it in 0..findFirstVisibleItemPosition }.size - 1
}

@SuppressLint("InflateParams")
fun MentorHomePageFragment.showReportPopWindow(view: View) {
    val popView = LayoutInflater.from(activity).inflate(R.layout.home_page_pop_window_layout, null)
    val report = popView.findViewById<TextView>(R.id.tv_hom_page_report)
    val pop = PopupWindow(popView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT).apply {
        setOnDismissListener {
            activity?.window?.apply {
                attributes = attributes.apply { alpha = 1f }
            }

        }
        isOutsideTouchable = true
        isFocusable = true
        setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
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
        pop.dismiss()
    }
    pop.showAsDropDown(view)

}


/**
 * 取消关注pop
 */
@SuppressLint("InflateParams")
fun MentorHomePageFragment.showUnFollowPop(view: View) {
    val popView = LayoutInflater.from(activity).inflate(R.layout.home_page_pop_un_follow, null)
    val pop = PopupWindow(popView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT).apply {
        setOnDismissListener {
            activity?.window?.apply {
                attributes = attributes.apply { alpha = 1f }
            }
        }
        isOutsideTouchable = true
        isFocusable = true
        setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }
    popView.findViewById<TextView>(R.id.tv_hom_page_cancel).apply {
        setOnClickListener {
            //请求取关
            (activity as? MeiCustomActivity)?.followDelete(mUserId) { isOk ->
                if (isOk) {
                    refreshFollowState(false)
                }
            }
            pop.dismiss()
        }
    }
    //解决x轴偏移无效的问题
    popView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
    pop.showAsDropDown(view, -popView.measuredWidth + view.width, 0)
}


/**
 * 获取直播状态
 */
@SuppressLint("SetTextI18n")
fun MentorHomePageFragment.refreshLivingState() {
    val isLiving = mUserStatus?.roomId.orEmpty().isNotEmpty()
    living_layout.isVisible = isLiving
    if (living_layout.isVisible && !PLAY_SHOW_NIECE.getBooleanMMKV(false)) {
        popPlaying(living_layout)
    }
    personal_live_room_id.text = "直播间ID: ${mUserStatus?.roomId ?: pagerResult.info?.roomId}"
    iv_online_status.isVisible = pagerResult.info?.onlineStatus != 0 || isLiving
    if (isLiving) {
        iv_online_status.setBackgroundResource(R.drawable.icon_playing_status)
        avatar_stroke.delegate.strokeColor = Cxt.getColor(R.color.color_ffa00a)
        iv_online_status.updateLayoutParams<RelativeLayout.LayoutParams> {
            setMargins(0, 0, dip(8), 0)
        }
    } else {
        if (pagerResult.info?.onlineStatus == 2) {
            iv_online_status.setBackgroundResource(R.drawable.icon_online_status)
            iv_online_status.updateLayoutParams<RelativeLayout.LayoutParams> {
                setMargins(0, 0, dip(15), dip(10))
            }
        } else {
            iv_online_status.isVisible = false
        }
        avatar_stroke.delegate.strokeColor = Color.TRANSPARENT
    }
}

//直播的时候首次进来的时候提示
const val PLAY_SHOW_NIECE = "PLAY_SHOW_NIECE"
var mPopupWindow: CommonPopupWindow? = null
fun MentorHomePageFragment.popPlaying(viewTarget: View) {
    mPopupWindow?.let {
        if (!it.isShowing) {
            it.showAsDropDown(viewTarget, -dip(135), -dip(83))
        }
        return
    }
    mPopupWindow = CommonPopupWindow.Builder(activity).setView(R.layout.pop_view_playing)
            .setWidthAndHeight(dip(195), dip(52))
            .setAnimationStyle(R.style.AnimRight).create()
    mPopupWindow?.showAsDropDown(viewTarget, -dip(135), -dip(83))
    mPopupWindow?.setOnDismissListener {
        PLAY_SHOW_NIECE.putMMKV(true)
    }
}
