package com.mei.im.ui.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.view.postDelayed
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.commit
import androidx.recyclerview.widget.RecyclerView
import com.joker.im.Conversation
import com.joker.im.bindEventLifecycle
import com.joker.im.mapToConversation
import com.joker.im.provider.ConversationProvider
import com.joker.im.provider.ConversationView
import com.mei.base.common.MESSAGE_DIALOG_SHIELD_USER
import com.mei.im.ext.isCmdId
import com.mei.im.quickLoginIM
import com.mei.im.ui.adapter.IMConversationAdapter
import com.mei.live.ui.VideoLiveRoomActivity
import com.mei.live.views.followFriend
import com.mei.message.SYSTEM_NOTIFY_USER_ID
import com.mei.orc.event.bindAction
import com.mei.orc.ext.getIndexOrNull
import com.mei.orc.ext.screenHeight
import com.mei.orc.ext.screenWidth
import com.mei.orc.john.model.JohnUser
import com.mei.orc.ui.toast.UIToast
import com.mei.orc.util.string.getInt
import com.mei.wood.R
import com.mei.wood.cache.getCacheUserInfo
import com.mei.wood.cache.requestImUserInfo
import com.mei.wood.cache.requestUserInfo
import com.mei.wood.dialog.NormalDialogLauncher
import com.mei.wood.ui.CustomSupportFragment
import com.mei.wood.ui.MeiCustomBarActivity
import com.net.MeiUser
import com.net.model.room.RoomInfo
import com.tencent.imsdk.TIMConversation
import com.tencent.imsdk.TIMConversationType
import com.tencent.imsdk.TIMMessage
import kotlinx.android.synthetic.main.fragment_conversation_dialog_layout.*

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2020/4/22
 */
class IMConversationFragmentDialog : CustomSupportFragment() {

    private val converList = arrayListOf<Any>()
    private val converAdapter by lazy {
        IMConversationAdapter(converList).apply {
            registerAdapterDataObserver(adapterObserver)
            setOnItemClickListener { _, _, position ->
                when (val item = converList.getIndexOrNull(position)) {
                    is Conversation -> {
                        if ((activity as? VideoLiveRoomActivity)?.roomInfo?.createUser?.userId.toString() != item.peer) {
                            gotoChatMessage(item.peer)
                        } else checkMentorFollow { gotoChatMessage(item.peer) }
                    }
                    is RoomInfo -> {
                        checkMentorFollow { gotoChatMessage(item.createUser?.userId.toString()) }
                    }
                }
            }
        }
    }
    private val adapterObserver: RecyclerView.AdapterDataObserver by lazy {
        object : RecyclerView.AdapterDataObserver() {
            override fun onChanged() {
                super.onChanged()
                if (isAdded) {
                    im_empty_layout.isVisible = converList.isEmpty()
                }
            }
        }
    }

    private val converProvider by lazy {
        object : ConversationProvider(object : ConversationView {
            override fun initListView(conversations: List<TIMConversation>) {
                converList.clear()
                val convList = conversations.filter {
                    it.peer.orEmpty().isNotEmpty()
                            && it.type == TIMConversationType.C2C
                            && it.peer != SYSTEM_NOTIFY_USER_ID
                            && !it.peer.toIntOrNull().isCmdId()
                            && getCacheUserInfo(it.peer.getInt(0))?.isBlackList != true
                }.filter { it.lastMsg != null }// 没有产生消息，不显示
                        .map { it.mapToConversation() }
                converList.addAll(convList.sortedByDescending { it.getLastMessageTime() })
                (activity as? VideoLiveRoomActivity)?.roomInfo?.apply {
                    if (!isCreator) {
                        /** 当前导师排第一个 **/
                        val mentorConv = convList.find { (it as? Conversation)?.peer == createUser?.userId.toString() }
                        if (mentorConv != null) {
                            converList.remove(mentorConv)
                            converList.add(0, mentorConv)
                        } else converList.add(0, this)
                    }
                }
                updateMessage(convList.mapNotNull { it.timConversation.lastMsg })
            }

            override fun updateMessage(msgs: List<TIMMessage>) {
                val userIds = msgs.mapNotNull { it.conversation?.peer.orEmpty().toIntOrNull() }
                        .filter { getCacheUserInfo(it) == null || getCacheUserInfo(it)?.avatar.isNullOrEmpty() || getCacheUserInfo(it)?.nickname.isNullOrEmpty() }.toTypedArray()
                if (userIds.isNotEmpty()) apiSpiceMgr.requestImUserInfo(userIds, true) {
                    val blackLists: List<Int>? = userIds.filter { getCacheUserInfo(it)?.isBlackList == true }
                    val blackConversationList: List<Any>? = converList.filter {
                        blackLists?.contains((it as? Conversation)?.peer.getInt(0)) == true
                    }
                    if (blackConversationList?.isNotEmpty() == true) {
                        converList.removeAll(blackConversationList)
                    }
                    converAdapter.notifyDataSetChanged()
                }
                converAdapter.notifyDataSetChanged()
            }
        }) {
            override fun onRefresh() {

            }

            override fun onRefreshConversation(conversations: MutableList<TIMConversation>?) {
                super.onRefreshConversation(conversations)
                val newConvers = conversations.orEmpty().filter {
                    it.peer.orEmpty().isNotEmpty()
                            && it.type == TIMConversationType.C2C
                            && it.peer != SYSTEM_NOTIFY_USER_ID
                            && !it.peer.toIntOrNull().isCmdId()
                            && getCacheUserInfo(it.peer.getInt(0))?.isBlackList != true
                }
                if (newConvers.isNotEmpty()) loadConversation()
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_conversation_dialog_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fragment_conversation_layout.updateLayoutParams {
            width = screenWidth
            height = (screenHeight * 0.7).toInt()
        }
        conversation_recycler.adapter = converAdapter
        converProvider.bindEventLifecycle(this)
        converProvider.loadConversation()
        conversation_recycler.postDelayed(200) { converAdapter.notifyDataSetChanged() }
        bindAction<String>(MESSAGE_DIALOG_SHIELD_USER) {
            refreshImConversationList()
        }
    }

    private fun refreshImConversationList() {
        converList.clear()
        if (JohnUser.getSharedUser().hasLogin()) {
            quickLoginIM(success = { converProvider.loadConversation() })

        }
        if (isAdded) converAdapter.notifyDataSetChanged()
    }

    override fun onDestroy() {
        super.onDestroy()
        converAdapter.unregisterAdapterDataObserver(adapterObserver)
    }

    private fun checkMentorFollow(hasPermission: () -> Unit) {
        val roomInfo = (activity as? VideoLiveRoomActivity)?.roomInfo
        if (roomInfo != null) {
            if (roomInfo.createUser?.isFocus != true) {
                NormalDialogLauncher.newInstance().showDialog(activity, "是否关注并私聊知心达人", okBack = {
                    val mentorId = roomInfo.createUser?.userId ?: 0
                    (activity as? MeiCustomBarActivity)?.followFriend(mentorId, 6, roomInfo.roomId) {
                        if (it) hasPermission()
                    }
                })
            } else hasPermission()
        }

    }

    private fun gotoChatMessage(chatId: String) {
        apiSpiceMgr.requestUserInfo(arrayOf(chatId.getInt())) {
            val info = it.firstOrNull()
            if (info?.isPublisher == MeiUser.getSharedUser().info?.isPublisher) {
                UIToast.toast(activity, if (info?.isPublisher == true) "暂不支持知心达人之间的私聊功能" else "暂不支持用户之间的私聊功能")
            } else {
                parentFragment?.childFragmentManager?.commit(true) {
                    setCustomAnimations(R.anim.fragment_push_bottom_in,
                            R.anim.fade_out,
                            android.R.anim.fade_in,
                            R.anim.fragment_push_bottom_out)
                    replace(R.id.chat_container, IMMessageFragmentDialogLauncher.newInstance(chatId, ""), "IMMessageFragmentDialog")
                    addToBackStack("IMMessageFragmentDialog")
                }
            }
        }

    }
}