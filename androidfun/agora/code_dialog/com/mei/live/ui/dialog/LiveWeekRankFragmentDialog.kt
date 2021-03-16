package com.mei.live.ui.dialog

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.mei.GrowingUtil
import com.mei.live.ext.createSplitSpannable
import com.mei.live.ext.parseColor
import com.mei.live.ui.VideoLiveRoomActivity
import com.mei.live.ui.dialog.adapter.LiveWeekRankListAdapter
import com.mei.live.ui.fragment.appendRoomHalfScreenParamsUrl
import com.mei.live.views.performApply
import com.mei.orc.Cxt
import com.mei.orc.dialog.BottomDialogFragment
import com.mei.orc.ext.dip
import com.mei.orc.imageload.glide.glideDisplay
import com.mei.wood.R
import com.mei.wood.cache.requestUserInfo
import com.mei.wood.ext.AmanLink
import com.mei.wood.extensions.executeKt
import com.mei.wood.ui.MeiCustomActivity
import com.mei.wood.util.MeiUtil
import com.net.model.room.RoomInfo
import com.net.model.room.RoomWeekRank
import com.net.model.room.RoomWeekRankList.WeekRank
import com.net.network.room.RoomWeekRankRequest
import kotlinx.android.synthetic.main.dialog_live_week_rank.*
import kotlinx.android.synthetic.main.view_live_week_rank_bottom.*

/**
 *
 * @author Created by lenna on 2020/9/9
 */


fun FragmentActivity.showLiveWeekRankFragmentDialog(roomInfo: RoomInfo) {
    LiveWeekRankFragmentDialog().apply {
        this.roomInfo = roomInfo
        show(supportFragmentManager, "LiveWeekRankFragmentDialog")
    }
}


class LiveWeekRankFragmentDialog : BottomDialogFragment() {
    var roomInfo: RoomInfo? = null
    private var roomWeekRank: RoomWeekRank? = null
    private var data = arrayListOf<Any>()
    private val adapter by lazy {
        LiveWeekRankListAdapter(data)
                .apply {
                    loadMoreModule.setOnLoadMoreListener {
                        //加载更多
                        loadMoreData()
                    }
                    setOnItemClickListener { _, _, position ->
                        //点击item
                        (data.getOrNull(position) as? WeekRank)?.let {
                            activity?.showRankUserInfoDialog(it.userId.toInt(), back = { type, info ->
                                if (type == 0) {
                                    (activity as? VideoLiveRoomActivity)?.let {
                                        it.apiSpiceMgr.requestUserInfo(arrayOf(info.userId), true) { list ->
                                            list.firstOrNull()?.let { info ->
                                                it.showInputKey(info)
                                            }
                                        }
                                    }
                                    dismiss()
                                }
                            })
                        }
                    }
                }
    }

    override fun onSetInflaterLayout(): Int {
        return R.layout.dialog_live_week_rank
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        getWeekRank()
    }

    private fun initView() {
        container_dialog_view.updateLayoutParams {
            height = dip(480)
        }
        week_rank_rlv.adapter = adapter
        week_rank_rlv.layoutManager = LinearLayoutManager(activity)
        week_rank_error_epv.setBtnClickListener(View.OnClickListener {
            getWeekRank()
        })
        week_rank_error_epv.setTopMargin(dip(40))
        try {
            GrowingUtil.track("function_view",
                    "function_name", "知心周榜弹窗",
                    "function_page", "直播间")
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }


    private fun getWeekRank() {
        (activity as? MeiCustomActivity)?.showLoading(true)
        (activity as? MeiCustomActivity)?.apiSpiceMgr?.executeKt(RoomWeekRankRequest(roomInfo?.roomId, 1), success = {
            if (it.isSuccess) {
                if (isAdded) {
                    week_rank_error_epv.isVisible = false
                    week_rank_rlv.isVisible = true
                    roomWeekRank = it.data
                    data.clear()
                    data.addAll(roomWeekRank?.rank?.list ?: arrayListOf())
                    week_rank_title_tv.text = it.data.rankTitle
                    loadBottomView()
                    if (it.data.rank?.hasMore == true) {
                        adapter.loadMoreModule.loadMoreComplete()
                    } else {
                        adapter.loadMoreModule.loadMoreEnd()
                        adapter.loadMoreModule.isEnableLoadMore = false
                        data.add(it.data.rankTips.orEmpty())
                    }
                    adapter.notifyDataSetChanged()
                }

            } else {
                if (isAdded) {
                    week_rank_error_epv.isVisible = true
                    week_rank_rlv.isVisible = false
                }
            }
        }, failure = {
            if (isAdded) {
                week_rank_error_epv.isVisible = true
                week_rank_rlv.isVisible = false
            }
        }, finish = {
            (activity as? MeiCustomActivity)?.showLoading(false)
        })
    }

    /**
     * 加载更多
     */
    private fun loadMoreData() {
        (activity as? MeiCustomActivity)?.showLoading(true)
        (activity as? MeiCustomActivity)?.apiSpiceMgr?.executeKt(RoomWeekRankRequest(roomInfo?.roomId, roomWeekRank?.rank?.nextPageNo
                ?: -1), success = {
            if (it.isSuccess) {
                if (isAdded) {
                    data.addAll(it.data.rank?.list ?: arrayListOf())
                    roomWeekRank?.rank?.list?.addAll(it.data.rank?.list ?: arrayListOf())
                    if (it.data.rank?.hasMore == true) {
                        adapter.loadMoreModule.loadMoreComplete()
                    } else {
                        adapter.loadMoreModule.loadMoreEnd()
                        adapter.loadMoreModule.isEnableLoadMore = false
                        data.add(it.data.rankTips.orEmpty())
                    }
                    roomWeekRank?.rank?.nextPageNo = it.data.rank?.nextPageNo
                    roomWeekRank?.rank?.hasMore = it.data.rank?.hasMore
                    adapter.notifyDataSetChanged()
                }

            } else {
                if (isAdded) {
                    adapter.loadMoreModule.loadMoreFail()
                }
            }
        }, failure = {
            if (isAdded) {
                adapter.loadMoreModule.loadMoreFail()
            }
        }, finish = {
            (activity as? MeiCustomActivity)?.showLoading(false)
        })
    }

    @SuppressLint("SetTextI18n")
    private fun loadBottomView() {
        if (isAdded) {
            live_week_rank_bottom_view.isVisible = true

            ranking_number_tv.apply {
                text = roomWeekRank?.currentPublisher?.rank.toString()
                setTextColor(roomWeekRank?.currentPublisher?.publisherInfo?.color.orEmpty()
                        .parseColor(Cxt.getColor(R.color.color_D8D8D8)))
            }
            week_rank_crown_iv.apply {
                glideDisplay(roomWeekRank?.currentPublisher?.publisherInfo?.showImage.orEmpty())
                isVisible = roomWeekRank?.currentPublisher?.publisherInfo?.showImage?.isNotEmpty() == true
            }
            week_rank_avatar_riv.apply {
                glideDisplay(roomWeekRank?.currentPublisher?.publisherInfo?.avatar.orEmpty())
                viewStrokeWidth = if (roomWeekRank?.currentPublisher?.publisherInfo?.color?.isNotEmpty() == true)
                    dip(2) * 1f
                else dip(0) * 1f
                viewStrokeColor = roomWeekRank?.currentPublisher?.publisherInfo?.color.orEmpty()
                        .parseColor(Cxt.getColor(android.R.color.white))
            }
            week_rank_name_tv.text = roomWeekRank?.currentPublisher?.publisherInfo?.nickname.orEmpty()
            week_rank_name_tv.paint.isFakeBoldText = true

            receive_coin_tv.text = roomWeekRank?.weekRankHint
            receive_coin_tv.isVisible = roomWeekRank?.weekRankHint.orEmpty().isNotEmpty()

            bottom_receive_coin_tv.apply {
                text = roomWeekRank?.currentPublisher?.coinCount?.createSplitSpannable(Cxt.getColor(R.color.color_999999))
            }
            week_rank_bottom_options_gfl.isVisible = roomWeekRank?.applyOptionDtoItem?.title?.isNotEmpty() == true
            /**扩大点击范围*/
            week_rank_bottom_options_v.isVisible = roomWeekRank?.applyOptionDtoItem?.title?.isNotEmpty() == true
            week_rank_bottom_options_tv.text = roomWeekRank?.applyOptionDtoItem?.title.orEmpty()
            bottom_v.setOnClickListener {
                activity?.showRankUserInfoDialog(roomWeekRank?.currentPublisher?.publisherId
                        ?: 0, back = { type, info ->
                    if (type == 0) {
                        (activity as? VideoLiveRoomActivity)?.let {
                            it.apiSpiceMgr.requestUserInfo(arrayOf(info.userId), true) { list ->
                                list.firstOrNull()?.let { info ->
                                    it.showInputKey(info)
                                }
                            }
                        }
                        dismiss()
                    }
                })
            }
            week_rank_bottom_options_v.setOnClickListener {
                /**点击 操作按钮*/
                when (roomInfo?.isSpecialStudio) {
                    true -> {
                        if (roomInfo?.recommend != null) {
                            roomInfo?.let {
                                val loadUrl = activity?.appendRoomHalfScreenParamsUrl(MeiUtil.appendParamsUrl(AmanLink.NetUrl.exclusive_service,
                                        "service_id" to it.recommend?.specialServiceId.toString(), "publisher_id" to roomInfo?.publisherId.toString()), it).orEmpty()
                                activity?.showExclusiveServiceDialog(loadUrl, roomInfo?.roomId.orEmpty())
                            }
                        } else if (roomInfo?.recommendCourse != null) {
                            roomInfo?.let {
                                val loadUrl = activity?.appendRoomHalfScreenParamsUrl(MeiUtil.appendParamsUrl(AmanLink.NetUrl.exclusive_service,
                                        "service_id" to it.recommendCourse?.courseId.toString(), "publisher_id" to roomInfo?.publisherId.toString()), it).orEmpty()
                                activity?.showExclusiveServiceDialog(loadUrl, roomInfo?.roomId.orEmpty())
                            }
                        }
                    }
                    else -> {
                        activity?.performApply()
                    }
                }
            }
        }

    }
}