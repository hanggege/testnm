package com.mei.live.ui.fragment

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.Rect
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isGone
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.commit
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.flyco.roundview.RoundTextView
import com.joker.im.bindEventLifecycle
import com.joker.im.custom.CustomType
import com.joker.im.custom.chick.ChickCustomData
import com.joker.im.listener.IMAllEventManager
import com.joker.im.mapToMeiMessage
import com.joker.im.message.CustomMessage
import com.mei.agora.event.AgoraEventHandler
import com.mei.base.common.CREATOR_UPSTREAM
import com.mei.base.common.FOLLOW_USER_STATE
import com.mei.base.common.ROOM_TYPE_CHANGE
import com.mei.base.common.UPSTREAM_ACTION
import com.mei.base.ui.nav.Nav
import com.mei.im.ui.dialog.showChatDialog
import com.mei.live.manager.genderAvatar
import com.mei.live.ui.VideoLiveRoomActivity
import com.mei.live.ui.dialog.*
import com.mei.live.views.followFriend
import com.mei.live.views.room.LiveRankFragment
import com.mei.live.views.room.LiveReceiveRedPacketFragment
import com.mei.orc.Cxt
import com.mei.orc.event.bindAction
import com.mei.orc.ext.dip
import com.mei.orc.ext.screenWidth
import com.mei.orc.ext.subListByIndex
import com.mei.orc.imageload.glide.glideDisplay
import com.mei.orc.john.model.JohnUser.getSharedUser
import com.mei.orc.util.click.setOnClickNotDoubleListener
import com.mei.orc.util.date.formatTimeVideo
import com.mei.wood.R
import com.mei.wood.cache.getCacheUserInfo
import com.mei.wood.cache.requestUserInfo
import com.mei.wood.ext.AmanLink
import com.mei.wood.extensions.executeKt
import com.mei.wood.ui.CustomSupportFragment
import com.mei.wood.util.MeiUtil
import com.mei.wood.util.logDebug
import com.net.MeiUser
import com.net.model.room.RoomInfo
import com.net.model.room.RoomInfo.CreateUser
import com.net.model.room.RoomType
import com.net.model.room.RoomUserTypeEnum
import com.net.network.chick.friends.FollowExistRequest
import com.net.network.room.RoomPopularityRequest
import com.net.network.room.RoomUserRecentRequest
import com.tencent.imsdk.TIMMessage
import kotlinx.android.synthetic.main.fragment_live_top_container.*
import kotlinx.android.synthetic.main.layout_common_top_container.*
import kotlinx.android.synthetic.main.layout_special_studio_top_container.*
import kotlinx.android.synthetic.main.layout_studio_top_container.*
import kotlinx.android.synthetic.main.view_exclusive_audience.view.*
import java.util.*

/**
 * Created by hang on 2020-02-18.
 * 直播间顶部模块
 */
@Suppress("UNCHECKED_CAST")
class LiveTopContainerFragment : CustomSupportFragment(), AgoraEventHandler {

    var roomInfo: RoomInfo = RoomInfo()
        set(value) {
            field = value
            initView()
        }

    private var currentTime = System.currentTimeMillis()
    private val pushMsgList = LinkedList<String>()


    /**检测是否有邀请连线弹框*/
    private var detectionInviteDialogRunnable: Runnable? = null
    private var givingCourseMsg: ChickCustomData? = null
    val postHandler: Handler by lazy { Handler() }

    private val onLineUserInfoList = arrayListOf<CreateUser>()
    private val onLineUserAdapter by lazy {
        OnlineUserAdapter(onLineUserInfoList).also {
            online_user_recycler.adapter = it
            it.setOnItemClickListener { _, _, position ->
                (activity as? VideoLiveRoomActivity)?.run {
                    onLineUserInfoList.getOrNull(position)?.let {
                        if (it.userId < 0) {
                            upstreamListDialog?.dismissAllowingStateLoss()
                            showLiveListDialog(roomInfo.roomId, RoomUserTypeEnum.ROOM_OLD_USER, { dialog -> upstreamListDialog = dialog }, { upstreamListDialog = null })
                        } else {
                            showUserInfoDialog(roomInfo, it.userId) { type, info ->
                                if (type == 0) {
                                    apiSpiceMgr.requestUserInfo(arrayOf(info.userId), needRefresh = true) { list ->
                                        list.firstOrNull()?.let { userInfo ->
                                            showInputKey(userInfo)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
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

    private var hasStartMarquee = false
    private fun handleIMEvent(msg: CustomMessage) {
        val type = msg.customMsgType
        logDebug("liveTop_handleIMEvent: ${type.name}")
        (msg.customInfo?.data as? ChickCustomData)?.let { data ->
            when (type) {
                CustomType.room_broadcast -> {
                    if (data.roomId == roomInfo.roomId && data.content.isNotEmpty() && data.content[0].text.isNotEmpty()) {
                        pushMsgList.offer(data.content[0].text)
                        if (hasStartMarquee) {
                            startMarqueeAnim()
                        }
                    }
                }
                CustomType.room_info_change -> {
                    if (roomInfo.roomId == data.roomId) {
                        roomInfo.title = data.roomInfoChange?.title.orEmpty()
                        roomInfo.tag = data.roomInfoChange?.tag.orEmpty()
                        roomInfo.broadcastId = data.roomInfoChange?.broadcastId.orEmpty()
                        updateView()
                    }
                }
                CustomType.send_gift -> {
                    if (roomInfo.roomId == data.roomId) {
                        if (data.userId == (getSharedUser().userID) && data.gift?.giftId ?: 0 == RECHARGE_SERVICE_SUCCESS_ID) {
                            /**刷新与知心达人是否存在关注关系*/
                            followExist(roomInfo.createUser?.userId ?: 0)
                        }
                        requestRoomPopularity()
                    }
                }
                CustomType.send_text -> {
                    if (data.roomId == roomInfo.roomId) {
                        requestRoomPopularity()
                    }
                }
                CustomType.people_changed -> {
                    if (data.reason == 2 || data.reason == 4 || data.reason == 5) {
                        requestRoomPopularity()
                    }

                    if (roomInfo.isCreator && (data.reason == 1 || data.reason == 2 || data.reason == 3 || data.reason == 4)) {
                        requestRoomUserRecent()
                    }
                }
                CustomType.room_reject_up -> {
                    if (roomInfo.isCreator) {
                        requestRoomUserRecent()
                    }
                }
                /**关注送课程消息 c2c消息*/
                CustomType.general_card_popup -> {
                    givingCourseMsg = data
                    startGivingCourseDialog()
                }

                else -> {
                }
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_live_top_container, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initEvent()

        if (roomInfo.roomId.isNotEmpty()) {
            initView()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun initView() {
        if (isAdded) {
            exclusive_layout.roomInfo = roomInfo
            exclusive_layout.refreshInfo(hashSetOf(roomInfo.publisherId, roomInfo.guestUser?.userId
                    ?: View.NO_ID))
            if (roomInfo.isCreator) {
                requestRoomUserRecent()
            }
            initAction()

            skill_loop_view.initData(roomInfo.createUser?.skills.orEmpty())

            notifyOnlineNumberVisible(roomInfo.isCreator && roomInfo.needUpstream)

            common_top_container.isVisible = false
            studio_top_container.isVisible = false
            special_studio_top_container.isVisible = false
            when {
                roomInfo.isStudio -> {
                    initStudioView()
                }
                roomInfo.isSpecialStudio -> {
                    initSpecialStudioView()
                }
                else -> {
                    initCommonView()
                }
            }
            updateView()

//            apply_upstream_view.mRoomInfo = roomInfo

            hasStartMarquee = false
            /**暂时隐藏广播*/
//            view?.postDelayed(startMarqueeRunnable, 10000)
            marquee_layout.isInvisible = true
        }
    }

    private val redPacketFragment by lazy {
        LiveReceiveRedPacketFragment().apply {
            roomId = roomInfo.roomId
        }
    }

    private val rankFragment by lazy {
        LiveRankFragment().also {
            it.roomInfo = roomInfo
        }
    }

    private fun initAction() {
        childFragmentManager.commit(true) {
            replace(R.id.rank_container, rankFragment)
            /**不是导师才显示红包*/
            if (MeiUser.getSharedUser().info?.isPublisher != true) {
                replace(R.id.packet_container, redPacketFragment)
                hide(redPacketFragment)
            }
        }


        bindAction(arrayOf(FOLLOW_USER_STATE, UPSTREAM_ACTION, CREATOR_UPSTREAM)) { action, obj ->
            if (isAdded) {
                when (action) {
                    FOLLOW_USER_STATE -> {
                        (obj as? Pair<Int, Boolean>)?.let {
                            if (it.first == roomInfo.createUser?.userId) {
                                roomInfo.createUser?.isFocus = it.second
                                refreshFollowBtnState(follow_anchor)
                                refreshFollowBtnState(special_studio_follow_anchor)
                                refreshFollowBtnState(follow_presenter)
                                if (group_presenter.isGone) {
                                    follow_presenter.isGone = true
                                }
                            }
                        }
                    }
                    CREATOR_UPSTREAM -> {
                        notifyOnlineNumberVisible(true)
                        followExist(roomInfo.createUser?.userId ?: 0)
                    }
                }
            }
        }
        bindAction<Any>(ROOM_TYPE_CHANGE) {
            refreshConsultHint()
        }
    }

    private fun refreshConsultHint() {
        consult_hint.isVisible = roomInfo.roomType == RoomType.COMMON || activity?.isUpstream() == true || roomInfo.roomRole == 2
        consult_hint.text = when {
            roomInfo.roomType == RoomType.COMMON -> "直播咨询是什么"
            roomInfo.isSpecialService -> "专属服务是什么"
            else -> "如何私密连线"
        }
    }

    /**
     * 刷新关注按钮
     */
    private fun refreshFollowBtnState(textView: RoundTextView) {
        if (roomInfo.isCreator) {
            textView.isVisible = false
        } else {
            textView.isVisible = true
            if (roomInfo.createUser?.isFocus == true) {
                textView.delegate.backgroundColor = Cxt.getColor(R.color.color_FF3A3A)
                textView.text = "私聊"
                textView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
                textView.setTextColor(Color.WHITE)
                textView.setPadding(dip(14), 0, dip(14), 0)
            } else {
                textView.delegate.backgroundColor = Color.WHITE
                textView.text = "关注"
                textView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_follow_add_red, 0, 0, 0)
                textView.setTextColor(Cxt.getColor(R.color.color_FF3A3A))
                textView.setPadding(dip(8), 0, dip(8), 0)
            }
        }
    }

    /**
     * 工作室专属房顶部ui
     */
    @SuppressLint("SetTextI18n")
    private fun initSpecialStudioView() {
        special_studio_top_container.isVisible = true

        special_studio_name.text = roomInfo.groupInfo?.groupName.orEmpty()
        special_studio_avatar.glideDisplay(roomInfo.createUser?.avatar.orEmpty(), roomInfo.createUser?.gender.genderAvatar())
        special_studio_anchor_name.text = roomInfo.createUser?.nickName.orEmpty()
        special_studio_online_number.text = "${roomInfo.onlineCount}人气"
        special_studio_online_number_group.isGone = roomInfo.onlineCount <= 0
        special_studio_room_id.text = "房间ID：${roomInfo.roomId}"

        refreshFollowBtnState(special_studio_follow_anchor)

        marquee_layout.updateLayoutParams<ConstraintLayout.LayoutParams> {
            topMargin = dip(60)
        }
        consult_hint.updateLayoutParams<ConstraintLayout.LayoutParams> {
            topToBottom = R.id.online_user_recycler
            topMargin = dip(5)
            goneTopMargin = 0
        }
    }

    /**
     * 工作室直播间顶部ui
     */
    @SuppressLint("SetTextI18n")
    private fun initStudioView() {
        studio_top_container.isVisible = true

        studio_cover.glideDisplay(roomInfo.groupInfo?.avatar.orEmpty(), R.drawable.default_studio_cover)
        studio_name.text = roomInfo.groupInfo?.groupName.orEmpty()
        studio_name.paint.isFakeBoldText = true
        studio_introduce.paint.isFakeBoldText = true

        studio_people_count.text = "团队成员：${roomInfo.groupInfo?.memberCount ?: 0}人"
        studio_online_number.text = "${roomInfo.onlineCount}人气"
        studio_online_number_group.isGone = roomInfo.onlineCount <= 0

        presenter_avatar.glideDisplay(roomInfo.createUser?.avatar.orEmpty(), roomInfo.createUser?.gender.genderAvatar())
        presenter_name.text = roomInfo.createUser?.nickName.orEmpty()
        if (group_presenter.isGone) {
            follow_presenter.isGone = true
        } else {
            refreshFollowBtnState(follow_presenter)
        }

        marquee_layout.updateLayoutParams<ConstraintLayout.LayoutParams> {
            topMargin = dip(85)
        }

        consult_hint.updateLayoutParams<ConstraintLayout.LayoutParams> {
            topToBottom = R.id.back_choice
            topMargin = dip(15)
            goneTopMargin = 0
        }
    }

    /**
     * 普通直播间顶部ui
     */
    @SuppressLint("SetTextI18n")
    private fun initCommonView() {
        common_top_container.isVisible = true

        anchor_avatar.glideDisplay(roomInfo.createUser?.avatar.orEmpty(), roomInfo.createUser?.gender.genderAvatar())
        anchor_name.text = roomInfo.createUser?.nickName.orEmpty()
        anchor_name.paint.isFakeBoldText = true

        online_number.text = "${roomInfo.onlineCount}人气"
        (roomInfo.onlineCount <= 0).apply {
            online_number.isGone = this
            online_number_point.isGone = this
        }
        refreshFollowBtnState(follow_anchor)

        marquee_layout.updateLayoutParams<ConstraintLayout.LayoutParams> {
            topMargin = dip(60)
        }

        consult_hint.updateLayoutParams<ConstraintLayout.LayoutParams> {
            topToBottom = R.id.online_user_recycler
            topMargin = dip(5)
            goneTopMargin = 0
        }
    }

    /**
     * 刷新连麦时长
     */
    fun refreshUpstreamTime(timeAccount: Long) {
        if (isAdded) {
            exclusive_layout.mTimeAccount = timeAccount
        }
    }

    var onLineTime = 0L

    /**
     * 刷新开播时长
     */
    fun refreshStartLiveTime(timeAccount: Long) {
        if (isAdded) {
            live_mic_time_tv.isVisible = timeAccount > 0 && roomInfo.isCreator
            studio_mic_time_tv.isVisible = timeAccount > 0 && roomInfo.isCreator
            special_studio_mic_time_tv.isVisible = timeAccount > 0 && roomInfo.isCreator
            if (timeAccount > 0) {
                live_mic_time_tv.text = timeAccount.formatTimeVideo()
                studio_mic_time_tv.text = timeAccount.formatTimeVideo()
                special_studio_mic_time_tv.text = timeAccount.formatTimeVideo()
            }
        }
    }

    fun refreshSpecialStudioState(groupRoomIsOpened: Boolean){
        exclusive_layout.studio_live_layout.isVisible = groupRoomIsOpened
    }

    @SuppressLint("SetTextI18n")
    private fun updateView() {
        if (isAdded) {
            presenter_avatar.glideDisplay(roomInfo.createUser?.avatar.orEmpty(), roomInfo.createUser?.gender.genderAvatar())
            presenter_name.text = roomInfo.createUser?.nickName.orEmpty()
        }
    }

    private fun followFriend() {
        if (roomInfo.createUser?.isFocus == true) {
            activity?.showChatDialog(roomInfo.createUser?.userId.toString(), "")
        } else {
            (activity as? VideoLiveRoomActivity)?.followFriend(roomInfo.createUser?.userId
                    ?: 0, 2, roomInfo.roomId)
        }
    }

    private fun initEvent() {
        checkNewEvent.bindEventLifecycle(this)

        follow_anchor.setOnClickNotDoubleListener {
            followFriend()
        }
        follow_presenter.setOnClickNotDoubleListener {
            followFriend()
        }
        special_studio_follow_anchor.setOnClickNotDoubleListener {
            followFriend()
        }

        back_choice.setOnClickNotDoubleListener {
            if (roomInfo.createUser?.isFocus == false && onLineTime > roomInfo.minWatchTimeOfShowFollowDialogSec) {
                activity?.showQuitRoomFollowDialog(activity.isUpstream(), roomInfo.roomId, roomInfo.createUser?.userId
                        ?: 0) {
                    if (it == 0) {
                        activity?.showEvaluationDialog(roomInfo.publisherId, roomInfo.roomId)
                        (activity as? VideoLiveRoomActivity)?.liveVideoSplitFragment?.stopStream()
                    } else {
                        activity?.finish()
                    }
                }
            } else {
                (activity as? VideoLiveRoomActivity)?.liveVideoSplitFragment?.onBackPressed(false)
            }
        }

        anchor_avatar.setOnClickListener {
            activity?.showUserInfoDialog(roomInfo, roomInfo.createUser?.userId ?: 0) { type, info ->
                if (type == 0) {
                    getCacheUserInfo(info.userId)?.let {
                        (activity as? VideoLiveRoomActivity)?.showInputKey(it)
                    }
                }
            }
        }

        pushMsgList.offer(Cxt.getStr(R.string.platform_safe_hint_one))
        pushMsgList.offer(Cxt.getStr(R.string.platform_safe_hint_two))


        studio_live_layout.setOnClickNotDoubleListener {
            Nav.toPersonalPage(activity, roomInfo.createUser?.userId ?: 0)
        }
        presenter_avatar.setOnClickNotDoubleListener {
            anchor_avatar.performClick()
        }
        special_studio_avatar.setOnClickNotDoubleListener {
            anchor_avatar.performClick()
        }
        consult_hint.setOnClickNotDoubleListener {
            val index = if (roomInfo.roomType == RoomType.COMMON) 0 else 4
            val url = MeiUtil.appendParamsUrl(AmanLink.NetUrl.instructions,
                    "group_id" to (MeiUser.getSharedUser().info?.groupInfo?.groupId
                            ?: 0).toString(),
                    "index" to index.toString(),
                    "from" to "im")
            activity?.showExclusiveServiceDialog(url, roomInfo.roomId)
        }
    }

    /**
     * 不在线时不显示label和在线人数
     */
    private fun notifyOnlineNumberVisible(isCreatorOnline: Boolean) {
        online_number_layout.isVisible = isCreatorOnline || roomInfo.onlineCount > 0

        group_presenter.isVisible = isCreatorOnline
        if (isCreatorOnline) {
            refreshFollowBtnState(follow_presenter)
        }
        updateView()
    }

    private val startMarqueeRunnable = Runnable {
        startMarqueeAnim()
    }

    private var isAnim = false
    private val rect = Rect()

    private fun startMarqueeAnim() {
        if (!isAnim && isAdded) {
            hasStartMarquee = true
            pushMsgList.poll()?.let {
                val delay = when {
                    it == Cxt.getStr(R.string.platform_safe_hint_one) -> 6500
                    System.currentTimeMillis() - currentTime < 10000 -> 10000 - (System.currentTimeMillis() - currentTime)
                    else -> 0
                }
                live_tips_layout.translationX = screenWidth.toFloat()
                system_tips_tv.paint.getTextBounds(it, 0, it.length - 1, rect)
                live_tips_layout.animate().translationX(-(rect.width() + 100).toFloat()).setDuration(150L * (it.length)).setInterpolator(LinearInterpolator()).withLayer().setStartDelay(delay).withStartAction {
                    if (isAdded) {
                        isAnim = true
                        system_tips_tv.text = it
                        live_tips_layout.isVisible = true
                    }
                }.withEndAction {
                    if (isAdded) {
                        isAnim = false
                        currentTime = System.currentTimeMillis()
                        live_tips_layout.isVisible = false
                        if (pushMsgList.isNotEmpty()) {
                            startMarqueeAnim()
                        }
                    }
                }.start()
            }
        }
    }

    /**
     * 请求人气数据
     */
    @SuppressLint("SetTextI18n")
    private fun requestRoomPopularity() {
        apiSpiceMgr.executeKt(RoomPopularityRequest(roomInfo.roomId), success = {
            it?.data?.let { data ->
                roomInfo.onlineCount = data.onlineCount
                online_number.text = "${data.onlineCount}人气"
                (roomInfo.onlineCount > 0).apply {
                    online_number.isVisible = this
                    online_number_point.isVisible = this
                }

                studio_online_number.text = "${roomInfo.onlineCount}人气"
                studio_online_number_group.isVisible = roomInfo.onlineCount > 0

                special_studio_online_number.text = "${roomInfo.onlineCount}人气"
                special_studio_online_number_group.isVisible = roomInfo.onlineCount > 0
            }
        })
    }

    /**
     * 请求房间内最新在线用户信息
     */
    private fun requestRoomUserRecent() {
        apiSpiceMgr.executeKt(RoomUserRecentRequest(roomInfo.roomId, 4, "join"), success = {
            it.data?.let { data ->
                onLineUserInfoList.clear()
                onLineUserInfoList.addAll(data.users.subListByIndex(4))
                if (data.total > 4) {
                    onLineUserInfoList.add(CreateUser().apply {
                        userId = -1
                        gender = data.total  //用性别字段存储总数 简单处理
                    })
                }
                onLineUserAdapter.notifyDataSetChanged()
                online_user_recycler.isVisible = onLineUserInfoList.size > 0
            }
        })
    }

    private fun isInviteDialog(): Boolean {
        return activity?.supportFragmentManager?.findFragmentByTag("invite_dialog") != null
    }

    private fun startGivingCourseDialog() {
        if (isInviteDialog()) {
            detectionInviteDialogRunnable = null
            val run = Runnable {
                startGivingCourseDialog()
                detectionInviteDialogRunnable = null
            }
            detectionInviteDialogRunnable = run
            postHandler.postDelayed(run, 3000)

        } else {
            detectionInviteDialogRunnable?.let { postHandler.removeCallbacks(it) }
            activity?.showGivingCourseDialog(givingCourseMsg)
            givingCourseMsg = null
        }
    }

    /**
     * 判断是否是关注关系
     */
    private fun followExist(followId: Int) {
        apiSpiceMgr.executeKt(FollowExistRequest(followId), success = {
            roomInfo.createUser?.isFocus = it.data.isFollow
            follow_anchor.isGone = it.data.isFollow
            refreshFollowBtnState(follow_anchor)
            refreshFollowBtnState(special_studio_follow_anchor)
            if (group_presenter.isVisible) {
                refreshFollowBtnState(follow_presenter)
            }
        })
    }

    inner class OnlineUserAdapter(list: MutableList<CreateUser>) : BaseQuickAdapter<CreateUser, BaseViewHolder>(R.layout.item_online_user_avatar, list) {

        override fun convert(holder: BaseViewHolder, item: CreateUser) {
            holder.setGone(R.id.avatar_img, item.userId < 0)
                    .setGone(R.id.people_count, item.userId >= 0)
                    .setText(R.id.people_count, item.gender.toString())
            holder.getView<ImageView>(R.id.avatar_img).glideDisplay(item.avatar, item.gender.genderAvatar(item.isPublisher))
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        live_tips_layout?.animate()?.cancel()
        view?.removeCallbacks(startMarqueeRunnable)
        detectionInviteDialogRunnable?.let { postHandler.removeCallbacks(it) }
    }

    override fun onUserJoined(uid: Int, elapsed: Int) {
        if (isAdded) {
            if (uid == roomInfo.createUser?.userId) {
                notifyOnlineNumberVisible(true)
            }
        }
    }

    override fun onUserOffline(uid: Int, reason: Int) {
        if (isAdded) {
            if (uid == roomInfo.createUser?.userId) {
                notifyOnlineNumberVisible(false)
            }
        }
    }

}


