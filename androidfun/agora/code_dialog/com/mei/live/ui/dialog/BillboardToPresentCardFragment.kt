package com.mei.live.ui.dialog

import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.core.view.setMargins
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mei.GrowingUtil
import com.mei.base.ui.nav.Nav
import com.mei.live.LiveEnterType
import com.mei.live.ui.VideoLiveRoomActivityLauncher
import com.mei.live.ui.dialog.adapter.UserToAnchorWorksCardAdapter
import com.mei.live.ui.dialog.adapter.item.CardIntroductionHolder
import com.mei.live.ui.ext.getCurrLiveState
import com.mei.live.ui.getRoomTypeForGrowingTrack
import com.mei.live.views.ExpandableTextView.StatusType
import com.mei.mentor_home_page.ui.dialog.showMentorBrowser
import com.mei.orc.ext.dip
import com.mei.orc.imageload.glide.glideDisplay
import com.mei.orc.ui.toast.UIToast
import com.mei.orc.util.click.setOnClickNotDoubleListener
import com.mei.wood.R
import com.mei.wood.extensions.executeKt
import com.mei.wood.ui.CustomSupportFragment
import com.net.model.chick.friends.ProductBean
import com.net.model.chick.friends.WorkListResult
import com.net.model.room.RoomInfo
import com.net.model.user.DataCard
import com.net.network.chick.friends.WorkListRequest
import kotlinx.android.synthetic.main.fragment_billboard_to_presenter_card.*

/**
 * Created by hang on 2020/9/9.
 */
class BillboardToPresentCardFragment : CustomSupportFragment() {

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

    private val cardAdapter by lazy {
        UserToAnchorWorksCardAdapter(itemList).apply {
            setOnItemClickListener { adapter, _, position ->
                adapter.getItemOrNull(position)?.let { item ->
                    if (item is ProductBean) {
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
            inflater.inflate(R.layout.fragment_billboard_to_presenter_card, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
        getSmallNameTopMargin()
        initListener()
    }

    private fun getSmallNameTopMargin(): Int {
        val fontMetrics = studio_name.paint.fontMetricsInt
        val nameHeight = fontMetrics.bottom - fontMetrics.top
        return (dip(40) - nameHeight) / 2
    }

    private fun initListener() {
        head_layout.setOnClickNotDoubleListener {
            growCard("工作室介绍入口")
            Nav.toPersonalPage(activity, dataCard.userId)
        }

        view?.setOnClickListener { dismissBack() }

        recycler_view.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (isAdded) {
                    scrollY += dy
                    if (scrollY <= dip(50)) {
                        println("percent:${scrollY.toFloat() / dip(50)}")
                        doHeadScrollAnim(scrollY.toFloat() / dip(50))
                    } else {
                        doHeadScrollAnim(1f)
                    }
                }
            }
        })

        live_state.setOnClickNotDoubleListener {
            when (getCurrLiveState()) {
                1 -> UIToast.toast("当前正在直播中，无法查看")
                2 -> UIToast.toast("当前正在连线中，无法查看")
                else ->
                    VideoLiveRoomActivityLauncher.startActivity(activity, dataCard.roomId, LiveEnterType.room_data_card)
            }
        }
    }

    private fun doHeadScrollAnim(percent: Float) {
        studio_cover.updateLayoutParams<ViewGroup.MarginLayoutParams> {
            setMargins(dip(15) - (dip(5) * percent).toInt())
            width = dip(90) - (dip(50) * percent).toInt()
            height = dip(90) - (dip(50) * percent).toInt()
        }

        studio_name.textSize = 18 - 3 * percent
        studio_name.updateLayoutParams<ViewGroup.MarginLayoutParams> {
            marginStart = dip(15) - (dip(5) * percent).toInt()
            topMargin = dip(12) - ((dip(12) - getSmallNameTopMargin()) * percent).toInt()
        }

        studio_introduce.isGone = percent > 0.5f
        studio_introduce_small.isVisible = percent > 0.5f
        live_state.isGone = percent > 0.5f || dataCard.onlineStatus != 1
    }

    private fun initView() {
        recycler_view.layoutManager = GridLayoutManager(activity, 3)
        recycler_view.adapter = cardAdapter

        /**添加工作室条目*/
        studio_name.paint.isFakeBoldText = true
        studio_cover.glideDisplay(dataCard.avatar.orEmpty(), R.drawable.default_studio_cover)
        studio_name.text = dataCard.nickname.orEmpty()


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

    }

    private fun requestLoadMoreWorks() {
        apiSpiceMgr.executeKt(WorkListRequest(dataCard.memberInfo?.userId
                ?: 0, workPage), success = {
            val worksBean = it.data?.list
            if (worksBean != null) {
                if (worksBean.list.isNotEmpty()) {
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

    private fun growCard(clickType: String) {
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