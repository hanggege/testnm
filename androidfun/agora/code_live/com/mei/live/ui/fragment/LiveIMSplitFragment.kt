package com.mei.live.ui.fragment

import android.annotation.SuppressLint
import android.graphics.*
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.buildSpannedString
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.commit
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.joker.im.bindEventLifecycle
import com.joker.im.custom.CustomType
import com.joker.im.custom.chick.ChickCustomData
import com.joker.im.listener.IMAllEventManager
import com.joker.im.mapToMeiMessage
import com.joker.im.message.CustomMessage
import com.joker.im.newMessages
import com.mei.agora.event.AgoraEventHandler
import com.mei.base.common.ROOM_TYPE_CHANGE
import com.mei.base.common.UPSTREAM_ACTION
import com.mei.base.ui.nav.Nav
import com.mei.base.weight.recyclerview.manager.WrapLayoutManager
import com.mei.im.ext.isCmdId
import com.mei.live.ui.VideoLiveRoomActivity
import com.mei.live.ui.adapter.LiveIMMessageAdapter
import com.mei.live.ui.ext.LiveIMChatReceiver
import com.mei.live.views.LiveApplyUpstreamFragment
import com.mei.orc.ActivityLauncher
import com.mei.orc.event.bindAction
import com.mei.orc.ext.dip
import com.mei.orc.ext.getIndexOrNull
import com.mei.orc.ext.screenHeight
import com.mei.orc.ext.subListByIndex
import com.mei.orc.john.model.JohnUser
import com.mei.wood.R
import com.mei.wood.cache.getCacheUserInfos
import com.mei.wood.cache.requestUserInfo
import com.mei.wood.extensions.appendLink
import com.mei.wood.ui.CustomSupportFragment
import com.mei.wood.util.logDebug
import com.net.model.chick.tab.tabbarConfig
import com.net.model.room.RoomInfo
import com.net.model.room.RoomRedPacketResult
import com.net.model.room.RoomType
import com.tencent.imsdk.TIMMessage
import com.tencent.imsdk.TIMValueCallBack
import kotlinx.android.synthetic.main.fragment_live_video_split_layout.*
import kotlinx.android.synthetic.main.layout_live_im_split_bottom.*
import kotlinx.android.synthetic.main.live_im_split_layout.*
import java.util.*

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2019-12-02
 */

@SuppressLint("SetTextI18n")
class LiveIMSplitFragment : CustomSupportFragment(), AgoraEventHandler {
    var showKeyBoard: () -> Unit = {}
    var roomType: RoomType = RoomType.COMMON
    var serviceId = 0
    val tabBar = tabbarConfig
    var mPacketCouponInfo: RoomRedPacketResult = RoomRedPacketResult()

    var roomInfo: RoomInfo = RoomInfo()
        set(value) {
            field = value
            chatMsgReceiver.identify = value.roomId
            initView()
            checkLiveInitIM()
        }

    val msgList: ArrayList<CustomMessage> = arrayListOf()
    val imAdapter: LiveIMMessageAdapter by lazy {
        LiveIMMessageAdapter(this, msgList).apply {
            setOnItemClickListener { _, _, position ->
                val item = msgList.getIndexOrNull(position)?.customInfo?.data as? ChickCustomData
                val action = item?.action.orEmpty()
                if (action.isNotEmpty()) {
                    Nav.toAmanLink(activity, action)
                }
            }
            setOnItemChildLongClickListener { _, view, position ->
                msgList.getOrNull(position)?.let { adapterItemChildLongClick(it, view) }
                true
            }
        }
    }

    val packetCouponList: ArrayList<Any> = arrayListOf()

    /**
     * 处理IM推送的事件
     */
    var leaveRunnable: Runnable? = null
    var imWelcomeRunnable: Runnable? = null

    private val checkNewEvent: IMAllEventManager by lazy {
        object : IMAllEventManager() {
            /** 监听最新消息 **/
            override fun onNewMessages(msgs: MutableList<TIMMessage>?): Boolean {
                val customList = msgs.orEmpty()
                        .mapNotNull { it.mapToMeiMessage() as? CustomMessage }
                        .filter { !it.isDeleted() }//去掉已删除的消息

                /** 只接收群消息与发送指令的消息 **/
                customList.filter { it.peer == roomInfo.roomId || it.peer.toIntOrNull().isCmdId() }
                        .forEach { handleIMEvent(it) }
                return super.onNewMessages(msgs)
            }
        }
    }
    var unReadMessageNum = 0

    // 历史删除消息回显，有时用户退出直播间后，管理员进行删除消息无法删除，需要之后加载新消息时强制删除
    var deleteCache = mutableSetOf<String>()
    val chatMsgReceiver by lazy {
        LiveIMChatReceiver(this, loginSuccess = {
            conversation.getMessage(50, null, object : TIMValueCallBack<MutableList<TIMMessage>> {
                override fun onSuccess(list: MutableList<TIMMessage>?) {
                    val lastIndex = list.orEmpty().indexOfFirst { timMsgList.firstOrNull()?.msgId == it.msgId }
                    if (lastIndex > 0) newMessages(list.subListByIndex(lastIndex).reversed())
                }

                override fun onError(p0: Int, p1: String?) {

                }

            })
        }) { msgs, isNewMsg ->
            if (msgs.isEmpty()) return@LiveIMChatReceiver
            val layoutManager = if (isAdded) (im_recycler_view.layoutManager as? LinearLayoutManager) else null
            val lastVisiblePosition = layoutManager?.findLastVisibleItemPosition() ?: 0
            var newMsgNum = 0
            msgs.filter { checkMsgState(it) }.forEach {
                newMsgNum++
                when {
                    it.customMsgType == CustomType.delete_message -> {
                        deleteCache.add((it.customInfo?.data as? ChickCustomData)?.msgId.orEmpty())
                        newMsgNum--
                    }
                    isNewMsg -> {
                        //添加私有服务列表
                        msgList.add(it)
                    }
                    else -> {
                        msgList.add(0, it)
                    }
                }

            }
            apiSpiceMgr.requestUserInfo(msgs.mapNotNull { it.sender.toIntOrNull() }.toTypedArray()) {
                if (it.isNotEmpty()) imAdapter.notifyDataSetChanged()
            }
            msgList.removeAll {
                if (deleteCache.remove(it.timMessage.msgId)) {
                    it.timMessage.remove()
                    true
                } else false
            }
            imAdapter.notifyDataSetChanged()
            if (newMsgNum > 0) {
                println("newMsgNum:${newMsgNum}----msgList.size:${msgList.size}---isNewMsg:${isNewMsg}")
                if (msgList.size == newMsgNum) {
                    /** 初次加载 **/
                    im_recycler_view?.post {
                        layoutManager?.scrollToPosition(newMsgNum - 1)
                    }
                } else if (isNewMsg) {
                    /** 添加新消息 **/
                    if (msgList.lastOrNull()?.sender == JohnUser.getSharedUser().userIDAsString || msgList.size - lastVisiblePosition < 3) {
                        layoutManager?.scrollToPosition(msgList.size - 1)
                    } else {
                        unReadMessageNum += newMsgNum
                        unread_message_hint.isVisible = true
                        if (unReadMessageNum < 100) {
                            unread_message_hint.text = "${unReadMessageNum}条新消息"
                        } else {
                            unread_message_hint.text = "99+条新消息"
                        }
                    }
                } else if (msgList.size > newMsgNum && newMsgNum > 0) {
                    /** 加载更多消息  **/
                    layoutManager?.scrollToPosition(newMsgNum + lastVisiblePosition)
                }
            }
            val userList = msgs.mapNotNull { if (it.sender.toIntOrNull().isCmdId()) (it.customInfo?.data as? ChickCustomData)?.userId else it.sender.toIntOrNull() }
            val cacheList = getCacheUserInfos(userList.toTypedArray())
            apiSpiceMgr.requestUserInfo(userList.toTypedArray()) {
                if (it.size > cacheList.size) imAdapter.notifyDataSetChanged()
            }
            apiSpiceMgr.requestUserInfo(msgs.filter { it.customMsgType == CustomType.join_group }.mapNotNull { it.sender.toIntOrNull() }.toTypedArray(), true) {
                if (it.isNotEmpty()) imAdapter.notifyDataSetChanged()
            }
        }
    }

    val xfermode by lazy { PorterDuffXfermode(PorterDuff.Mode.DST_IN) }
    val topShader by lazy { LinearGradient(0f, 0f, 0f, 100f, arrayOf(0, Color.BLACK).toIntArray(), null, Shader.TileMode.CLAMP) }
    val topFadePaint by lazy {
        Paint().apply {
            xfermode = xfermode
        }
    }
    private val topFadeDecoration by lazy {
        object : RecyclerView.ItemDecoration() {
            override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
                super.onDrawOver(c, parent, state)
                if (parent.measuredHeight > dip(150)) {
                    topFadePaint.xfermode = xfermode
                    topFadePaint.shader = topShader
                    c.drawRect(0f, 0f, parent.right.toFloat(), dip(50).toFloat(), topFadePaint)
                    topFadePaint.xfermode = null
                    (parent.tag as? Int)?.let { c.restoreToCount(it) }
                }

            }

            @Suppress("DEPRECATION")
            override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
                super.onDraw(c, parent, state)
                parent.tag = c.saveLayer(0f, 0f, parent.width.toFloat(), parent.height.toFloat(), topFadePaint, Canvas.ALL_SAVE_FLAG)
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        ActivityLauncher.bind(this)
        return inflater.inflate(R.layout.live_im_split_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        im_recycler_view.layoutManager = WrapLayoutManager(requireContext())
        im_recycler_view.adapter = imAdapter
        im_recycler_view.addItemDecoration(topFadeDecoration)

        checkNewEvent.bindEventLifecycle(this)
        chatMsgReceiver.apply {
            imRecyclerView = im_recycler_view
            identify = roomInfo.roomId
        }

        im_recycler_view.postDelayed({
            if (isAdded) {
                (im_recycler_view.layoutManager as? LinearLayoutManager)?.scrollToPosition(msgList.size - 1)
                im_container_fl.animate().alphaBy(1f).duration = 2000
            }
        }, 200)
        checkLiveInitIM()

        initEvent()
        if (roomInfo.roomId.isNotEmpty()) {
            initView()
        }
    }

    private val applyUpstreamFragment by lazy { LiveApplyUpstreamFragment() }
    private fun initView() {
        if (isAdded) {
            childFragmentManager.commit(true) {
                replace(R.id.apply_upstream_container, applyUpstreamFragment.also { it.roomInfo = roomInfo })
            }

            bindAction(arrayOf(UPSTREAM_ACTION, ROOM_TYPE_CHANGE)) { action, obj ->
                when (action) {
                    UPSTREAM_ACTION -> {
                        if (obj != true) {
                            chatMsgReceiver.identify = roomInfo.roomId
                        }
                    }
                    ROOM_TYPE_CHANGE -> {
                        if (checkUserStatus() == 2 && (roomInfo.roomType != roomType || (roomInfo.isSpecialStudio && serviceId != roomInfo.specialServiceSampleDto?.specialServiceId))) {
                            msgList.clear()
                            if (roomInfo.roomType == RoomType.COMMON) chatMsgReceiver.identify = roomInfo.roomId
                        }
                        roomType = roomInfo.roomType
                        serviceId = roomInfo.specialServiceSampleDto?.specialServiceId ?: 0
                        imAdapter.notifyDataSetChanged()
                    }
                }
            }

            logDebug("getApplyCountInitMicState", "initView")
            /**主播端动态设置列表高度*/
            im_container_fl.updateLayoutParams {
                height = if (roomInfo.isCreator) screenHeight - dip(440) else dip(170)
            }
            exclusive_service.isVisible = (roomInfo.recommend != null || roomInfo.recommendCourse != null) && tabBar.review == false

            getServiceRefreshList()
        }
    }

    override fun onUserJoined(uid: Int, elapsed: Int) {
        super.onUserJoined(uid, elapsed)
        activity.upstreamUserIds().add(uid)
    }

    override fun onDestroyView() {
        live_follow_anim_tv.removeCallbacks(leaveRunnable)
        im_recycler_view.removeCallbacks(imWelcomeRunnable)
        super.onDestroyView()
    }

    @SuppressLint("SetTextI18n")
    override fun onNetworkQuality(uid: Int, txQuality: Int, rxQuality: Int) {
        super.onNetworkQuality(uid, txQuality, rxQuality)
        me_network_quality.isVisible = roomInfo.roomRole == 2
        you_network_quality.isVisible = roomInfo.roomRole == 2
        if (isAdded && roomInfo.roomRole == 2) {
            if (uid == roomInfo.createUser?.userId) {
                me_network_quality.text = buildSpannedString {
                    appendLink("达人上传>>>>", Color.WHITE)
                    appendLink(generateText(txQuality), generateColor(txQuality))
                    appendLink("\n达人接收<<<<", Color.WHITE)
                    appendLink(generateText(rxQuality), generateColor(rxQuality))
                }
            } else if (uid == (activity as? VideoLiveRoomActivity)?.liveVideoSplitFragment?.user_video_layout?.userId) {
                you_network_quality.text = buildSpannedString {
                    appendLink(">>>>${generateText(rxQuality)}", generateColor(rxQuality))
                    appendLink(">>>>用户接收", Color.WHITE)
                    appendLink("\n<<<<${generateText(txQuality)}", generateColor(txQuality))
                    appendLink("<<<<用户上传", Color.WHITE)
                }
            }
        }
    }

    private fun generateColor(type: Int): Int {
        return when (type) {
            0 -> Color.GRAY
            1, 2, 3 -> Color.GREEN
            4, 5 -> Color.YELLOW
            else -> Color.RED
        }
    }

    private fun generateText(type: Int): String {
        return when (type) {
            0 -> "0未知"
            1 -> "1极好"
            2 -> "2良好"
            3 -> "3一般"
            4 -> "4较差"
            5 -> "5很差"
            6 -> "6断开"
            8 -> "8探测"
            else -> "未知"
        }
    }

}

