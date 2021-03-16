package com.mei.live.ui.dialog.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.mei.base.common.FOLLOW_USER_STATE
import com.mei.base.ui.nav.Nav
import com.mei.dialog.showReportDialog
import com.mei.im.ui.dialog.showChatDialog
import com.mei.im.ui.view.GridSpacingItemDecoration
import com.mei.live.LiveEnterType
import com.mei.live.manager.genderAvatar
import com.mei.live.ui.VideoLiveRoomActivity
import com.mei.live.ui.VideoLiveRoomActivityLauncher
import com.mei.live.ui.dialog.DATA_CARD_FROM_BILLBOARD
import com.mei.live.ui.dialog.adapter.UserToAnchorWorksCardAdapter
import com.mei.live.ui.dialog.adapter.item.BillboardOperateWrapper
import com.mei.live.ui.dialog.adapter.item.CardIntroductionHolder
import com.mei.live.ui.dialog.adapter.item.OperatingType
import com.mei.live.ui.dialog.linstener.IOperatingListener
import com.mei.live.ui.dialog.showSendGiftDialog
import com.mei.live.ui.ext.*
import com.mei.live.views.ExpandableTextView.CardExpandableTextView
import com.mei.live.views.ExpandableTextView.StatusType
import com.mei.live.views.followFriend
import com.mei.mentor_home_page.ui.dialog.showMentorBrowser
import com.mei.orc.event.bindAction
import com.mei.orc.ext.dip
import com.mei.orc.imageload.glide.glideDisplay
import com.mei.orc.ui.toast.UIToast
import com.mei.orc.util.click.setOnClickNotDoubleListener
import com.mei.wood.R
import com.mei.wood.ui.CustomSupportFragment
import com.mei.wood.ui.MeiCustomActivity
import com.net.model.chick.friends.ProductBean
import com.net.model.chick.friends.WorkListResult
import com.net.model.room.RoomInfo
import com.net.model.rose.RoseFromSceneEnum
import com.net.model.user.DataCard
import kotlinx.android.synthetic.main.fragment_works_use_see_pub_cards.*
import androidx.recyclerview.widget.RecyclerView as RecyclerView1

/**
 *
 * @ProjectName:    dove
 * @Package:        com.mei.live.ui.dialog.fragment
 * @ClassName:      UserToAnchorWroksCardFragment
 * @Description:   用户查看主播有做作品或者简介以及标签
 * @Author:         zxj
 * @CreateDate:     2020/6/10 17:00
 * @UpdateUser:
 * @UpdateDate:     2020/6/10 17:00
 * @UpdateRemark:
 * @Version:
 */
@Suppress("WHEN_ENUM_CAN_BE_NULL_IN_JAVA")
class UserToAnchorWorksCardFragment : CustomSupportFragment() {
    //ITEM 信息
    val listItemInfo = arrayListOf<Any>()
    var userInfo: DataCard = DataCard()

    var from: String = ""

    //关闭窗口回调
    var dismissBack: () -> Unit = {}

    //房间信息
    var roomInfo: RoomInfo = RoomInfo()

    //窗口关闭回调
    var back: (type: Int) -> Unit = {}

    //作品请求页数
    var workPage = 1

    //操作行为
    val operatingType = OperatingType()
    var workResult = WorkListResult.WorksBean()

    //标签或者简介信息
    private var mCardIntroductionData = CardIntroductionHolder.CardIntroductionData()

    //是否有加载更更多
    var hasMore = false
    private val itemDecoration by lazy {
        val rude = if ((userInfo.introduction.orEmpty().isNotEmpty() || userInfo.publisherTags.orEmpty().isNotEmpty() || userInfo.skills.orEmpty().isNotEmpty()) && !userInfo.isSelf) 2 else 1
        activity?.let { GridSpacingItemDecoration(3, dip(1f), rude, false) }
    }

    val cardAdapter by lazy {
        UserToAnchorWorksCardAdapter(listItemInfo).apply {
            mOnExpandOrContractClickListener = CardExpandableTextView.OnExpandOrContractClickListener {
                when (it) {
                    StatusType.STATUS_EXPAND -> {
                        mCardIntroductionData.needExpand = false
                        notifyDataSetChanged()

                    }
                    StatusType.STATUS_CONTRACT -> {
                        mCardIntroductionData.needExpand = true
                        notifyDataSetChanged()
                        recycle_card_dialog.smoothScrollToPosition(0)
                    }
                }
            }
            //关注，私聊，送礼
            iOperatingAdapterListener = object : IOperatingListener {
                override fun setGift() {
                    back(1)
                    growCard("送礼")
                    activity?.showSendGiftDialog(userInfo.userId, userInfo.nickname.orEmpty(), (activity as? VideoLiveRoomActivity)?.roomId.orEmpty(), roseFromScene = RoseFromSceneEnum.COMMON_ROOM, fromType = "主播资料卡送礼")
                }

                override fun setFocus() {
                    (activity as? MeiCustomActivity)?.followFriend(userInfo.userId, 1, (activity as? VideoLiveRoomActivity)?.roomId
                            ?: "") {
                        userInfo.isFollow = it
                        operatingType.isFocus = it
                        notifyItemChanged(0)
                        growCard("关注")
                        tv_card_follow_anchor.apply {
                            text = if (it || from == DATA_CARD_FROM_BILLBOARD) "私聊" else "关注"
                        }
                    }
                }

                override fun setAT() {
                    back(0)
                    dismissBack()
                    growCard("@TA")
                }

                override fun setIM() {
                    (activity as? VideoLiveRoomActivity)?.showChatDialog(userInfo.userId.toString(), "CreatorDataCard")
                    dismissBack()
                }
            }
            setOnItemClickListener { adapter, _, position ->
                val item = adapter.getItemOrNull(position)
                if (item is ProductBean) {
                    when (getCurrLiveState()) {
                        1 -> UIToast.toast("当前正在直播中，无法查看")
                        2 -> UIToast.toast("当前正在连线中，无法查看")
                        else -> {
                            val index = workResult.list.indexOfFirst { it.seqId == item.seqId }
                            if (index > -1) {
                                growCard("作品")
                                activity?.showMentorBrowser(userInfo.userId, workResult, index)
                            }
                        }
                    }
                }
            }
            /**
             * 加载更多
             */
            loadMoreModule.isEnableLoadMore = false
            loadMoreModule.setOnLoadMoreListener {
                requestLoadMoreWorks()
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_works_use_see_pub_cards, container, false)

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (userInfo.isSelf) {
            /**主播自己看自己*/
            tv_card_follow_anchor.isVisible = false
        } else {
            if (from == DATA_CARD_FROM_BILLBOARD) {
                listItemInfo.add(BillboardOperateWrapper(userInfo.userId))

                more_btn.isVisible = false
                live_state.isVisible = userInfo.onlineStatus == 1
            } else {
                operatingType.isFocus = userInfo.isFollow
                tv_card_follow_anchor.isVisible = true
                listItemInfo.add(operatingType)

                more_btn.isVisible = true
                live_state.isVisible = false
            }
        }
        //添加标签和简介
        mCardIntroductionData = CardIntroductionHolder.CardIntroductionData().apply {
            isSelf = userInfo.isSelf
            needExpand = userInfo.worksCount > 0
            cardAdapter.statusType = if (needExpand) StatusType.STATUS_EXPAND else StatusType.STATUS_CONTRACT
            introduction = userInfo.introduction.orEmpty()
            publisherTags = if (userInfo.publisherTags.orEmpty().isEmpty()) {
                val skill = arrayListOf<String>()
                userInfo.skills?.forEach {
                    skill.add(it.name)
                }
                skill
            } else {
                userInfo.publisherTags.orEmpty()
            }
        }
        listItemInfo.add(mCardIntroductionData)
        workResult = userInfo.works ?: WorkListResult.WorksBean()
        workResult.worksCount = userInfo.worksCount
        userInfo.works?.list?.let {
            listItemInfo.addAll(it)
            workPage = userInfo.works.nextPageNo
            hasMore = userInfo.works.hasMore

        }
        recycle_card_dialog.layoutManager = GridLayoutManager(activity, 3)
        itemDecoration?.let { recycle_card_dialog.addItemDecoration(it) }
        tv_card_dialog_use_name.paint.isFakeBoldText = true
        recycle_card_dialog.adapter = cardAdapter
        tv_card_dialog_use_name.text = userInfo.nickname.orEmpty()
        card_image_head.glideDisplay(userInfo.avatar.orEmpty(), userInfo.gender.genderAvatar(userInfo.isPublisher))
        //关注或者私聊按钮
        listener()
    }

    /**
     * 事件
     */
    private fun listener() {
        /**
         * 主播头像
         */
        fl_head_dialog_card.setOnClickListener {
            growCard("主播头像")
            Nav.toPersonalPage(activity, userInfo.userId)
        }
        /**
         * 私聊或者关注
         */
        tv_card_follow_anchor.apply {
            text = if (userInfo.isFollow || from == DATA_CARD_FROM_BILLBOARD) {
                "私聊"
            } else {
                "关注"
            }
            setOnClickListener {
                if (userInfo.isFollow || from == DATA_CARD_FROM_BILLBOARD) {
                    growCard("私聊")
                    activity.showChatDialog(userInfo.userId.toString(), "CreatorDataCard")
                    dismissBack()
                } else {
                    growCard("关注")
                    (activity as? MeiCustomActivity)?.followFriend(userInfo.userId, 1, roomInfo.roomId) {
                        userInfo.isFollow = it
                        operatingType.isFocus = it
                        cardAdapter.notifyDataSetChanged()
                        text = if (it) {
                            "私聊"
                        } else {
                            "私聊"
                        }
                    }
                }
            }
        }
        /** 关注更新**/
        bindAction<Pair<Int, Boolean>>(FOLLOW_USER_STATE) {
            if (it != null && it.first == roomInfo.createUser?.userId) {
                userInfo.isFollow = it.second
                operatingType.isFocus = it.second
                cardAdapter.notifyDataSetChanged()
                tv_card_follow_anchor.text = if (it.second || from == DATA_CARD_FROM_BILLBOARD) "私聊" else "关注"
            }
        }
        /** 关闭**/
        tb_dimss.setOnClickListener {
            dismissBack()
        }
        /** 举报 **/
        more_btn.setOnClickListener {
            activity?.showUserToShieldingDialog(roomInfo, more_btn, back = { type ->
                when (type) {
                    // 直播间举报
                    UserToMentorOptionsType.REPORT.name -> {
                        activity?.showReportDialog(userInfo.userId, roomInfo.roomId) {
                            if (it == 1) {
                                dismissBack()
                            }
                        }
                    }
                    UserToMentorOptionsType.SHIELDING.name -> {
                        activity?.shieldingDialog("是否确定拉黑 ${userInfo.nickname.orEmpty()}?", userInfo.userId.toString(), roomInfo.roomId, back, dialogMiss = {
                            //拉黑成功退出直播间
                            if (roomInfo.createUser?.userId == userInfo.userId) {
                                activity?.finish()
                            }
                        })
                    }
                }
            })
        }
        live_state.setOnClickNotDoubleListener {
            when (getCurrLiveState()) {
                1 -> UIToast.toast("当前正在直播中，无法查看")
                2 -> UIToast.toast("当前正在连线中，无法查看")
                else ->
                    VideoLiveRoomActivityLauncher.startActivity(activity, userInfo.roomId, LiveEnterType.room_data_card)
            }
        }


        /**
         * RecyclerView 滚动监听 改变头部大小
         */
        val maxDistance = dip(50)
        val maxTitleHeight = dip(10)
        val maxImage = dip(40)
        val martTop = dip(26)
        recycle_card_dialog.addOnScrollListener(object : RecyclerView1.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView1, dx: Int, dy: Int) {
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val position = layoutManager.findFirstVisibleItemPosition()
                if (position == 0) {
                    val firstView = layoutManager.findViewByPosition(position)
                    val scrollY = -(firstView?.top ?: 0)
                    val percent = scrollY * 1f / maxDistance
                    if (percent <= 1) {
                        setLayout(percent, maxTitleHeight, maxImage, martTop)
                    } else {
                        restoreLayout()
                    }
                } else {
                    //标题栏变宽
                    restoreLayout()

                }
            }
        })
    }

}

