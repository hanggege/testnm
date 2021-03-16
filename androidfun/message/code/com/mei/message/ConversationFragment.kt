package com.mei.message

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.core.view.isVisible
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.joker.im.Conversation
import com.joker.im.bindEventLifecycle
import com.joker.im.mapToConversation
import com.joker.im.provider.ConversationProvider
import com.joker.im.provider.ConversationView
import com.joker.im.refreshConversation
import com.mei.base.common.CANCEL_SHIELD_USER
import com.mei.base.common.CHANG_LOGIN
import com.mei.base.common.SHIELD_USER
import com.mei.chat.toImChat
import com.mei.im.ext.isCmdId
import com.mei.im.quickLoginIM
import com.mei.im.ui.view.showIMMenuBubblePopWindow
import com.mei.login.toLogin
import com.mei.message.adapter.ConversationAdapter
import com.mei.message.ext.addHeaderMenuItemView
import com.mei.message.ext.loadingOpenNotification
import com.mei.message.ext.notifyNotificationState
import com.mei.message.ext.refreshHeaderState
import com.mei.message.wiget.MentorMessageHeaderView
import com.mei.message.wiget.MessagePageNotificationView
import com.mei.orc.event.bindAction
import com.mei.orc.ext.dip
import com.mei.orc.ext.getIndexOrNull
import com.mei.orc.john.model.JohnUser
import com.mei.orc.ui.toast.UIToast
import com.mei.orc.util.save.getSetString
import com.mei.orc.util.save.putMMKV
import com.mei.orc.util.string.getInt
import com.mei.wood.R
import com.mei.wood.cache.getCacheUserInfo
import com.mei.wood.cache.requestImUserInfo
import com.net.MeiUser
import com.tencent.imsdk.TIMConversation
import com.tencent.imsdk.TIMConversationType
import com.tencent.imsdk.TIMManager
import com.tencent.imsdk.TIMMessage
import kotlinx.android.synthetic.main.empty_notify_layout.*
import kotlinx.android.synthetic.main.empty_to_login_layout.*
import kotlinx.android.synthetic.main.fragment_converstaion_layout.*

/**
 * author : Song Jian
 * date   : 2020/2/13
 * desc   : 即时消息
 */
private const val STICK_TOP_CONVERSATION = "STICK_TOP_CONVERSATION"

class ConversationFragment : BaseBannerFragment() {

    override val bannerAdapter: BaseQuickAdapter<*, BaseViewHolder> by lazy { conversationAdapter }
    override val dataChange: () -> Unit by lazy {
        {
            to_login_layout.isGone = JohnUser.getSharedUser().hasLogin()
            if (!JohnUser.getSharedUser().hasLogin() && conversationList.isNotEmpty()) {
                /** 最后检查一次 **/
                conversationList.clear()
                conversationAdapter.notifyDataSetChanged()
            }
            im_conversation_empty_layout.isVisible = JohnUser.getSharedUser().hasLogin() && conversationList.isEmpty()
        }
    }

    private val stickTopList by lazy { STICK_TOP_CONVERSATION.getSetString().toMutableSet() }
    var mMentorMessageHeaderView: MentorMessageHeaderView? = null
    var mNotifyLayout: MessagePageNotificationView? = null
    val conversationList = arrayListOf<Conversation>()
    val conversationAdapter by lazy {
        ConversationAdapter(conversationList, stickTopList).apply {
            setOnItemClickListener { _, _, position ->
                conversationList.getIndexOrNull(position)?.let { conversation ->
                    apiSpiceMgr.requestImUserInfo(arrayOf(conversation.peer.getInt()), needRefresh = true) {
                        val info = it.firstOrNull()
                        if (info?.isPublisher == MeiUser.getSharedUser().info?.isPublisher) {
                            UIToast.toast(activity, if (info?.isPublisher == true) "暂不支持知心达人之间的私聊功能" else "暂不支持用户之间的私聊功能")
                        } else activity?.toImChat(conversation.peer, false)
                    }
                }
            }
            setOnItemLongClickListener { _, view, position ->
                conversationList.getIndexOrNull(position)?.let { conversation ->
                    val hasStick = stickTopList.contains(conversation.peer)
                    val optionList = arrayListOf("删除", if (hasStick) "取消置顶" else "置顶")
                    view.showIMMenuBubblePopWindow(optionList, -dip(12)) {
                        when {
                            it == 0 -> {
                                conversation.readAllMessage()
                                TIMManager.getInstance().deleteConversationAndLocalMsgs(conversation.type, conversation.peer)
                                refreshConversation(arrayListOf(conversation.timConversation))
                            }
                            it == 1 && hasStick -> {
                                stickTopList.remove(conversation.peer)
                                refreshConversation(arrayListOf(conversation.timConversation))
                                STICK_TOP_CONVERSATION.putMMKV(stickTopList)
                            }
                            it == 1 && !hasStick -> {
                                stickTopList.add(conversation.peer)
                                refreshConversation(arrayListOf(conversation.timConversation))
                                STICK_TOP_CONVERSATION.putMMKV(stickTopList)
                            }
                            else -> {
                            }
                        }
                    }
                }
                true
            }
        }
    }

    private val conversationProvider by lazy {
        object : ConversationProvider(object : ConversationView {
            override fun initListView(conversations: List<TIMConversation>) {
                conversationList.clear()
                val conversationList = conversations.filter {
                    it.peer.orEmpty().isNotEmpty()
                            && it.type == TIMConversationType.C2C
                            && it.peer != SYSTEM_NOTIFY_USER_ID
                            && !it.peer.toIntOrNull().isCmdId()
                            /**这里添加了黑名单的过滤*/
                            && getCacheUserInfo(it.peer.getInt(0))?.isBlackList != true

                }.filter { it.lastMsg != null }// 没有产生消息,黑名单，不显示
                        .map { it.mapToConversation() }
                this@ConversationFragment.conversationList.addAll(conversationList)
                updateMessage(conversationList.mapNotNull { it.timConversation.lastMsg })
            }

            override fun updateMessage(msgs: List<TIMMessage>) {
                conversationList.sortedByDescending { it.getLastMessageTime() }
                val stickConversations = stickTopList.reversed().mapNotNull { peer -> conversationList.find { it.peer == peer } }
                conversationList.removeAll(stickConversations)
                conversationList.addAll(0, stickConversations)
                conversationList.distinctBy { it.peer }

                val userIds = msgs.mapNotNull { it.conversation?.peer.orEmpty().toIntOrNull() }
                        .filter { getCacheUserInfo(it) == null || getCacheUserInfo(it)?.avatar.isNullOrEmpty() || getCacheUserInfo(it)?.nickname.isNullOrEmpty() }.toTypedArray()
                if (userIds.isNotEmpty()) apiSpiceMgr.requestImUserInfo(userIds, needRefresh = true) {
                    val blackLists: List<Int>? = userIds.filter { getCacheUserInfo(it)?.isBlackList == true }
                    val blackConversationList: List<Conversation>? = conversationList.filter { blackLists?.contains(it.peer.getInt(0)) == true }
                    /**移除黑名单会话，不显示*/
                    if (blackConversationList?.isNotEmpty() == true) {
                        conversationList.removeAll(blackConversationList)
                    }
                    conversationAdapter.notifyDataSetChanged()
                }

                conversationAdapter.notifyDataSetChanged()
                if (isAdded) im_conversation_empty_layout?.isVisible = conversationList.isEmpty()
            }
        }) {
            override fun onRefresh() {
            }

            override fun onRefreshConversation(conversations: MutableList<TIMConversation>?) {
                super.onRefreshConversation(conversations)
                val newConversations = conversations.orEmpty().filter {
                    it.peer.orEmpty().isNotEmpty()
                            && it.type == TIMConversationType.C2C
                            && it.peer != SYSTEM_NOTIFY_USER_ID
                            && !it.peer.toIntOrNull().isCmdId()
                            /**这里添加了黑名单的过滤*/
                            && getCacheUserInfo(it.peer.getInt(0))?.isBlackList != true
                }
                if (newConversations.isNotEmpty()) loadConversation()
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_converstaion_layout, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        im_conversation_recycler.adapter = conversationAdapter
        empty_hint_tv.text = "暂无私聊消息"
        addHeaderMenuItemView()
        loadingOpenNotification()
        buildHeader()
        refreshIMList()
        goto_login_view.setOnClickListener { activity?.toLogin() }
        conversationProvider.bindEventLifecycle(this)
        bindAction<Boolean>(CHANG_LOGIN) {
            refreshIMList()
            refreshHeaderState(it)
        }
        bindAction<List<Int>>(CANCEL_SHIELD_USER) {
            refreshConversationList(it)
        }
        bindAction<List<Int>>(SHIELD_USER) {
            refreshConversationList(it)
        }
    }

    /**
     * 有拉黑，取消拉黑操作，刷新IM会话列表
     */
    private fun refreshConversationList(userId: List<Int>?) {
        val userIdList = userId?.toTypedArray()
        if (userIdList?.isNotEmpty() == true) {
            apiSpiceMgr.requestImUserInfo(userIds = userIdList, needRefresh = true, back = {
                if (it.isNotEmpty()) {
                    refreshIMList()
                }
            })
        }
    }

    override fun willAppear() {
        super.willAppear()
        if (JohnUser.getSharedUser().hasLogin()) {
            apiSpiceMgr.requestImUserInfo(conversationList.mapNotNull { it.peer.toIntOrNull() }.toTypedArray(), needRefresh = true) {
                if (isAdded) conversationAdapter.notifyDataSetChanged()
            }
        } else {
            conversationList.clear()
            if (isAdded) conversationAdapter.notifyDataSetChanged()
        }
    }

    override fun refreshIMList() {
        conversationList.clear()
        if (JohnUser.getSharedUser().hasLogin()) {
            quickLoginIM(success = { conversationProvider.loadConversation() })

        }
        if (isAdded) conversationAdapter.notifyDataSetChanged()
    }

    override fun onResume() {
        super.onResume()
        if (isAdded) {
            notifyNotificationState()
        }
    }


}