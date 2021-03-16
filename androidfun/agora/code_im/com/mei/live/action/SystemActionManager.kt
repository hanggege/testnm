package com.mei.live.action

import android.app.Activity
import android.app.Application
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import com.joker.im.custom.chick.ChickCustomData
import com.joker.im.provider.imIsForeground
import com.mei.init.meiApplication
import com.mei.init.spiceHolder
import com.mei.orc.activity.MeiBaseBarActivity
import com.mei.orc.dialog.MeiSupportDialogFragment
import com.mei.orc.john.model.JohnUser
import com.mei.orc.util.save.getBooleanMMKV
import com.mei.orc.util.save.getStringMMKV
import com.mei.orc.util.save.putMMKV
import com.mei.splash.ui.SplashActivity
import com.mei.wood.AppLifecycle
import com.mei.wood.event.honorMedalMsgQueue
import com.mei.wood.event.showHonorMedalDialog
import com.mei.wood.extensions.executeKt
import com.mei.wood.extensions.subscribeBy
import com.mei.wood.ui.MeiCustomBarActivity
import com.mei.wood.ui.TabMainActivity
import com.mei.wood.util.AppManager
import com.net.model.chick.global.TipsGet
import com.net.network.chick.global.TipsGetRequest
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.TimeUnit


/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2020/8/4
 */

var isFirst = true
fun Application.registerSystemAction() {
    if (isFirst) {
        isFirst = false
        registerActivityLifecycleCallbacks(AppLifecycle(onResume = {
            it.checkSystemAction()
        }))
        Observable.interval(10, 5, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy {
                    systemActionView.checkAnimLooper()
                    requestGlobalTips()
                }
    }
}

//已经显示过了的msg
private var hasShowOver = hashSetOf<String>()
const val APP_NEXT_START_KEY = "app_next_start_key"
var requestCount = -1
private fun requestGlobalTips() {
    if (JohnUser.getSharedUser().hasLogin()) {
        val appNextStartKey = APP_NEXT_START_KEY.getStringMMKV()
        requestCount++
        val isOpenSystemInvite = "new_message_invitation_sb${JohnUser.getSharedUser().userID}".getBooleanMMKV(true)
        val preCurrentActivity = AppManager.getInstance().currentActivity() as? FragmentActivity
        val preTabId = if (preCurrentActivity is TabMainActivity) {
            preCurrentActivity.getCurrentTabItem().name
        } else ""

        spiceHolder().apiSpiceMgr.executeKt(TipsGetRequest(appNextStartKey, !preCurrentActivity.isCanShowDialog(), requestCount, preTabId, isOpenSystemInvite), success = {
            it.data?.apply {
                APP_NEXT_START_KEY.putMMKV(nextStartKey)
                /**顶部充值信息*/
                tipsList.orEmpty().forEach { info ->
                    if (!hasShowOver.any { k -> k == info.msgId }) {
                        msgQueue.offer(info)
                    }
                    hasShowOver.add(info.msgId)
                }
                if (msgQueue.isNotEmpty()) systemActionView.checkAnimLooper()

                /**系统全局弹窗*/
                val currActivity = AppManager.getInstance().currentActivity() as? MeiCustomBarActivity
                val currTabId = if (currActivity is TabMainActivity) {
                    currActivity.getCurrentTabItem().name
                } else ""
                if (currActivity.isCanShowDialog() && (tabId == currTabId || tabId.isNullOrEmpty())) {
                    message?.content?.also { content ->
                        if (message.type == "SYSTEM_INVITE") {
                            currActivity?.systemInviteUp(ChickCustomData().apply {
                                userId = content.userId.toInt()
                                inviteUp = content.inviteInfo
                                title = content.title.orEmpty()
                                roomId = content.roomId.orEmpty()
                                timeOut = content.timeOut
                            }, isSystemInvite = true,
                                    needCallHandle = content.needCallHandle)
                        } else if (message.type == "DIALOG") {
                            currActivity?.showGlobalGeneralDialog(content) {
                                if (honorMedalMsgQueue.isNotEmpty() && !(currActivity.findHasInviteDialog())) {
                                    currActivity.showHonorMedalDialog(honorMedalMsgQueue.poll())
                                }
                            }
                        }
                    }
                }
            }
        })
    }
}

/**
 * 是否能够显示系统全局弹窗
 */
private fun FragmentActivity?.isCanShowDialog(): Boolean {
    val isShowTabMain = this is TabMainActivity && !findHasInviteDialog()
    return JohnUser.getSharedUser().hasLogin() && imIsForeground && isShowTabMain
}

fun FragmentActivity.findHasInviteDialog(): Boolean {
    return supportFragmentManager.fragments.any {
        it is MeiSupportDialogFragment
    } || globalDialog?.isShowing == true
}


private var msgQueue: LinkedBlockingQueue<TipsGet.ActionInfo> = LinkedBlockingQueue()
private val systemActionView: SystemActionView by lazy {
    SystemActionView(meiApplication().applicationContext) {
        val data = msgQueue.poll()
        data
    }
}

private fun Activity.checkSystemAction() {
    (window.decorView as? ViewGroup)?.let { contentView ->
        if (systemActionView.parent != contentView && this is MeiBaseBarActivity && this !is SplashActivity) {
            systemActionView.attachActivity = this
            (systemActionView.parent as? ViewGroup)?.removeView(systemActionView)
            contentView.addView(systemActionView)
            systemActionView.checkAnimLooper()
        }
    }
}


interface IgnoreSystemAction {
    fun isShow(data: TipsGet.ActionInfo?): Boolean = false
}