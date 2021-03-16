package com.mei.chat.ui.view

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.view.MotionEvent
import android.view.ViewConfiguration
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.widget.FrameLayout
import androidx.core.view.contains
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.joker.im.Message
import com.joker.im.custom.chick.ChickCustomData
import com.joker.im.message.CustomMessage
import com.mei.base.ui.nav.Nav
import com.mei.base.util.shadow.setListShadowDefault
import com.mei.chat.toImChat
import com.mei.im.ui.dialog.showChatDialog
import com.mei.live.manager.genderAvatar
import com.mei.live.ui.VideoLiveRoomActivity
import com.mei.message.SYSTEM_NOTIFY_USER_ID
import com.mei.orc.ext.dip
import com.mei.orc.ext.getStatusBarHeight
import com.mei.orc.ext.layoutInflaterKtToParentAttach
import com.mei.orc.imageload.glide.glideDisplay
import com.mei.orc.ui.toast.UIToast
import com.mei.orc.util.handler.GlobalUIHandler
import com.mei.orc.util.string.decodeUrl
import com.mei.orc.util.string.getInt
import com.mei.wood.R
import com.mei.wood.cache.getCacheUserInfo
import com.mei.wood.cache.requestImUserInfo
import com.mei.wood.dialog.DialogData
import com.mei.wood.dialog.NormalDialogLauncher
import com.mei.wood.ext.AmanLink
import com.mei.wood.extensions.executeKt
import com.mei.wood.extensions.executeToastKt
import com.mei.wood.ui.MeiCustomBarActivity
import com.mei.wood.util.parseValue
import com.net.MeiUser
import com.net.network.chick.friends.HomeStatusRequest
import com.net.network.report.MessagePushReport
import com.tencent.imsdk.TIMConversationType
import kotlinx.android.synthetic.main.view_app_im_notification.view.*
import java.lang.ref.WeakReference
import kotlin.math.abs


/**
 * App 内悬挂式消息通知
 * @author Created by lenna on 2020/6/10
 */
class AppIMNotificationView(context: Context) : FrameLayout(context) {
    private var mRunTask: Runnable? = null
    private var isRemove: Boolean = false
    private var mMsg: Message? = null
    private var valueAnimator: ValueAnimator? = null
    var activity: MeiCustomBarActivity? = null

    init {
        if (context is MeiCustomBarActivity) {
            activity = context
        }
        val viewParent = activity?.window?.decorView
        (viewParent as? ViewGroup)?.removeView(this)
        layoutInflaterKtToParentAttach(R.layout.view_app_im_notification)
        layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        this.id = R.id.app_im_notification_view
        (viewParent as? ViewGroup)?.addView(this)
        activity?.lifecycle?.addObserver(object : LifecycleEventObserver {
            override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                if (event == Lifecycle.Event.ON_DESTROY) {
                    appIMNotificationView = null
                }
            }
        })
        isVisible = false
        setListShadowDefault(new_message_rrl)
        setOnClickListener {
            val userId = MeiUser.getSharedUser().info?.userId
            val chickData = ((mMsg as? CustomMessage)?.customInfo?.data as? ChickCustomData)
            val chatId = if (chickData != null && mMsg?.chatType == TIMConversationType.C2C && mMsg?.peer.getInt(0) < 1000) {
                chickData.userId.toString()
            } else {
                mMsg?.peer
            }
            //对正在连线中的用户进行拦截
            if ((activity as? VideoLiveRoomActivity)?.liveVideoSplitFragment
                            ?.upstreamUserIds?.contains(userId ?: 0) == true
                    && mMsg?.timMessage?.conversation?.peer == SYSTEM_NOTIFY_USER_ID) {
                NormalDialogLauncher.newInstance().showDialog(activity, DialogData(message = "您当前正在连线中，请结束后查看此类型消息", cancelStr = null), okBack = { })
                removeAnimation()
                return@setOnClickListener
            }


            if (chatId?.isNotEmpty() == true) {
                if (chickData != null) {
                    val action = chickData.action
                    if (chickData.report.isNotEmpty()) {
                        activity?.apiSpiceMgr?.executeKt(MessagePushReport(chickData.report.parseValue("seq_id", "0")))
                    }
                    when {
                        action.matches(AmanLink.URL.jump_to_video_live.toRegex()) -> {
                            activity?.apiSpiceMgr?.executeToastKt(HomeStatusRequest(chickData.userId), success = { result ->
                                if (result.data?.isLiving == true) Nav.toAmanLink(activity, action)
                                else UIToast.toast(activity, "直播已结束")
                            })
                        }
                        action.isNotEmpty() -> Nav.toAmanLink(activity, action)
                        else -> {
                            gotoDetailPage(chatId)
                        }
                    }
                } else {
                    gotoDetailPage(chatId)
                }
            }
            removeAnimation()
        }
    }

    private var touchSlop = ViewConfiguration.get(activity).scaledTouchSlop
    private var firstY: Float = 0f
    private var downY: Float = 0f
    private var moveY: Float = 0f

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                firstY = event.y
                downY = event.rawY
            }
            MotionEvent.ACTION_MOVE -> {
                //滑动距离
                moveY = event.rawY
                val scrollY = event.y - firstY
                if (scrollY < 0) {
                    this.y -= abs(scrollY)
                }

            }
            MotionEvent.ACTION_UP -> {
                val dy = event.rawY - downY
                if (dy < -touchSlop) {
                    valueAnimator = ObjectAnimator.ofFloat(this, "translationY", -dip(105).toFloat())
                    valueAnimator?.interpolator = AccelerateInterpolator()
                    valueAnimator?.duration = 100
                    valueAnimator?.start()
                    return true
                }
            }
        }
        return super.onTouchEvent(event)
    }

    private fun gotoDetailPage(chatId: String) {
        if (activity is VideoLiveRoomActivity) {
            (activity as VideoLiveRoomActivity).showChatDialog(chatId, "Notification")
        } else {
            activity?.toImChat(chatId, false)
        }

    }


    fun showMsg(msg: Message?) {
        mMsg = msg
        val data = ((msg as? CustomMessage)?.customInfo?.data as? ChickCustomData)
        val chatId = if (data != null && msg.chatType == TIMConversationType.C2C && msg.peer.getInt(0) < 1000) {
            data.userId.toString()
        } else {
            msg?.peer
        }
        if (chatId?.isNotEmpty() == true) {
            activity?.apiSpiceMgr?.requestImUserInfo(arrayOf(chatId.toIntOrNull()
                    ?: 0), needRefresh = true) {
                val userInfo = getCacheUserInfo(chatId.toIntOrNull() ?: 0)
                new_message_notification_avatar_iv.glideDisplay(userInfo?.avatar.orEmpty(), userInfo?.gender.genderAvatar(userInfo?.isPublisher))
                if (data != null) {
                    if (msg.timMessage.conversation?.peer == SYSTEM_NOTIFY_USER_ID) {
                        new_message_notification_title_tv.text = data.title
                    } else {
                        new_message_notification_title_tv.text = userInfo?.nickname.orEmpty()
                    }
                    new_message_notification_content_tv.text = msg.getSummary().decodeUrl()

                    if (msg.timMessage.conversation?.peer != SYSTEM_NOTIFY_USER_ID
                            || data.canPushWhenAtForeground) {
                        isVisible = true
                        startAnimation()
                    }
                } else {
                    if (MeiUser.getSharedUser().info?.isPublisher == true) {
                        new_message_notification_service_icon_iv.isVisible = userInfo?.specialFlag == true
                        new_message_notification_connect_icon_iv.isVisible = userInfo?.upFlag == true
                    }
                    new_message_notification_title_tv.text = userInfo?.nickname.orEmpty()
                    new_message_notification_content_tv.text = msg?.getSummary().orEmpty().decodeUrl()
                    isVisible = true
                    startAnimation()
                }

            }

        }
    }

    fun startAnimation() {
        if (valueAnimator?.isRunning != true) {
            valueAnimator = ObjectAnimator.ofFloat(0f, 1f)
            valueAnimator?.interpolator = AccelerateInterpolator()
            valueAnimator?.addUpdateListener { it ->
                val fraction = it.animatedFraction
                val height = fraction * getStatusBarHeight()
                this.translationY = (fraction - 1) * dip(105) + height
            }
            valueAnimator?.duration = 500
            valueAnimator?.start()
        }
        if (!isRemove) {
            isRemove = true
            mRunTask = Runnable {
                if (isRemove) {
                    removeAnimation()
                }
            }
            GlobalUIHandler.schedule(mRunTask, 3000)
        } else {
            mRunTask?.let {
                GlobalUIHandler.getInstance().removeCallbacks(it)
                GlobalUIHandler.schedule(it, 3000)
            }
        }
    }

    private fun removeAnimation() {
        isRemove = false
        if (valueAnimator == null) {
            valueAnimator = ObjectAnimator.ofFloat(0f, 1f)
            valueAnimator?.interpolator = AccelerateInterpolator()

        }
        valueAnimator?.addUpdateListener {
            val fraction = it.animatedFraction
            this.translationY = -fraction * dip(105)
            if (fraction == 1f) {
                (parent as? ViewGroup)?.removeView(this)
            }
        }
        valueAnimator?.duration = 300
        valueAnimator?.start()

    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        valueAnimator?.cancel()
        activity = null

    }


}

var appIMNotificationView: AppIMNotificationView? = null
fun MeiCustomBarActivity.getAppIMNotificationView(): AppIMNotificationView? {
    val viewParent = activity.window?.decorView
    appIMNotificationView = viewParent?.findViewById(R.id.app_im_notification_view)
    if (appIMNotificationView == null) {
        val wr = WeakReference<Context>(this)
        appIMNotificationView = wr.get()?.let { AppIMNotificationView(it) }
    }
    return appIMNotificationView
}

fun MeiCustomBarActivity.showImNotificationView(msg: Message?) {
    val viewParent = activity.window?.decorView
    val imView = getAppIMNotificationView()
    if (imView != null && (viewParent as? ViewGroup)?.contains(imView) != true) {
        (viewParent as? ViewGroup)?.addView(imView)
    }
    imView?.bringToFront()
    imView?.showMsg(msg)
}