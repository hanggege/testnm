package com.mei.live.ui.dialog.fragment

import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mei.GrowingUtil
import com.mei.base.ui.nav.Nav
import com.mei.live.manager.genderAvatar
import com.mei.live.ui.dialog.adapter.UserToAnchorWorksCardAdapter
import com.mei.live.ui.dialog.adapter.item.CardIntroductionHolder
import com.mei.live.ui.ext.getCurrLiveState
import com.mei.live.ui.getRoomTypeForGrowingTrack
import com.mei.live.views.ExpandableTextView.StatusType
import com.mei.mentor_home_page.ui.dialog.showMentorBrowser
import com.mei.orc.ext.dip
import com.mei.orc.ext.dp
import com.mei.orc.imageload.glide.glideDisplay
import com.mei.orc.john.model.JohnUser
import com.mei.orc.ui.toast.UIToast
import com.mei.widget.gradient.applyViewDelegate
import com.mei.wood.R
import com.mei.wood.extensions.executeKt
import com.mei.wood.ui.CustomSupportFragment
import com.net.model.chick.friends.ProductBean
import com.net.model.chick.friends.WorkListResult
import com.net.model.room.RoomInfo
import com.net.model.user.DataCard
import com.net.model.user.GroupInfo
import com.net.network.chick.friends.WorkListRequest
import kotlinx.android.synthetic.main.fragment_user_to_presenter_card.*

/**
 * Created by hang on 2020/7/27.
 */
class UserToPresenterCardFragment : CustomSupportFragment() {
    private val itemList = arrayListOf<Any>()

    var workResult = WorkListResult.WorksBean()

    //作品请求页数
    private var workPage = 1

    var dataCard: DataCard = DataCard()

    //房间信息
    var roomInfo: RoomInfo = RoomInfo()

    //窗口关闭回调
    var back: (type: Int) -> Unit = {}

    //关闭窗口回调
    var dismissBack: () -> Unit = {}

    var scrollY = 0

    //资料卡类型
    var type = 0

    var managerPopWindow: PopupWindow? = null

    private val cardAdapter by lazy {
        UserToAnchorWorksCardAdapter(itemList).apply {
            setOnItemClickListener { adapter, _, position ->
                adapter.getItemOrNull(position)?.let { item ->
                    if (item is GroupInfo) {
                        growCard("工作室介绍入口")
                        Nav.toPersonalPage(activity, dataCard.memberInfo?.userId ?: 0)
                    } else if (item is ProductBean) {
                        when (getCurrLiveState()) {
                            1 -> UIToast.toast("当前正在直播中，无法查看")
                            2 -> UIToast.toast("当前正在连线中，无法查看")
                            else -> {
                                val index = workResult.list.indexOfFirst { it.seqId == item.seqId }
                                if (index > -1) {
                                    growCard("作品")
                                    activity?.showMentorBrowser(dataCard.memberInfo?.userId
                                            ?: 0, workResult, index)
                                }
                            }
                        }
                    }
                }
            }
            loadMoreModule.setOnLoadMoreListener {
                requestLoadMoreWorks()
            }
        }
    }

    private var firstWorkIndex = -1
    private val itemDecoration by lazy {
        object : RecyclerView.ItemDecoration() {
            override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
                if (firstWorkIndex > -1) {
                    val childLayoutPosition = parent.getChildLayoutPosition(view)
                    when ((childLayoutPosition - firstWorkIndex) % 3) {
                        0 -> {
                            outRect.set(0, 0, dip(1.5f), dip(1.5f))
                        }
                        2 -> {
                            outRect.set(dip(1.5f), 0, 0, dip(1.5f))
                        }
                        else -> {
                            outRect.set(0, 0, 0, dip(1.5f))
                        }
                    }
                }
            }
        }
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_user_to_presenter_card, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
        initListener()
    }

    private fun initView() {
        resetType()
        recycler_view.layoutManager = GridLayoutManager(activity, 3)
        recycler_view.adapter = cardAdapter

        /**添加工作室条目*/
        if (dataCard.groupId > 0) {
            val groupInfo = GroupInfo().apply {
                avatar = dataCard.avatar
                groupId = dataCard.groupId
                groupName = dataCard.nickname
            }
            itemList.add(groupInfo)
        }
        //添加标签和简介
        val cardIntroductionData = CardIntroductionHolder.CardIntroductionData().apply {
            isSelf = dataCard.isSelf
            isGroup = dataCard.groupId > 0
            needExpand = dataCard.worksCount > 0
            cardAdapter.statusType = if (needExpand) StatusType.STATUS_EXPAND else StatusType.STATUS_CONTRACT
            introduction = dataCard.introduction.orEmpty()
            publisherTags = if (dataCard.publisherTags.isNullOrEmpty()) {
                dataCard.skills.orEmpty().map { it.name }
            } else {
                dataCard.publisherTags.orEmpty()
            }
        }
        itemList.add(cardIntroductionData)
        workResult = dataCard.works ?: WorkListResult.WorksBean()
        workResult.worksCount = dataCard.worksCount
        dataCard.works?.list?.let {
            itemList.addAll(it)
            workPage = dataCard.works?.nextPageNo ?: 0
        }

        firstWorkIndex = itemList.indexOfFirst { it is ProductBean }
        cardAdapter.loadMoreModule.isEnableLoadMore = firstWorkIndex > -1
        recycler_view.addItemDecoration(itemDecoration)
        cardAdapter.notifyDataSetChanged()
        if (dataCard.works?.hasMore == true) {
            cardAdapter.loadMoreModule.loadMoreComplete()
        } else {
            cardAdapter.loadMoreModule.loadMoreEnd()
        }

        presenter_avatar.glideDisplay(dataCard.memberInfo?.avatar.orEmpty(), dataCard.memberInfo?.gender.genderAvatar(dataCard.isPublisher))
        presenter_name.text = dataCard.memberInfo?.nickname.orEmpty()
        presenter_name.paint.isFakeBoldText = true

        add_follow.text = if (dataCard.isFollow) "私聊" else "关注"
        add_follow_img.setImageResource(if (dataCard.isFollow) R.drawable.icon_chat_tip else R.drawable.icon_follow_add)
        follow_presenter.text = if (dataCard.isFollow) "私聊" else "关注"

        if (roomInfo.createUser?.userId == dataCard.memberInfo?.userId) {
            icon_presenter_img.setImageResource(R.drawable.icon_presenter)
            icon_presenter_layout.delegate.applyViewDelegate {
                radius = 14.dp
                startColor = ContextCompat.getColor(context, R.color.color_FBB982)
                endColor = ContextCompat.getColor(context, R.color.color_F66557)
            }
            icon_presenter.text = if (roomInfo.isStudio) "主讲人" else "咨询师"
        } else {
            icon_presenter_img.setImageResource(android.R.color.transparent)
            icon_presenter_layout.delegate.applyViewDelegate {
                radius = 14.dp
                startColor = ContextCompat.getColor(context, R.color.color_81C9FC)
                endColor = ContextCompat.getColor(context, R.color.color_7695FF)
            }
            icon_presenter.text = dataCard.memberInfo?.groupRoleName.orEmpty()
        }

        if (type == 0) {
            doHeadScrollAnim(0f)
        } else {
            doHeadScrollAnim(1f)
        }
    }

    private fun resetType() {
        type = if (roomInfo.groupRole == 0 && roomInfo.createUser?.userId == dataCard.memberInfo?.userId) {
            /**游客看主讲人*/
            0
        } else if (roomInfo.groupRole == 0 && dataCard.memberInfo?.groupRoleId != 0) {
            /**游客看工作室其他成员*/
            1
        } else if (dataCard.memberInfo?.userId == JohnUser.getSharedUser().userID) {
            /**工作室成员查看自己*/
            2
        } else {
            /**工作室成员查看其他成员*/
            3
        }
    }

    private fun requestLoadMoreWorks() {
        apiSpiceMgr.executeKt(WorkListRequest(dataCard.memberInfo?.userId
                ?: 0, workPage), success = {
            val worksBean = it.data?.list
            if (worksBean != null) {
                if(worksBean.list.isNotEmpty()) {
                    workPage = worksBean.nextPageNo
                    workResult.list.addAll(worksBean.list)
                    itemList.addAll(worksBean.list)
                    firstWorkIndex = itemList.indexOfFirst { it is ProductBean }
                    cardAdapter.notifyItemRangeInserted(itemList.size - worksBean.list.size, worksBean.list.size)
                }
                if (worksBean.hasMore) {
                    cardAdapter.loadMoreModule.loadMoreComplete()
                } else {
                    cardAdapter.loadMoreModule.loadMoreEnd()
                }
            } else {
                cardAdapter.loadMoreModule.loadMoreFail()
            }
        }, failure = {
            cardAdapter.loadMoreModule.loadMoreFail()
        })
    }

    fun growCard(clickType: String) {
        val viewType = if (workResult.list.size > 0 && dataCard.introduction.orEmpty().isNotEmpty()) {
            "有简介有作品"
        } else if (workResult.list.size > 0) {
            "有简介无作品"
        } else if (dataCard.introduction.orEmpty().isNotEmpty()) {
            "无简介有作品"
        } else {
            "无简介无作品"
        }
        GrowingUtil.track("personal_datacard_click", "room_id", roomInfo.roomId, "user_id", roomInfo.createUser?.userId.toString(),
                "room_type", roomInfo.getRoomTypeForGrowingTrack(), "click_type", clickType, "view_type", viewType)
    }
}