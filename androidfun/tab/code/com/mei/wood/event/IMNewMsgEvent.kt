package com.mei.wood.event

import android.annotation.SuppressLint
import android.app.Activity
import androidx.fragment.app.FragmentActivity
import com.joker.im.Message
import com.joker.im.custom.CustomType
import com.joker.im.custom.HonorMedalType
import com.joker.im.custom.chick.ChickCustomData
import com.joker.im.custom.chick.HonorMedal
import com.joker.im.custom.chick.InviteJoinInfo
import com.joker.im.mapToConversation
import com.joker.im.message.CustomMessage
import com.mei.agora.work.AGORA_FILE_LOG
import com.mei.base.common.CANCEL_SHIELD_USER
import com.mei.base.common.HONOR_MEDAL_CHANGED
import com.mei.base.common.SHIELD_USER
import com.mei.base.ui.nav.Nav
import com.mei.base.upload.MeiUploader
import com.mei.chat.ui.view.upstreamLoadingView
import com.mei.dialog.PayFromType
import com.mei.dialog.showPayDialog
import com.mei.im.ext.isCmdId
import com.mei.im.ui.IMC2CMessageActivity
import com.mei.im.ui.ext.handleShielding
import com.mei.init.spiceHolder
import com.mei.live.LiveEnterType
import com.mei.live.action.findHasInviteDialog
import com.mei.live.parseLiveEnterType
import com.mei.live.ui.VideoLiveRoomActivity
import com.mei.live.ui.VideoLiveRoomActivityLauncher
import com.mei.live.ui.dialog.*
import com.mei.orc.callback.TaskCallback
import com.mei.orc.event.postAction
import com.mei.orc.ext.runDelayedOnUiThread
import com.mei.orc.john.model.JohnUser
import com.mei.orc.john.upload.PhotoList
import com.mei.orc.ui.toast.UIToast
import com.mei.orc.util.image.uriToFile
import com.mei.orc.util.string.getInt
import com.mei.wood.cache.requestUserInfo
import com.mei.wood.dialog.DISSMISS_DO_OK
import com.mei.wood.extensions.executeToastKt
import com.mei.wood.modle.TAB_ITEM
import com.mei.wood.ui.MeiCustomBarActivity
import com.mei.wood.ui.TabMainActivity
import com.mei.wood.util.AppManager
import com.mei.wood.util.logDebug
import com.net.MeiUser
import com.net.model.config.UPLOAD_uptoken
import com.net.model.room.RoomApplyType
import com.net.network.chat.ExclusiveHandleRequest
import com.net.network.room.ApplyHandleRequest
import com.net.network.room.SnapUpApplyHandleRequest
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.TimeUnit

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2019-12-04
 */

val honorMedalMsgQueue: LinkedBlockingQueue<ChickCustomData> = LinkedBlockingQueue()
var mDownTask: Disposable? = null

class IMNewMsgEvent(var tabMainActivity: TabMainActivity) {
    fun handleMessage(list: List<Message>) {
        list.mapNotNull { it as? CustomMessage }.forEach { msg ->
            if (!msg.sender.getInt().isCmdId()) {
                /** 只有发指令的111..115消息才能走下去  **/
                return
            }
            logDebug("IMNewMsgEvent_handleIMEvent: ${msg.customMsgType.name}")
            msg.timMessage.conversation?.mapToConversation()?.readAllMessage()
            when (msg.customMsgType) {
                CustomType.toast -> {
                    val ids = (msg.customInfo?.data as? ChickCustomData)?.userIds.orEmpty()
                    if (ids.isEmpty() || ids.any { it == JohnUser.getSharedUser().userID }) {
                        UIToast.toast(msg.getSummary())
                    }
                }
                CustomType.invite_up -> {// 邀请连线
                    /** 接收到的消息不要超过30s，不然是超时消息，不再处理 **/
                    val msgTimeOut = (System.currentTimeMillis() / 1000 - msg.timMessage.timestamp()) > 30
                    (AppManager.getInstance().currentActivity() as? MeiCustomBarActivity)?.let { activity ->
                        if (activity !is VideoLiveRoomActivity && upstreamLoadingView?.isShow != true && !msgTimeOut) {
                            (msg.customInfo?.data as? ChickCustomData)?.let { data ->
                                activity.handRoomDialog(data)
                            }
                        }
                    }
                }
                CustomType.apply_upstream_result -> {//同意连线
                    /** 接收到的消息不要超过30s，不然是超时消息，不再处理 **/
                    val msgTimeOut = (System.currentTimeMillis() / 1000 - msg.timMessage.timestamp()) > 30
                    (AppManager.getInstance().currentActivity() as? MeiCustomBarActivity)?.let { activity ->
                        if (activity !is VideoLiveRoomActivity && upstreamLoadingView?.isShow != true && !msgTimeOut) {
                            (msg.customInfo?.data as? ChickCustomData)?.let { data ->
                                activity.handRoomDialog(data)
                            }
                        }
                    }
                }
                CustomType.apply_exclusive -> {
                    /** 接收到的消息不要超过30s，不然是超时消息，不再处理 **/
                    val msgTimeOut = (System.currentTimeMillis() / 1000 - msg.timMessage.timestamp()) > 60
                    val topActivity = AppManager.getInstance().currentActivity()
                    if (topActivity !is VideoLiveRoomActivity && !msgTimeOut) {
                        (msg.customInfo?.data as? ChickCustomData)?.let { data ->
                            (topActivity as? MeiCustomBarActivity)?.applyExclusive(data)
                        }
                    }
                }
                CustomType.upload_local_log -> {
                    /** 上传声网日志*/
                    (msg.customInfo?.data as? ChickCustomData)?.let { data ->
                        if (AGORA_FILE_LOG.isNotEmpty() && data.uploadLog?.logType == "agora") {
                            val uploadManager = MeiUploader(spiceHolder().apiSpiceMgr)
                            val upToken = UPLOAD_uptoken().apply {
                                upToken = data.uploadLog?.token
                                dir = data.uploadLog?.key
                                url = data.uploadLog?.url
                                localFilePath = AGORA_FILE_LOG
                            }
                            uploadManager.uploadLogFile(upToken, uriToFile(arrayListOf(AGORA_FILE_LOG)), arrayListOf(AGORA_FILE_LOG), object : TaskCallback<PhotoList>() {
                                override fun onSuccess(photos: PhotoList) {
                                    logDebug("声网日志上传成功")
                                }

                                override fun onFailed(exception: java.lang.Exception?, errCode: Int, vararg objects: Any) {
                                    logDebug(errCode.toString())
                                }
                            })
                        }
                    }
                }
                CustomType.honor_medal -> {
                    /**荣誉勋章*/
                    (msg.customInfo?.data as? ChickCustomData)?.let {
                        if (!honorMedalMsgQueue.any { data -> it.msgId == data.msgId }) {
                            honorMedalMsgQueue.offer(it)
                        }
                        startShowDialogTask()
                        (AppManager.getInstance().currentActivity() as? TabMainActivity)?.apply {
                            if (getCurrentTabItem().name == TAB_ITEM.MY.name) {
                                postAction(HONOR_MEDAL_CHANGED, true)
                            }
                        }

                    }
                }

                CustomType.quick_apply_exclusive -> {
                    /**快速咨询申请**/
                    val msgTimeOut = (System.currentTimeMillis() / 1000 - msg.timMessage.timestamp()) > 60
                    val topActivity = AppManager.getInstance().currentActivity()
                    if (topActivity !is VideoLiveRoomActivity && !msgTimeOut) {
                        (msg.customInfo?.data as? ChickCustomData)?.let { data ->
                            (topActivity as? MeiCustomBarActivity)?.let { activity ->
                                activity.showQuickConsultDialog(data) {
                                    activity.handSnapUpExclusiveDialog(data)
                                }
                            }
                        }
                    }
                }
                CustomType.exclusive_block_notify -> {
                    /**拉黑操作*/
                    (AppManager.getInstance().currentActivity() as? MeiCustomBarActivity)?.shieldingTask(msg)
                }

                else -> {

                }
            }

        }
    }


}

/**
 * 处理拉黑操作更新数据
 */
fun MeiCustomBarActivity.shieldingTask(msg: CustomMessage) {
    (msg.customInfo?.data as? ChickCustomData)?.let { data ->
        if (data.userInfoChange?.status == 7) {
            //拉黑操作(如果当前正在跟该用户私聊，就需要弹起透明遮罩，点击遮罩触发弹框)

            when {
                AppManager.getInstance().getTargetActivity(IMC2CMessageActivity::class.java) != null -> {
                    postAction(SHIELD_USER, data.userIds)
                }
                this is IMC2CMessageActivity -> {
                    (this as? IMC2CMessageActivity)?.handleShielding(data.userIds)
                }

                else -> {
                }
            }
        } else {
            //解除拉黑不需任何展示，刷新会话
            postAction(CANCEL_SHIELD_USER, data.userIds)
        }
    }
}

@SuppressLint("CheckResult")
fun startShowDialogTask() {
    if (mDownTask != null) {
        return
    }
    Observable.interval(0, 5, TimeUnit.SECONDS)
            .doOnSubscribe { task ->
                mDownTask = task
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                if (honorMedalMsgQueue.isNotEmpty()) {
                    (AppManager.getInstance().currentActivity() as? FragmentActivity)?.let { activity ->
                        if (!activity.findHasInviteDialog()) {
                            val data = honorMedalMsgQueue.poll()
                            val type = data?.medalInfo?.medals?.firstOrNull()?.type.orEmpty()
                            if (type == HonorMedal.HonorMedalType.USER_SPECIAL_SERVICE_AMOUNT.name
                                    || type == HonorMedal.HonorMedalType.PUBLISHER_FANS.name) {
                                //延迟等待购买服务动画，粉丝动效完成后再弹勋章
                                runDelayedOnUiThread(4000) {
                                    activity.showHonorMedalDialog(data)
                                }
                            } else {
                                activity.showHonorMedalDialog(data)
                            }
                        }
                    }

                } else {
                    if (mDownTask?.isDisposed == false) {
                        mDownTask?.dispose()
                        mDownTask = null
                    }
                }

            }

}

/**
 * 处理房间消息过来的弹窗
 */
fun MeiCustomBarActivity.handRoomDialog(data: ChickCustomData) {
    apiSpiceMgr.requestUserInfo(arrayOf(data.userId), needRefresh = true) {
        if (data.serviceInfo != null) {
            activity.showSpecialServiceDialog(data) { dialog, back ->
                dialog.dismissAllowingStateLoss()
                upstreamOutRoom(data, false, back)
            }
        } else {
            activity.showLiveAvatarDialog(data) { dialog, back ->
                dialog.dismissAllowingStateLoss()
                upstreamOutRoom(data, false, back)
            }
        }
    }
}

/**
 * 私密连线申请过来的消息
 */
fun MeiCustomBarActivity.applyExclusive(data: ChickCustomData) {
    apiSpiceMgr.requestUserInfo(arrayOf(data.userId), needRefresh = true) {
        if (data.serviceInfo != null) {
            showSpecialServiceDialog(data) { dialog, back ->
                dialog.dismissAllowingStateLoss()
                handExclusiveDialog(data, back)
            }
        } else {
            showLiveAvatarDialog(data) { dialog, back ->
                dialog.dismissAllowingStateLoss()
                handExclusiveDialog(data, back)
            }
        }
    }
}

fun MeiCustomBarActivity.handExclusiveDialog(data: ChickCustomData, back: Int) {
    if (MeiUser.getSharedUser().info?.isPublisher == true) {
        showLoading(true)
        loadingView.loadContainer.isClickable = true
        apiSpiceMgr.executeToastKt(ExclusiveHandleRequest().apply {
            result = back
            userId = data.userId
        }, success = {
            if (it.isSuccess && back == 1) {
                it.data?.roomInfo?.let { info ->
                    VideoLiveRoomActivityLauncher.startActivity(activity, info.roomId, LiveEnterType.myself_apply_vip, info)
                }
            }
        }, finish = {
            showLoading(false)
        })
    } else {
        upstreamOutRoom(data, false, back)
    }
}

fun MeiCustomBarActivity.handSnapUpExclusiveDialog(data: ChickCustomData) {
    if (MeiUser.getSharedUser().info?.isPublisher == true) {
        showLoading(true)
        loadingView.loadContainer.isClickable = true
        apiSpiceMgr.executeToastKt(SnapUpApplyHandleRequest().apply {
            roomId = ""
            userId = data.userId
        }, success = {
            if (it.isSuccess) {
                it.data?.roomInfo?.let { info ->
                    VideoLiveRoomActivityLauncher.startActivity(activity, info.roomId, LiveEnterType.myself_apply_vip, info)
                }
            }
        }, finish = {
            showLoading(false)
        })
    } else {
        upstreamOutRoom(data, false, DISSMISS_DO_OK)
    }
}

/**
 * @param needCallHandle 是否需要调用接口处理结果
 * @param needTopUp 是否需要弹充值窗
 */
fun FragmentActivity.upstreamOutRoom(data: ChickCustomData, isSystemInvite: Boolean, back: Int, needCallHandle: Boolean = true, needTopUp: Boolean = false, videoMode: Int = 0) {
    if (!needCallHandle) {
        if (isSystemInvite) {
            if (back == 1) {
                Nav.toAmanLink(this, data.inviteUp?.rightAction.orEmpty())
            } else if (back == 0) {
                Nav.toAmanLink(this, data.inviteUp?.leftAction.orEmpty())
            }
        } else {
            if (back == 1) {
                VideoLiveRoomActivityLauncher.startActivity(
                        this, data.roomId,
                        if (isSystemInvite) {//这个无
                            LiveEnterType.system_auto_invite
                        } else {
                            parseLiveEnterType(data.inviteUp?.from)
                        })
            }
        }
    } else {
        (this as? MeiCustomBarActivity)?.run {
            showLoading(true)
            loadingView.loadContainer.isClickable = true
            apiSpiceMgr.executeToastKt(ApplyHandleRequest().apply {
                userId = JohnUser.getSharedUser().userID
                roomId = data.roomId
                type = data.parseApplyType()
                result = back
                this.videoMode = videoMode
            }, success = {
                /**502 表明麦上有人，只是进入直播间  不连线*/
                val payFromType = if (data.type == CustomType.invite_up) PayFromType.INVITED_UP_APPLY else PayFromType.UP_APPLY
                if (it.isSuccess || it.rtn == 502) {
                    val option = it.data?.option
                    if (option == null) {
                        upstreamOutRoom(data, isSystemInvite, back, !needCallHandle)
                    } else {
                        showApplyPrivateUpstreamDialog(option, payFromType) { back, close ->
                            var callback = back
                            if (back == 0) {
                                callback = 3
                            } else if (back == 2) {
                                callback = 4
                            }
                            upstreamOutRoom(data, isSystemInvite, callback, needCallHandle, needTopUp, if (close) 1 else 0)
                        }
                    }
                } else if (it.rtn == 700 && needTopUp) {
                    /**余额不足**/
                    showPayDialog(payFromType)
                }
            }, finish = {
                showLoading(false)
            })
        }
    }
}

fun ChickCustomData.parseApplyType(): RoomApplyType {
    return when (type) {
        CustomType.invite_up -> {
            RoomApplyType.INVITE_UPSTREAM
        }
        CustomType.apply_upstream_result -> {
            RoomApplyType.UPSTREAM
        }
        else -> {
            RoomApplyType.parseValue(applyType)
        }
    }
}


/**
 * 筛选出当前弹框
 */
fun FragmentActivity.screenCurrentDialog(tag: String): Boolean {
    return supportFragmentManager.fragments.any {
        (it.tag == tag)
    }
}


/**
 * 荣誉勋章弹框
 */
fun FragmentActivity.showHonorMedalDialog(data: ChickCustomData?) {


    data?.let {
        if (it.medalInfo?.type == HonorMedalType.single.name) {
            showMedalDialog(it, back = {
                if (honorMedalMsgQueue.isNotEmpty() && !findHasInviteDialog()) {
                    showHonorMedalDialog(honorMedalMsgQueue.poll())
                }
            })
        } else {
            showMoreMedalDialog(it, back = {
                if (honorMedalMsgQueue.isNotEmpty() && !findHasInviteDialog()) {
                    showHonorMedalDialog(honorMedalMsgQueue.poll())
                }
            })
        }
    }
}


interface ISystemInviteJoinShow {
    //这个页面是够显示
    fun isShow(sendId: String, info: InviteJoinInfo): Boolean
}


/**
 * 那些页面不展示邀请相亲
 */
@Suppress("UNUSED_PARAMETER")
private fun Activity.isInterceptInviteIn(data: ChickCustomData, isSystemInvite: Boolean): Boolean {
    if (this is TabMainActivity) {
        return false
    }
    return true
}



