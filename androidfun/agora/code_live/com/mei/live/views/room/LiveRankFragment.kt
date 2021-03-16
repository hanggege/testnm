package com.mei.live.views.room

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.joker.im.bindEventLifecycle
import com.joker.im.custom.CustomType
import com.joker.im.custom.chick.ChickCustomData
import com.joker.im.listener.IMAllEventManager
import com.joker.im.mapToMeiMessage
import com.joker.im.message.CustomMessage
import com.mei.GrowingUtil
import com.mei.live.ui.dialog.showContributeDialog
import com.mei.live.ui.dialog.showLiveWeekRankFragmentDialog
import com.mei.live.ui.ext.formatContribute
import com.mei.live.ui.getRoomTypeForGrowingTrack
import com.mei.orc.util.click.setOnClickNotDoubleListener
import com.mei.wood.R
import com.mei.wood.extensions.executeKt
import com.mei.wood.ui.CustomSupportFragment
import com.mei.wood.util.logDebug
import com.net.model.room.RoomInfo
import com.net.network.room.CoinReceiveRequest
import com.net.network.room.RoomWeekRankRefreshRequest
import com.tencent.imsdk.TIMMessage
import kotlinx.android.synthetic.main.view_live_rank.*

/**
 * Created by hang on 2020/11/16.
 */
class LiveRankFragment : CustomSupportFragment() {

    var roomInfo: RoomInfo = RoomInfo()
        set(value) {
            field = value
            if(isAdded) {
                initView()
            }
        }

    private val checkNewEvent: IMAllEventManager by lazy {
        object : IMAllEventManager() {
            /** 监听最新消息 **/
            @SuppressLint("SetTextI18n")
            override fun onNewMessages(msgs: MutableList<TIMMessage>?): Boolean {
                val customList = msgs.orEmpty()
                        .mapNotNull { it.mapToMeiMessage() as? CustomMessage }
                        /** 只接收群消息与当前知心达人的消息 **/
                        .filter { it.peer == roomInfo.roomId || it.peer == roomInfo.createUser?.userId?.toString() }
                customList.forEach {
                    handleIMEvent(it)
                }
                return super.onNewMessages(msgs)
            }
        }
    }

    private fun handleIMEvent(msg: CustomMessage) {
        val type = msg.customMsgType
        logDebug("liveTop_handleIMEvent: ${type.name}")
        (msg.customInfo?.data as? ChickCustomData)?.let { data ->
            when (type) {
                CustomType.publisher_coin_changed -> {
                    refreshContributionView(data.receiveCount)
                }
                CustomType.week_rank_changed -> {
                    if (data.roomId == roomInfo.roomId) {
                        roomInfo.weekRankText = data.weekRankText
                        refreshWeekRank()
                    }
                }
                CustomType.send_gift -> {
                    requestContributionData()
                }
                else -> {
                }
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.view_live_rank, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initEvent()

        checkNewEvent.bindEventLifecycle(this)
        if (roomInfo.roomId.isNotEmpty()) {
            initView()
        }
    }

    private fun initView() {
        refreshContributionView(roomInfo.receiveCount)
        refreshWeekRank()
        requestRefreshWeekRank()
    }

    private fun initEvent() {
        fan_contribute.setOnClickNotDoubleListener {
            GrowingUtil.track(
                    "list_popup_view",
                    "room_id", roomInfo.roomId.orEmpty(),
                    "broadcast_id", roomInfo.broadcastId.orEmpty(),
                    "user_id", roomInfo.publisherId.toString(),
                    "room_type", roomInfo.getRoomTypeForGrowingTrack()
            )
            activity?.showContributeDialog(roomInfo.publisherId.toString())
        }

        room_week_rank.setOnClickListener {
            roomInfo.let { info -> activity?.showLiveWeekRankFragmentDialog(info) }
        }
    }

    private fun refreshContributionView(receiveCount: Long) {
        fan_contribute.text = formatContribute(receiveCount, "w")
        fan_contribute.isVisible = receiveCount > 0 && !roomInfo.isSpecialStudio
    }

    /**刷新周榜*/
    var refreshWeekRankRunnable: Runnable? = null
    private var refreshTime: Long = 10L
    private fun requestRefreshWeekRank() {
        refreshWeekRankRunnable = null
        if (isAdded) {
            val run = Runnable {
                requestRefreshWeekRank()
                refreshWeekRankRunnable = null
            }
            refreshWeekRankRunnable = run
            apiSpiceMgr.executeKt(RoomWeekRankRefreshRequest(roomInfo.roomId), success = {
                if (isAdded) {
                    if (it.isSuccess) {
                        roomInfo.weekRankText = it.data.weekRankText
                        refreshWeekRank()
                    }
                }
                refreshTime = it.data.delayTime
                if (refreshTime > 0)
                    room_week_rank.postDelayed(run, refreshTime * 1000L)
            }, failure = {
                room_week_rank.postDelayed(run, refreshTime * 1000L)
            })
        }
    }

    /**刷新周榜*/
    private fun refreshWeekRank() {
        room_week_rank.text = roomInfo.weekRankText.orEmpty()
        room_week_rank.isVisible = roomInfo.weekRankText.orEmpty().isNotEmpty()
        GrowingUtil.track("function_view",
                "function_name", "知心周榜入口",
                "function_page", "直播间")
    }

    /**
     * 请求贡献榜数据
     */
    private fun requestContributionData() {
        apiSpiceMgr.executeKt(CoinReceiveRequest(roomInfo.publisherId), success = {
            it.data?.let { data ->
                refreshContributionView(data.receiveCount)
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        room_week_rank.removeCallbacks(refreshWeekRankRunnable)
    }

}