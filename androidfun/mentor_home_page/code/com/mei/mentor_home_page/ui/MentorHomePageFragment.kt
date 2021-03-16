package com.mei.mentor_home_page.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import com.joker.im.bindEventLifecycle
import com.joker.im.custom.CustomType
import com.joker.im.custom.chick.ChickCustomData
import com.joker.im.custom.chick.CourseInfo
import com.joker.im.custom.chick.ServiceInfo
import com.joker.im.listener.IMAllEventManager
import com.joker.im.mapToMeiMessage
import com.joker.im.message.CustomMessage
import com.mei.live.manager.genderAvatar
import com.mei.mentor_home_page.adapter.ITEM_TYPE_COURSE_UNFOLD
import com.mei.mentor_home_page.adapter.ITEM_TYPE_SERVICE_UNFOLD
import com.mei.mentor_home_page.adapter.ITEM_TYPE_WORK_UNFOLD
import com.mei.mentor_home_page.adapter.MentorPageAdapter
import com.mei.mentor_home_page.model.CourseTitle
import com.mei.mentor_home_page.model.MentorLiveData
import com.mei.mentor_home_page.model.ServiceTitle
import com.mei.mentor_home_page.model.WorkTitle
import com.mei.orc.ActivityLauncher
import com.mei.orc.imageload.glide.glideDisplay
import com.mei.wood.R
import com.mei.wood.extensions.executeToastKt
import com.mei.wood.ui.CustomSupportFragment
import com.net.model.chick.friends.ProductBean
import com.net.model.chick.friends.UserHomePagerResult
import com.net.model.chick.friends.WorkListResult
import com.net.model.chick.recommend.BatRoomStatusResult
import com.net.network.chick.friends.CourseListRequest
import com.net.network.chick.friends.ServiceListRequest
import com.net.network.chick.friends.UserHomePagerRequest
import com.net.network.chick.friends.WorkListRequest
import com.tencent.imsdk.TIMConversationType
import com.tencent.imsdk.TIMMessage
import kotlinx.android.synthetic.main.mentor_home_page_activity.*
import kotlinx.android.synthetic.main.mentor_home_page_header.*
import launcher.Boom
import launcher.MulField

/**
 *
 * @author Created by zyh on 2020/5/20
 */
@Suppress("WHEN_ENUM_CAN_BE_NULL_IN_JAVA")
class MentorHomePageFragment : CustomSupportFragment() {

    @Boom
    var mUserId: Int = 0

    @Boom
    @MulField
    var mSelectPosition = 0   //默认选择tab

    var pagerResult = UserHomePagerResult()

    var mData = arrayListOf<Any>()
    var mUserStatus: BatRoomStatusResult.StatusResult? = null
    var mWorkListResult = WorkListResult.WorksBean()
    var findIndexList = arrayListOf<Int>()
    val mAdapter by lazy {
        MentorPageAdapter(mData).apply {
            setOnItemChildClickListener { adapter, view, position -> onAdapterChildClick(view, adapter.getItemOrNull(position)) }
            setOnItemClickListener { adapter, view, position -> onAdapterItemClick(view, adapter.getItemOrNull(position)) }
            registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
                override fun onChanged() {
                    super.onChanged()
                    findIndexList.clear()
                    findIndexList.addAll(arrayListOf<Int>().apply {
                        //个人简介
                        add(mData.indexOfFirst { it is UserHomePagerResult })
                        //课程服务
                        val position = mData.indexOfFirst { it is ServiceTitle || it is CourseTitle }
                        if (position != -1) {
                            add(position)
                        }
                        //相册
                        add(mData.indexOfFirst { it is WorkTitle })
                        //收到感谢
                        add(mData.indexOfFirst { it is MentorLiveData })
                    })
                }
            })
        }
    }
    val smoothScroller: LinearSmoothScroller by lazy {
        object : LinearSmoothScroller(activity) {
            override fun getVerticalSnapPreference(): Int = SNAP_TO_START
        }
    }

    // 主要是为了监听专属服务充值成功
    val checkNewEvent by lazy {
        object : IMAllEventManager() {
            override fun onNewMessages(msgs: MutableList<TIMMessage>?): Boolean {
                val customList = msgs.orEmpty()
                        .mapNotNull { it.mapToMeiMessage() as? CustomMessage }
                        .filter { !it.isDeleted() && it.chatType == TIMConversationType.C2C }//去掉已删除的消息
                customList.forEach {
                    (it.customInfo?.data as? ChickCustomData)?.let { data ->
                        when (data.type) {
                            CustomType.special_service_card -> {
                                if (data.serviceInfo?.cardType == ServiceInfo.CardType.TYPE_RECHARGE) {
                                    //刷新关注状态
                                    refreshFollowState(true)
                                }

                            }
                            else -> {
                            }
                        }

                    }
                }
                return super.onNewMessages(msgs)
            }
        }
    }

    var hasService
        get() = pagerResult.specialServices?.list.orEmpty().isNotEmpty() || pagerResult.courses?.list.orEmpty().isNotEmpty()
        set(_) {}


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        ActivityLauncher.bind(this)
        return inflater.inflate(R.layout.mentor_home_page_activity, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initEvent()
        checkNewEvent.bindEventLifecycle(this)
        if (pagerResult.info?.userId != mUserId) loadUserInfo()
        else refreshListData()
    }

    @SuppressLint("SetTextI18n")
    fun loadUserInfo() {
        apiSpiceMgr.executeToastKt(UserHomePagerRequest(mUserId), success = {
            if (it.isSuccess && it.data != null) {
                pagerResult = it.data
                refreshListData()
            }
        }, failure = {

        }, finish = {
            showLoading(false)
        })

    }

    @SuppressLint("SetTextI18n")
    private fun refreshListData() {
        pagerResult.apply {
            mentor_bg_iv.glideDisplay(info?.homepageCover.orEmpty(), R.drawable.bg_personal_page_head)
            personal_avatar.glideDisplay(info?.userInfo?.avatar.orEmpty(), info?.userInfo?.gender.genderAvatar(info?.userInfo?.isPublisher))
            personal_name.text = info?.userInfo?.nickname.orEmpty()
            mentor_toolbar_title.text = info?.userInfo?.nickname.orEmpty()
            personal_ability.text = "擅长领域：${info?.userInfo?.publisherSkills.orEmpty().joinToString { it.name }}"
            personal_ability.isVisible = info?.userInfo?.publisherSkills.orEmpty().isNotEmpty()
            if (info?.publisherTags.orEmpty().isEmpty()) {
                //如果没有标签，就显示擅长领域
                info?.publisherTags?.addAll(info?.skills.orEmpty().mapNotNull { it.name })
            }
            refreshFollowState(info?.isFollow ?: false)
            conversionData(this)
        }
    }

    fun loadMoreProducts() {
        showLoading(true)
        apiSpiceMgr.executeToastKt(WorkListRequest(mUserId, mWorkListResult.nextPageNo), success = { response ->
            response.data?.list?.let { data ->
                mWorkListResult.list.addAll(data.list.orEmpty())
                mWorkListResult.list.forEachIndexed { index, bean -> bean.index = index }
                mWorkListResult.nextPageNo = data.nextPageNo
                mWorkListResult.hasMore = data.hasMore

                //判断还有没有更多
                if (!mWorkListResult.hasMore) mData.remove(ITEM_TYPE_WORK_UNFOLD)

                val insertPosition = mData.indexOfLast { it is ProductBean }
                if (insertPosition < 0) mData.indexOfFirst { it is WorkTitle }
                if (insertPosition > 0) {
                    mData.addAll(insertPosition + 1, data.list.orEmpty())
                }
                mAdapter.notifyDataSetChanged()
            }
        }, finish = {
            showLoading(false)
        })

    }

    fun loadMoreServices() {
        pagerResult.specialServices?.let { service ->
            apiSpiceMgr.executeToastKt(ServiceListRequest(mUserId, service.nextPageNo), success = { response ->
                response?.data?.list?.apply {
                    pagerResult.specialServices?.nextPageNo = nextPageNo
                    pagerResult.specialServices?.list?.addAll(list)

                    val insertPosition = mData.indexOfLast { it is ServiceInfo }
                    if (insertPosition > 0) {
                        mData.addAll(insertPosition, list)
                    }
                    //判断还有没有更多
                    if (!hasMore) {
                        mData.remove(ITEM_TYPE_SERVICE_UNFOLD)
                    }
                    mAdapter.notifyDataSetChanged()
                }
            }, finish = {
                showLoading(false)
            })
        }
    }

    fun loadMoreCourses() {
        pagerResult.courses?.let { course ->
            apiSpiceMgr.executeToastKt(CourseListRequest(mUserId, course.nextPageNo), success = { response ->
                response?.data?.list?.courses?.apply {
                    pagerResult.courses?.nextPageNo = nextPageNo
                    pagerResult.courses?.list?.addAll(list)

                    val insertPosition = mData.indexOfLast { it is CourseInfo }
                    if (insertPosition > 0) {
                        mData.addAll(insertPosition, list)
                    }
                    //判断还有没有更多
                    if (!hasMore) {
                        mData.remove(ITEM_TYPE_COURSE_UNFOLD)
                    }
                    mAdapter.notifyDataSetChanged()
                }
            }, finish = {
                showLoading(false)
            })
        }
    }


}