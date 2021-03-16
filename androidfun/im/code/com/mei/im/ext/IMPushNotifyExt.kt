package com.mei.im.ext

import android.app.*
import android.content.Context
import android.content.Context.VIBRATOR_SERVICE
import android.content.Intent
import android.graphics.Color
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import com.joker.im.Message
import com.joker.im.custom.CustomType
import com.joker.im.custom.chick.ChickCustomData
import com.joker.im.listener.IMAllEventManager
import com.joker.im.mapToMeiMessage
import com.joker.im.message.CustomMessage
import com.joker.im.provider.cacheMsg
import com.joker.im.provider.imIsForeground
import com.joker.im.provider.needIgnoreNotify
import com.joker.im.registered
import com.joker.im.utils.toNotifyInt
import com.mei.chat.ui.view.showImNotificationView
import com.mei.im.PUSH_MESSAGE_IDENTIFY
import com.mei.im.PUSH_MESSAGE_IS_GROUP
import com.mei.im.PUSH_TIM_REPORT_DATA
import com.mei.im.ui.IMC2CMessageActivity
import com.mei.init.meiApplication
import com.mei.live.ui.VideoLiveRoomActivity
import com.mei.login.ui.LoginByPhoneActivity
import com.mei.login.ui.WechatFirstLoginActivity
import com.mei.message.MessageFragment
import com.mei.message.MessageNotificationActivity
import com.mei.message.SYSTEM_NOTIFY_USER_ID
import com.mei.orc.ext.runDelayedOnUiThread
import com.mei.orc.john.model.JohnUser
import com.mei.orc.util.json.toJson
import com.mei.orc.util.save.getBooleanMMKV
import com.mei.orc.util.string.decodeUrl
import com.mei.orc.util.string.getInt
import com.mei.splash.ui.SplashActivity
import com.mei.wood.R
import com.mei.wood.cache.getCacheUserInfo
import com.mei.wood.modle.TAB_ITEM
import com.mei.wood.ui.MeiCustomBarActivity
import com.mei.wood.ui.TabMainActivity
import com.mei.wood.util.AppManager
import com.net.MeiUser
import com.net.model.chick.tab.tabbarConfig
import com.tencent.imsdk.TIMConversationType
import com.tencent.imsdk.TIMGroupReceiveMessageOpt
import com.tencent.imsdk.TIMMessage
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import java.lang.ref.WeakReference
import java.util.concurrent.TimeUnit


/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2019-06-20
 */

internal class IMPushNotify : IMAllEventManager() {
    private var isCacheMsg = false

    init {
        registered()
    }

    /**
     * 删除指定id的通知
     */
    fun resetNotifyById(pushId: String) {
        val manager = meiApplication().getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
        manager?.cancel(pushId.toNotifyInt())
    }

    /**
     * 删除所有的消息通知
     */
    fun resetNotifyAll() {
        val manager = meiApplication().getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
        manager?.cancelAll()
    }

    /**
     * 所有的消息回调{这里的消息msgs列表中只有一条消息，当多个消息累积时会不断的回调这个方法来进行通知}
     */
    override fun onNewMessages(msgs: MutableList<TIMMessage>?): Boolean {
        /**对于退出登录后，有新的消息累积，故会多次调用 为什么会取 preActivity 就是因为登录完成im也才会登录，
         * 到了第二个页面才有消息通知，对这些通知进行一个缓存，取最后一条进行横幅通知**/
        if (JohnUser.getSharedUser().hasLogin()) {
            val activity: Activity? = AppManager.getInstance().preActivity()
            if (activity is WechatFirstLoginActivity
                    || activity is LoginByPhoneActivity
                    || (activity == null && !isCacheMsg)
            ) {
                msgs.orEmpty().lastOrNull()?.mapToMeiMessage()?.let { cacheMsg.add(it) }
                cacheMsgPushNotify()
            } else {
                msgs.orEmpty().lastOrNull()?.mapToMeiMessage()?.let { pushNotify(it) }
            }
        }

        return super.onNewMessages(msgs)
    }

    /**
     * 当消息过多累积的情况下进行缓存，只通知最后三条数据
     */
    private fun cacheMsgPushNotify() {
        runDelayedOnUiThread(3000, action = {
            val newMsg = arrayListOf<Message>()
            newMsg.addAll(cacheMsg)
            //产品说获取缓存消息中的最后1条消息进行通知，因为消息太多 ,, Ben说只取1条
            val filterListMsg = newMsg.takeLast(1)
            if (filterListMsg.isNotEmpty()) {
                Observable.intervalRange(0, filterListMsg.size * 1L, 0, 1, TimeUnit.SECONDS)
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnNext {
                            pushNotify(filterListMsg[it.toInt()])
                            isCacheMsg = true
                        }.subscribe()
            }
            cacheMsg.clear()
        })
    }

    private fun pushNotify(msg: Message) {
        if (checkAvailable(msg)) {
            val sender = msg.sender.toIntOrNull() ?: 0
            val userId = if (sender == 99 || sender.isCmdId()) ((msg as? CustomMessage)?.customInfo?.data as? ChickCustomData)?.userId
                    ?: 0 else sender
            var title = getCacheUserInfo(userId)?.nickname ?: msg.timMessage.senderNickname
            if (msg.timMessage.conversation.peer == SYSTEM_NOTIFY_USER_ID) {
                if (msg is CustomMessage) {
                    val data = msg.customInfo?.data
                    if (data is ChickCustomData) {
                        title = data.title
                    }
                }
            }
            if (title.isNullOrEmpty() || title == "未知") title = "知心"
            if (imIsForeground) {
                appNotification(msg)
            } else {
                showNotification(title, msg.getRealSummary().decodeUrl(), msg.timMessage.conversation?.peer.orEmpty(), msg.chatType, msg)
            }
        }
    }

    /**
     * 检查是否需要通知
     */
    @Suppress("RedundantIf")
    private fun checkAvailable(msg: Message): Boolean {
        if (msg.chatType != TIMConversationType.C2C) {
            /** 不是单聊则不通知 **/
            return false
        } else if (msg.sender.getInt().isCmdId()
                && ((msg as? CustomMessage)?.customMsgType != CustomType.invite_up
                        && (msg as? CustomMessage)?.customMsgType != CustomType.apply_exclusive
                        && (msg as? CustomMessage)?.customMsgType != CustomType.quick_apply_exclusive)) {
            return false
        } else if (((msg as? CustomMessage)?.customInfo?.data as? ChickCustomData)?.type == CustomType.exclusive_system_notify) {
            return false
        } else if (msg.isSelf || msg.timMessage.recvFlag == TIMGroupReceiveMessageOpt.ReceiveNotNotify) {
            /** 如果是自己的消息或者消息设置了不需要通知 **/
            return false
        } else if (needIgnoreNotify(msg.sender)) {
            /** 当前发送者的消息是否需要忽略，比如在和他对话 **/
            return false
        } else if ((msg as? CustomMessage)?.isSupportMsg() == false || (msg as? CustomMessage)?.customMsgType == CustomType.call_status_changed) {
            /** 未支持的消息类型 **/
            return false
        } else if (!tabbarConfig.canPushWhenAtForeground) {
            return false
        } else {
            return true
        }
    }


    /**
     * 打开通知框展示最新消息通知
     */
    private fun showNotification(title: String, content: String, peer: String, type: TIMConversationType, msg: Message) {
        val appName = meiApplication().getString(R.string.app_name)
        val manager = meiApplication().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "channel_01"

        @Suppress("DEPRECATION")
        val builder = createBuilder(channelId, appName, manager)

        /** 尝试获取ChickCustomData，如果有的话获取的到，没有的话就是NULL **/
        val chickData = ((msg as? CustomMessage)?.customInfo?.data as? ChickCustomData)

        /** 如果chickData不为空，且是C2C聊天，群聊天的id不是int的值，且是系统账号发的自定义消息 **/
        val chatId = if (chickData != null && type == TIMConversationType.C2C && peer.getInt(0) < 1000) {
            chickData.userId.toString()
        } else {
            peer
        }
        val notificationIntent = Intent(meiApplication().applicationContext, TabMainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            putExtra(PUSH_MESSAGE_IDENTIFY, chatId)
            putExtra(PUSH_MESSAGE_IS_GROUP, type == TIMConversationType.Group)
            /** 这是附加值，有没可能没有 **/
//            if (peer == SYSTEM_NOTIFY_USER_ID) {
            putExtra(PUSH_TIM_REPORT_DATA, chickData.toJson().orEmpty())
//            }
        }
        val intent =
                PendingIntent.getActivity(meiApplication().applicationContext, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        @Suppress("DEPRECATION")
        val notify = builder.setContentTitle(title)
                .setContentText(content)
                .setContentIntent(intent)
                .setTicker("$title:$content")
                .setWhen(System.currentTimeMillis())
                .setDefaults(Notification.DEFAULT_ALL)
                .setSmallIcon(R.mipmap.ic_launcher)
                .build().apply {
                    flags = flags or Notification.FLAG_AUTO_CANCEL
                }
        manager.notify(peer.toNotifyInt(), notify)
    }

    @Suppress("DEPRECATION")
    private fun createBuilder(channelId: String, appName: String, manager: NotificationManager): Notification.Builder {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(channelId, appName, NotificationManager.IMPORTANCE_HIGH)
            notificationChannel.description = appName
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(true)
            manager.createNotificationChannel(notificationChannel)
            Notification.Builder(meiApplication(), channelId)
        } else Notification.Builder(meiApplication())
    }


    /**
     * app内悬挂式消息通知
     */
    private fun appNotification(msg: Message) {

        val activity: Activity? = AppManager.getInstance().currentActivity()
        val userId = MeiUser.getSharedUser().info?.userId
        val isShowBanner = "new_message_banner_sb$userId".getBooleanMMKV(true)
        val isPlayVoice = "new_message_voice_sb$userId".getBooleanMMKV(true)
        val isVibration = "new_message_vibration_sb$userId".getBooleanMMKV(true)
        //连线申请通知不需要app内消息通知
        if (((msg as? CustomMessage)?.customInfo?.data as? ChickCustomData)?.type == CustomType.invite_up
                || ((msg as? CustomMessage)?.customInfo?.data as? ChickCustomData)?.type == CustomType.apply_exclusive
                || ((msg as? CustomMessage)?.customInfo?.data as? ChickCustomData)?.type == CustomType.quick_apply_exclusive) {
            return
        }
        /**直播间中不弹送课程弹框消息*/
        if (activity is VideoLiveRoomActivity
                && ((msg as? CustomMessage)?.customInfo?.data as? ChickCustomData)?.type == CustomType.general_card_popup) {
            return
        }

        //震动（直播连线中，或者 是主播都不需要震动）
        if (isVibration) {
            vibrator(activity)
        }

        //播放提示音规则（直播间连线，或者当前私聊都不会播放提示音）
        if (isPlayVoice) {
            playVoice(activity)
        }

        //拉起横幅判断(私聊列表，当前私聊都不会弹起悬挂)
        if (isShowBanner) {
            showAppNotification(activity, msg)
        }

    }

    private fun showAppNotification(activity: Activity?, msg: Message) {
        //判断是否处于消息列表
        if (activity is TabMainActivity
                && msg.timMessage.conversation?.peer != SYSTEM_NOTIFY_USER_ID
                && activity.getCurrentTabItem() == TAB_ITEM.MESSAGE
                //如果是在私聊tab就不需要弹横幅
                && (activity.fragmentMap[activity.getSelectedIndex()]
                        as? MessageFragment)?.getCurrentIndex() == 0
        ) {

            return
        }
        if (activity is MessageNotificationActivity
                && msg.timMessage.conversation?.peer != SYSTEM_NOTIFY_USER_ID) {
            return
        }
        if (activity is SplashActivity) {
            return
        }
        if (activity is IMC2CMessageActivity) {
            val sender = msg.sender.toIntOrNull() ?: 0
            val userId = if (sender == 99 || sender.isCmdId()) ((msg as? CustomMessage)?.customInfo?.data as? ChickCustomData)?.userId
                    ?: 0 else sender
            if (userId == activity.chatId.getInt(0)) {
                return
            }
        }
        (activity as? MeiCustomBarActivity)?.showImNotificationView(msg)
    }

    /**
     * 震动
     */
    private fun vibrator(activity: Activity?) {
        if (activity is SplashActivity) {
            return
        }
        if (activity is VideoLiveRoomActivity) {
            //不是主播才可以震动
            if ((MeiUser.getSharedUser().info?.isPublisher != true)) {
                startVibrator(activity)
            }
        } else {
            startVibrator(activity)
        }
    }

    private fun playVoice(activity: Activity?) {
        if (activity is SplashActivity) {
            return
        }
        if (activity is VideoLiveRoomActivity) {
            //不在连线中且他不是主播才可以播放声音
            val userId = MeiUser.getSharedUser().info?.userId
            if (!activity.liveVideoSplitFragment.upstreamUserIds.contains(userId
                            ?: 0) && (MeiUser.getSharedUser().info?.isPublisher != true)) {
                playVoiceNotification(activity)
            }
        } else {
            playVoiceNotification(activity)
        }
    }

    /**
     * 播放提示音
     */
    private fun playVoiceNotification(activity: Activity?) {
        val wr = WeakReference<Activity>(activity)
        val notification: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val r = RingtoneManager.getRingtone(wr.get()?.applicationContext, notification)
        r?.play()
    }

    /**
     * 开始震动
     */
    @Suppress("DEPRECATION")
    private fun startVibrator(activity: Activity?) {
        val wr = WeakReference<Activity>(activity)
        val vibrator = wr.get()?.getSystemService(VIBRATOR_SERVICE) as Vibrator?
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator?.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            vibrator?.vibrate(100)
        }
    }
}
