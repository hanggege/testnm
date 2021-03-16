package com.mei.push

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.fragment.app.FragmentActivity
import com.joker.im.custom.chick.ChickCustomData
import com.mei.GrowingUtil
import com.mei.base.ui.nav.Nav
import com.mei.init.spiceHolder
import com.mei.live.ui.dialog.showQuickConsultDialog
import com.mei.orc.ext.runDelayedOnUiThread
import com.mei.orc.ui.toast.UIToast
import com.mei.orc.util.app.clearClipboard
import com.mei.orc.util.app.getCopyFromClipBoard
import com.mei.orc.util.app.getUserAgent
import com.mei.orc.util.save.getNonValue
import com.mei.orc.util.save.putValue
import com.mei.splash.ui.SplashActivity
import com.mei.wood.dialog.*
import com.mei.wood.event.handSnapUpExclusiveDialog
import com.mei.wood.ext.AmanLink
import com.mei.wood.extensions.executeKt
import com.mei.wood.extensions.executeToastKt
import com.mei.wood.ui.MeiCustomActivity
import com.mei.wood.util.AppManager
import com.mei.wood.util.logDebug
import com.mei.wood.util.parseValue
import com.net.network.chick.friends.HomeStatusRequest
import com.net.network.report.DdlReportRequest
import com.net.network.report.GetuiReportRequest
import com.net.network.report.MessagePushReport

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2020/3/11
 *
 * 个推接收后处理
 */

var payLoad: Payload? = null
var imOfflineCustomInfo: ChickCustomData? = null

fun handlePush() {
    val currActivity = AppManager.getInstance().currentActivity() as? FragmentActivity
    if (currActivity != null && isForeground) {
        payLoad?.let { info ->
            val cmdAction = info.cmd
            if (info.cmd != null) {
                currActivity.handLink(cmdAction, info.getui_push_log_id)
            } else if (info.leftAction != null && info.rightAction != null) {
                currActivity.runOnUiThread {
                    NormalDialogLauncher.newInstance().showDialog(currActivity,
                            DialogData(info.content, info.leftAction.text, info.rightAction.text, title = info.title, canceledOnTouchOutside = false),
                            object : DialogBack {
                                override fun handle(state: Int) {
                                    when (state) {
                                        DISSMISS_DO_OK -> {
                                            currActivity.handLink(info.rightAction, info.getui_push_log_id)
                                        }
                                        DISSMISS_DO_CANCEL -> {
                                            currActivity.handLink(info.leftAction, info.getui_push_log_id)
                                        }
                                    }
                                }

                            })
                }
            }
            payLoad = null
        }
    }
}

fun handleIMPush() {
    val currentActivity = AppManager.getInstance().currentActivity() as? FragmentActivity
    if (currentActivity != null && isForeground) {
        imOfflineCustomInfo?.let { data ->
            logDebug("getuipayload", data.type.name)
            if (data.couponMessageItem != null) {
                currentActivity.showQuickConsultDialog(data) {
                    (currentActivity as? MeiCustomActivity)?.handSnapUpExclusiveDialog(data)
                }
            } else {
                val action = data.action
                if (data.report.isNotEmpty()) {
                    spiceHolder().apiSpiceMgr.executeKt(MessagePushReport(data.report.parseValue("seq_id", "0")))
                }
                when {
                    action.matches(AmanLink.URL.jump_to_video_live.toRegex()) -> {
                        spiceHolder().apiSpiceMgr.executeToastKt(HomeStatusRequest(data.userId), success = { result ->
                            if (result.data?.isLiving == true) Nav.toAmanLink(currentActivity, action)
                            else UIToast.toast(currentActivity, "直播已结束")
                        })
                    }
                    action.isNotEmpty() -> Nav.toAmanLink(currentActivity, action)
                    else -> {
                    }
                }
            }
            imOfflineCustomInfo = null
        }

    }
}

fun Activity.ddlUploadServer() {
    val clipBoard = getCopyFromClipBoard("")
    if (clipBoard.startsWith("SZBTJZX:")) {
        if (clipBoard.length > 2000) {
            "ddl_upload_info_server".putValue(clipBoard.toString().substring(0, 2000))
        } else {
            "ddl_upload_info_server".putValue(clipBoard.toString())
        }
        //清除剪切板
        clearClipboard()
    }
    val uploadInfo = "ddl_upload_info_server".getNonValue("")
    if (uploadInfo.isNotEmpty()) {
        val userAgent = getUserAgent()
        spiceHolder().apiSpiceMgr.executeKt(DdlReportRequest(uploadInfo, userAgent), success = {
            "ddl_upload_info_server".putValue("")
        })
    }
}

private fun FragmentActivity.handLink(action: Payload.ActionInfo, getuiPushLogId: String) {
    Nav.toAmanLink(this, action.action)
    action.gioReportList.orEmpty().forEach {
        GrowingUtil.track(it.gioEventKey, it.gioValue)
    }
    spiceHolder().apiSpiceMgr.executeKt(GetuiReportRequest(action.actionId, action.actionType, getuiPushLogId))

}

/** 是否是在前台 **/
private var isForeground = false

class PushHandle : Application.ActivityLifecycleCallbacks {
    private var paused = false

    //handler用于处理切换activity时的短暂时期可能出现的判断错误
    private val handler = Handler()
    private var check: Runnable? = null
    override fun onActivityCreated(activity: Activity, p1: Bundle?) {

    }


    override fun onActivityResumed(activity: Activity) {
        paused = false
        isForeground = true
        check?.let { handler.removeCallbacks(it) }
        if (activity is SplashActivity) {

        } else {
            runDelayedOnUiThread(1500) {
                handlePush()
                handleIMPush()
            }
        }
        activity.ddlUploadServer()
    }

    override fun onActivityStarted(activity: Activity) {

    }


    override fun onActivityPaused(activity: Activity) {
        paused = true
        check?.let { handler.removeCallbacks(it) }
        check = Runnable {
            if (isForeground && paused) {
                isForeground = false
                Log.i("ActivityLifecycle", "went background")
            } else {
                Log.i("ActivityLifecycle", "still foreground")
            }
        }.apply {
            handler.postDelayed(this, 500)
        }


    }

    override fun onActivityDestroyed(activity: Activity) {
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {

    }

    override fun onActivityStopped(activity: Activity) {

    }

}

