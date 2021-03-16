package com.mei.live.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.joker.im.bindEventLifecycle
import com.joker.im.custom.CustomType
import com.joker.im.custom.chick.ChickCustomData
import com.joker.im.custom.chick.RoomEnterAnim
import com.joker.im.listener.IMAllEventManager
import com.joker.im.mapToMeiMessage
import com.joker.im.message.CustomMessage
import com.mei.init.spiceHolder
import com.mei.live.ui.VideoLiveRoomActivity
import com.mei.orc.john.model.JohnUser
import com.mei.orc.util.save.getObjectMMKV
import com.mei.orc.util.save.putMMKV
import com.mei.wood.R
import com.mei.wood.cache.requestUserInfo
import com.mei.wood.extensions.executeKt
import com.mei.wood.ui.CustomSupportFragment
import com.net.model.room.GenericEffectConfig
import com.net.network.room.GenericEffectConfigRequest
import com.tencent.imsdk.TIMMessage
import kotlinx.android.synthetic.main.fragment_room_enter_anim.*
import java.util.*

/**
 * Created by hang on 2020/4/18.
 * 进场动画fragment
 */
/**
 * 请求进场动画配置信息
 */
fun requestEnterRoomConfig(refresh: Boolean = false, back: (GenericEffectConfig) -> Unit = {}) {
    val genericEffectConfig = GenericEffectConfig::class.java.name.getObjectMMKV(GenericEffectConfig::class.java)
    if (refresh
            || genericEffectConfig?.rechargeSuccessAnimation.isNullOrEmpty()
            || genericEffectConfig?.list.isNullOrEmpty()
            || genericEffectConfig?.redPacket.isNullOrEmpty()
            || genericEffectConfig?.shortVideoEffect == null) {
        spiceHolder().apiSpiceMgr.executeKt(GenericEffectConfigRequest(), success = {
            it.data?.let { data ->
                GenericEffectConfig::class.java.name.putMMKV(data)
                back(data)
            }
        })
    } else {
        back(genericEffectConfig)
    }
}

class LiveRoomEnterAnimFragment : CustomSupportFragment() {

    private val roomEnterAnimList = LinkedList<RoomEnterAnim>()

    private val checkNewEvent: IMAllEventManager by lazy {
        object : IMAllEventManager() {
            /** 监听最新消息 **/
            override fun onNewMessages(msgs: MutableList<TIMMessage>?): Boolean {
                val customList = msgs.orEmpty()
                        .mapNotNull { it.mapToMeiMessage() as? CustomMessage }
                        .filter { !it.isDeleted() }//去掉已删除的消息

                (activity as? VideoLiveRoomActivity)?.let { activity ->
                    customList.filter { it.peer == activity.roomId }.forEach { handleIMEvent(it) }
                }
                return super.onNewMessages(msgs)
            }
        }
    }

    /**
     * 处理IM消息
     */
    fun handleIMEvent(message: CustomMessage) {
        val type = message.customMsgType
        (message.customInfo?.data as? ChickCustomData)?.let { data ->
            when (type) {
                CustomType.room_enter_anim -> {
                    /**如果当前用户正在连麦中 忽略掉这条消息*/
                    if (!activity.upstreamUserIds().contains(JohnUser.getSharedUser().userID) || JohnUser.getSharedUser().userID == data.userId) {
                        data.roomEnterAnim?.let { anim ->
                            apiSpiceMgr.requestUserInfo(arrayOf(anim.userId)) {
                                addMsgQueue(anim)
                            }
                        }
                    }
                }
                else -> {

                }
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_room_enter_anim, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkNewEvent.bindEventLifecycle(this)
    }

    private fun addMsgQueue(roomEnterAnim: RoomEnterAnim) {
        roomEnterAnimList.add(roomEnterAnim)
        if (!isVisible) {
            activity?.supportFragmentManager?.beginTransaction()?.show(this)?.commitAllowingStateLoss()
            roomEnterAnimList.poll()?.let { promoteAndExecute(it) }
        }
    }

    private fun promoteAndExecute(roomEnterAnim: RoomEnterAnim) {
        room_enter_anim_view.callback = {
            showNext()
        }
        room_enter_anim_view.refreshData(roomEnterAnim)
    }

    /**
     * 显示下一条动画
     */
    private fun showNext() {
        try {
            val poll = roomEnterAnimList.poll()
            if (poll != null) {
                promoteAndExecute(poll)
            } else {
                activity?.supportFragmentManager?.beginTransaction()?.hide(this)?.commitAllowingStateLoss()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}