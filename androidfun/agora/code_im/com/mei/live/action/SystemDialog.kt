package com.mei.live.action

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatDialog
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.joker.im.custom.chick.ChickCustomData
import com.mei.GrowingUtil
import com.mei.base.ui.nav.Nav
import com.mei.course_service.ui.MeiWebCourseServiceActivityLauncher
import com.mei.dialog.getSystemInViteDateView
import com.mei.intimate.TouristsActivity
import com.mei.login.checkLogin
import com.mei.orc.ext.layoutInflaterKt
import com.mei.orc.ext.runDelayedOnUiThread
import com.mei.orc.imageload.glide.glideDisplay
import com.mei.orc.john.model.JohnUser
import com.mei.wood.R
import com.mei.wood.cache.requestUserInfo
import com.mei.wood.dialog.DialogData
import com.mei.wood.dialog.NormalDialogLauncher
import com.mei.wood.event.honorMedalMsgQueue
import com.mei.wood.event.showHonorMedalDialog
import com.mei.wood.event.upstreamOutRoom
import com.mei.wood.ext.AmanLink
import com.mei.wood.extensions.appLifeTransformer
import com.mei.wood.extensions.executeKt
import com.mei.wood.extensions.subscribeBy
import com.mei.wood.ui.MeiCustomBarActivity
import com.mei.wood.util.MeiUtil
import com.mei.wood.web.MeiWebData
import com.net.model.chick.room.RoomList.TestReport
import com.net.model.chick.tim.SystemInviteResult
import com.net.model.room.RoomType
import com.net.network.chick.user.TestReportDialogRequest
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import java.util.concurrent.TimeUnit

/**
 * 佛祖保佑         永无BUG
 *w
 * @author Created by joker on 2020/10/29
 */

class SystemDialog(val activity: FragmentActivity) : AppCompatDialog(activity) {

    init {
        activity.lifecycle.addObserver(object : LifecycleEventObserver {
            override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                if (event == Lifecycle.Event.ON_DESTROY) {
                    this@SystemDialog.dismiss()
                }
            }
        })
    }

    override fun dismiss() {
        if (!activity.isDestroyed) {
            super.dismiss()
        }
    }
}

var globalDialog: SystemDialog? = null

/**
 *  全局弹窗
 */
@SuppressLint("SetTextI18n")
fun FragmentActivity.showGlobalGeneralDialog(systemDialogInfo: SystemInviteResult.SystemInviteJoin, back: () -> Unit) {
    if (globalDialog?.isShowing == true) return
    var disposable: Disposable? = null
    val contentView = layoutInflaterKt(R.layout.dialog_global_general)
    val dialog = SystemDialog(this).apply {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window?.apply {
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            attributes = attributes?.apply {
                width = WindowManager.LayoutParams.MATCH_PARENT
                height = WindowManager.LayoutParams.MATCH_PARENT
            }
        }
        setOnDismissListener {
            globalDialog = null
            disposable?.dispose()
            back()
        }
        setContentView(contentView)
        setCanceledOnTouchOutside(systemDialogInfo.canClose)
    }
    val action = systemDialogInfo.action
    val webData = MeiUtil.getJsonObject(action, AmanLink.URL.NEW_JSON_WEBVIEW) ?: MeiWebData()


    contentView.findViewById<ImageView>(R.id.general_cover).apply {
        glideDisplay(systemDialogInfo.image.orEmpty(), isGif = systemDialogInfo.image.orEmpty().endsWith("gif"))
        setOnClickListener {
            GrowingUtil.track("push_popup_click",
                    "popup_type", "全局弹窗",
                    "popup_click_type", webData.url,
                    "time_stamp", (System.currentTimeMillis() / 1000).toString())

            dialog.dismiss()
            Nav.toAmanLink(this@showGlobalGeneralDialog, systemDialogInfo.action)
        }
    }
    contentView.findViewById<ImageView>(R.id.circle_close).apply {
        isVisible = systemDialogInfo.showCloseBtn
        setOnClickListener {
            dialog.dismiss()
        }
    }
    val countDown = contentView.findViewById<TextView>(R.id.count_down).apply {
        isVisible = systemDialogInfo.timeOut > 0 && systemDialogInfo.showTimeOut
    }
    if (systemDialogInfo.timeOut > 0) {
        disposable = Observable.interval(1, TimeUnit.SECONDS)
                .appLifeTransformer(this)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy {
                    countDown.text = "(${systemDialogInfo.timeOut - it}s)"
                    if (it == systemDialogInfo.timeOut.toLong()) {
                        dialog.dismiss()
                    }
                }
    }

    GrowingUtil.track("push_popup_view",
            "popup_type", "全局弹窗",
            "view_type", webData.url,
            "time_stamp", (System.currentTimeMillis() / 1000).toString())

    dialog.show()
    globalDialog = dialog

}


/**
 * 系统推送消息是通过http轮训请求。
 */
fun MeiCustomBarActivity.systemInviteUp(data: ChickCustomData, isSystemInvite: Boolean, needCallHandle: Boolean = true) {
    //刷新个人信息，内部要用
    apiSpiceMgr.requestUserInfo(arrayOf(data.userId), needRefresh = true) {
        if (globalDialog?.isShowing == true) return@requestUserInfo
        val dialog = SystemDialog(this).apply {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            window?.apply {
                setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                attributes = attributes?.apply {
                    width = WindowManager.LayoutParams.MATCH_PARENT
                    height = WindowManager.LayoutParams.MATCH_PARENT
                }
            }
            setOnDismissListener {
                globalDialog = null
                val notShow = !this@systemInviteUp.findHasInviteDialog()
                if (honorMedalMsgQueue.isNotEmpty() && notShow) {
                    showHonorMedalDialog(honorMedalMsgQueue.poll())
                }
            }
        }

        val view = getSystemInViteDateView(this, data) { back ->
            GrowingUtil.track("push_popup_click",
                    "popup_type", "系统邀请进入直播间",
                    "popup_click_type", if (back == 0) data.inviteUp?.leftText
                    ?: "再想想" else data.inviteUp?.rightText ?: "立即连线",
                    "room_type", if (RoomType.parseValue(data.roomType) == RoomType.EXCLUSIVE) "专属房" else "普通房",
                    "click_jump", if (back == 0) findJumpLink(data.inviteUp?.leftAction.orEmpty()) else findJumpLink(data.inviteUp?.rightAction.orEmpty()),
                    "time_stamp", (System.currentTimeMillis() / com.mei.orc.unit.TimeUnit.SECOND).toString()
            )
            dialog.dismiss()
            upstreamOutRoom(data, isSystemInvite, back, needCallHandle)
        }
        dialog.setContentView(view)
        dialog.setCanceledOnTouchOutside(false)
        GrowingUtil.track(
                "push_popup_view",
                "popup_type",
                when {
                    isSystemInvite -> "系统邀请进入直播间"
                    data.inviteUp?.canFree == true -> "知心达人邀请免费连线"
                    else -> "知心达人邀请付费连线"
                },
                "room_type", if (RoomType.parseValue(data.roomType) == RoomType.EXCLUSIVE) "专属房" else "普通房",
                "time_stamp", (System.currentTimeMillis() / com.mei.orc.unit.TimeUnit.SECOND).toString()
        )
        runDelayedOnUiThread(data.timeOut.toLong() * 1000) {
            dialog.dismiss()
        }
        dialog.show()
        globalDialog = dialog
    }
}

private fun findJumpLink(link: String): String {
    val toRegex = "[a-zA-Z0-9_]+_[a-zA-Z0-9_]+".toRegex()
    val find = toRegex.find(link, 0)
    return find?.value.orEmpty()
}

fun TouristsActivity.showTestReportDialog(data: TestReport) {
    val dialog = NormalDialogLauncher.newInstance()
    val contentView = layoutInflaterKt(R.layout.dialog_test_report)
    contentView.findViewById<TextView>(R.id.test_report_text).apply {
        paint.isFakeBoldText = true
        text = data.title
    }
    contentView.findViewById<TextView>(R.id.report_title).apply {
        paint.isFakeBoldText = true
        text = data.text
    }
    contentView.findViewById<ImageView>(R.id.report_cover).glideDisplay(data.image.orEmpty())

    contentView.findViewById<TextView>(R.id.login_look)
            .apply { text = data.buttonText }
            .setOnClickListener {
                dialog.dismissAllowingStateLoss()
                GrowingUtil.track("push_popup_click", "popup_type", "落地页下载_测试报告生成", "popup_click_type", "登录查看")
                checkLogin {
                    if (it) {
                        MeiWebCourseServiceActivityLauncher.startActivity(this, MeiWebData("https://sssr.meidongli.net/dove/v16/test-evaluation", 0))
                    }
                }
            }
    GrowingUtil.track("push_popup_view", "popup_type", "落地页下载_测试报告生成")

    // 上报服务器已弹窗
    apiSpiceMgr.executeKt(TestReportDialogRequest(JohnUser.getSharedUser().userID, JohnUser.getSharedUser().sessionID))

    dialog.showDialog(this, DialogData(customView = contentView), okBack = {})
}
