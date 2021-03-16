@file:Suppress("DEPRECATION")

package com.mei.wood.web

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.text.TextUtils
import android.webkit.*
import androidx.core.view.isVisible
import com.joker.connect.PayCallBack
import com.joker.im.custom.chick.ChickCustomData
import com.joker.model.PayErrorCode
import com.joker.model.PayFailure
import com.joker.model.PaySuccess
import com.joker.support.TdSdk
import com.mei.base.common.*
import com.mei.base.saveImgToGallery
import com.mei.base.ui.nav.Nav
import com.mei.base.util.dialog.ifUserConfirm
import com.mei.base.util.web.initWebViewSetting
import com.mei.chat.toImChat
import com.mei.chat.ui.view.startPrivateUpstream
import com.mei.dialog.showReportDialog
import com.mei.live.LiveEnterType
import com.mei.live.ui.VideoLiveRoomActivityLauncher
import com.mei.live.ui.dialog.showConsultDirectionDialog
import com.mei.live.ui.dialog.showCouponDialog
import com.mei.live.ui.dialog.showSnapUpSuccessDialog
import com.mei.login.checkLogin
import com.mei.orc.callback.Callback
import com.mei.orc.dialog.BottomSelectFragment
import com.mei.orc.event.bindAction
import com.mei.orc.event.postAction
import com.mei.orc.ext.*
import com.mei.orc.john.model.JohnUser
import com.mei.orc.rxpermission.requestMulPermissionZip
import com.mei.orc.rxpermission.requestSinglePermission
import com.mei.orc.ui.toast.UIToast
import com.mei.orc.util.app.copyToClipboard
import com.mei.orc.util.app.doAddContacts
import com.mei.orc.util.click.setOnClickNotDoubleListener
import com.mei.orc.util.json.getIntValue
import com.mei.orc.util.json.getStringValue
import com.mei.orc.util.json.json2Obj
import com.mei.orc.util.permission.PermissionCheck
import com.mei.orc.util.string.getInt
import com.mei.orc.util.string.getLong
import com.mei.pay.thirdPartPaymentV2
import com.mei.player.view.playerFloatBar
import com.mei.wood.R
import com.mei.wood.dialog.ConfirmDialogData
import com.mei.wood.dialog.DialogData
import com.mei.wood.dialog.NormalDialogLauncher
import com.mei.wood.ext.AmanLink
import com.mei.wood.extensions.subscribeBy
import com.mei.wood.util.CalendarUtil
import com.mei.wood.util.MeiUtil
import com.mei.wood.util.parseAllValues
import com.mei.wood.util.parseValue
import com.net.MeiUser
import com.net.model.broadcast.PlayType
import com.pili.getPlayInfo
import com.pili.player
import kotlinx.android.synthetic.main.mei_web_layout.*
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2018/8/30
 */

/**
 * 通过[MeiWebData] 处理相关UI状态
 */
fun MeiWebActivity.initView() {
//    middle_line.isGone = webData.hide_nav_bottomline == 1 || webData.appear_top_bar == 0
    top_layout.isVisible = webData.appear_top_bar == 1
    web_title.isVisible = webData.show_title == 1
    web_refresh.isVisible = webData.show_refresh_button == 1
    mei_refresh.isEnabled = webData.can_refresh == 1
    mei_refresh.setOnRefreshListener {
        if (refreshMethod.isNotEmpty()) {
            mei_web.loadUrl("javascript:${refreshMethod}()")
            mei_refresh.isRefreshing = false
        } else {
            mei_web.reload()
        }
    }

    when (webData.transparent_status_bar) {
        1 -> {
            //只透明，不更改状态栏字体主题
            transparentStatusBar()
        }
        2 -> {
            //透明，状态栏字体颜色更改成深色
            transparentStatusBar()
            lightMode()
        }
    }

    web_title.text = webData.title
    reload_btn.setOnClickListener { mei_web.reload() }
    web_back.setOnClickListener { onBackPressed() }
    web_refresh.setOnClickNotDoubleListener {
        mei_web.scrollTo(0, 0)
        mei_web.reload()
    }
    web_close.setOnClickListener { finish() }

    bindAction<String>(WEBVIEW_MESSAGE_CHANGE, callBack = { message ->
        message?.let {
            if (message.isNotEmpty() && !isFinishing) mei_web.loadUrl("javascript:$message")
        }
    })

}

/**
 * 初始化WebView相关数据
 */
@SuppressLint("SetJavaScriptEnabled")
@Suppress("DEPRECATION")
fun MeiWebActivity.initWeb() {
    mei_web.initWebViewSetting()
    WebView.setWebContentsDebuggingEnabled(true)
    mei_web.settings.apply {
        javaScriptCanOpenWindowsAutomatically = true
        useWideViewPort = true
        loadWithOverviewMode = true
        domStorageEnabled = true
        cacheMode = WebSettings.LOAD_NO_CACHE
        mediaPlaybackRequiresUserGesture = false
    }
    CookieSyncManager.createInstance(this)
    val manager = CookieManager.getInstance()
    manager.setAcceptCookie(true)
    manager.removeSessionCookie()
    webData.cookies.orEmpty().forEach {
        manager.setCookie(webData.url, it)
    }
    CookieSyncManager.getInstance().sync()

    mei_web.webChromeClient = openFileClient
    mei_web.webViewClient = meiWebClient
    mei_web.loadUrl(MeiUtil.appendGeneraUrl(webData.url))
    if (webData.need_reload_web == 1) {
        bindAction<Boolean>(WEB_REFRESH, callBack = { obj ->
            if (obj != null && obj) mei_web.reload()
        })
    }

    bindAction<PaySuccess>(PAY_SUCCESS, callBack = {
        //支付成功弹窗不刷新
        if (!mei_web.url.contains("/chat-artifact/index-v1.html#/payment")) {
            mei_web.reload()
        }

    })
    bindAction<Any>(CLOSE_LOADING) {
        mei_web.loadUrl("javascript:hideLoading()")
    }
    bindAction<Int>(SHARE_WEB_GIO) {
        mei_web.loadUrl("javascript:shareFromWebGio(${it})")
    }
}

/**
 * 处理相关内链
 */
@SuppressLint("CheckResult")
fun MeiWebActivity.handleLink(url: String): Boolean {
    if (url.matches(AmanLink.URL.USER_LOGIN.toRegex()) || url.matches(AmanLink.URL.USER_LOGIN_NEW.toRegex())) {
        /** 登录提示 **/
        checkLogin {
            if (it) {
                mei_web.loadUrl(MeiUtil.appendGeneraUrl(webData.url))
                clearHistory = true
            }
        }

    } else if (url.matches(AmanLink.URL.webview_message_channel.toRegex())) {
        postAction(WEBVIEW_MESSAGE_CHANGE, MeiUtil.getOneID(url, AmanLink.URL.webview_message_channel))
    } else if (url.matches(AmanLink.URL.close_webview.toRegex())) {
//        postAction(WEB_REFRESH, true)
        mei_web.loadUrl("about:blank")
        finish()
    } else if (url.matches(AmanLink.URL.back_show_alert.toRegex())) {
        val data = MeiUtil.getJsonObject<ConfirmDialogData>(url, AmanLink.URL.back_show_alert)
        data?.let {
            NormalDialogLauncher.newInstance().showDialog(this,
                    DialogData(it.title.orEmpty(), it.cancel_text.orEmpty().ifEmpty { "取消" }, it.confirm_text.orEmpty().ifEmpty { "确认" }),
                    okBack = { finish() })
        }
    } else if (url.matches(AmanLink.URL.web_current_index.toRegex())) {
        index = MeiUtil.getOneID(url, AmanLink.URL.web_current_index).getInt()
    } else if (url.matches(AmanLink.URL.has_finished_emotion_test.toRegex())) {
        postAction(EMOTION_TEST_SUCCESS, true)
    } else if (url.matches(AmanLink.URL.WEBVIEW_EDIT_CONTENT.toRegex())) {
        //  是否有编辑内容，
        webHasChange = TextUtils.equals("1", MeiUtil.getOneID(url, AmanLink.URL.WEBVIEW_EDIT_CONTENT))
    } else if (url.matches(AmanLink.URL.WEBVIEW_SHOW_TOOLBAR_REFRESH_ICON.toRegex())) {
        // 是否显示刷新按钮
        web_refresh.isVisible = TextUtils.equals("1", MeiUtil.getOneID(url, AmanLink.URL.WEBVIEW_SHOW_TOOLBAR_REFRESH_ICON))
    } else if (url.matches(AmanLink.URL.WEB_CALL_APP_PAY_SDK.toRegex())) {
        // 网页调起本地支付SDK
        thirdPartPaymentV2(url, object : PayCallBack {
            override fun onSuccess(success: PaySuccess) {
                mei_web.loadUrl("javascript:paySuccess()")
                postAction(PAY_SUCCESS, success)
                finish()
            }

            override fun onFailure(failure: PayFailure) {
                gotoFailResult(failure)
            }
        })
    } else if (url.matches(AmanLink.URL.WEB_CALL_APP_PAY_SDK_V2.toRegex())) {
        //新版网页调起本地支付SDK
        thirdPartPaymentV2(url, object : PayCallBack {
            override fun onSuccess(success: PaySuccess) {
                postAction(PAY_SUCCESS, success)
                mei_web.loadUrl("javascript:paySuccessV2()")
            }

            override fun onFailure(failure: PayFailure) {
                gotoFailResultV2(failure)
            }
        })
    } else if (url.matches(AmanLink.URL.COPY_WORDS.toRegex())) {
        val content = MeiUtil.getOneID(url, AmanLink.URL.COPY_WORDS)
        content.isNotEmpty().selectByTrue {
            BottomSelectFragment().addItem("复制").setItemClickListener(Callback {
                copyToClipboard(content)
                        .selectByTrue { UIToast.toast(this, "复制成功") }
            }).show(supportFragmentManager)
        }
    } else if (url.matches(AmanLink.URL.EXIT_WEBVIEW.toRegex())) {
        val content = MeiUtil.getOneID(url, AmanLink.URL.EXIT_WEBVIEW)
        content.isNotEmpty().selectByTrue { UIToast.toast(this, content) }
        finish()
    } else if (url.matches(AmanLink.URL.CANCELFOLLOWMENTOR_FROMWEB.toRegex())) {
        postAction(CANCEL_FOLLOW, MeiUtil.getOneID(url, AmanLink.URL.CANCELFOLLOWMENTOR_FROMWEB).getInt())
    } else if (url.matches(AmanLink.URL.FOLLOWMENTOR_FROMWEB.toRegex())) {
        postAction(FOLLOW_NEW_MENTOR, true)
    } else if (url.matches(AmanLink.URL.APP_GET_WEB_TITLE.toRegex()) || url.matches(AmanLink.URL.CHANGE_NAV_TITLE.toRegex())) {
        val content = MeiUtil.getOneID(url, AmanLink.URL.APP_GET_WEB_TITLE, AmanLink.URL.CHANGE_NAV_TITLE)
        if (content.isNotEmpty()) {
            webData.title = content
            web_title.text = content
            web_title.isVisible = true
        }
    } else if (url.matches(AmanLink.URL.HIDE_TITLE_LINE.toRegex())) {
//        middle_line.isVisible = !TextUtils.equals("1", MeiUtil.getOneID(url, AmanLink.URL.HIDE_TITLE_LINE))
    } else if (url.matches(AmanLink.URL.CREATE_NEW_WEB.toRegex())) {
        try {
            val data = MeiUtil.getOneID(url, AmanLink.URL.CREATE_NEW_WEB)
            val json = JSONObject(data)
            val needLogin = json.getIntValue("need_login")
            val loadUrl = json.getStringValue("url")
            if (needLogin == 1 && JohnUser.getSharedUser().userID <= 0) {
                checkLogin { if (it) mei_web.loadUrl(MeiUtil.appendGeneraUrl(webData.url)) }
            } else if (!TextUtils.isEmpty(loadUrl)) {
                Nav.toAmanLink(activity, loadUrl)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    } else if (url.matches(AmanLink.URL.show_toast.toRegex())) {
        UIToast.toast(this, MeiUtil.getOneID(url, AmanLink.URL.show_toast))
    } else if (url.matches(AmanLink.URL.show_loading.toRegex())) {
        showLoading(TextUtils.equals("1", MeiUtil.getOneID(url, AmanLink.URL.show_loading)))
    } else if (url.matches(AmanLink.URL.save_base64_image.toRegex())) {
        //js方法：javascript:onAppSaveImgCallback($status) status: 1： 成功，0：失败
        if (!isDestroyed && !isFinishing) {
            this.requestSinglePermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) {
                if (!isDestroyed && !isFinishing) {
                    if (it.first) {
                        this.saveImgToGallery(MeiUtil.getOneID(url, AmanLink.URL.save_base64_image)) {
                            if (it?.exists() == true) {
                                mei_web.loadUrl("javascript:typeof onAppSaveImgCallback == 'function' && onAppSaveImgCallback(1)")
                            } else {
                                mei_web.loadUrl("javascript:typeof onAppSaveImgCallback == 'function' && onAppSaveImgCallback(0)")
                            }
                        }
                    } else {
                        mei_web.loadUrl("javascript:typeof onAppSaveImgCallback == 'function' && onAppSaveImgCallback(0)")
                        UIToast.toast(activity, "保存图片需要您提供写存储权限")
                    }
                }
            }
        }
    } else if (url.startsWith(AmanLink.URL.USER_CHAT_SEND_MESSAGE)) {
        //发送自定义消息

        val values = url.parseAllValues()
        val data: String? = values["custom_message"]
        if (data?.isNotEmpty() == true) {
            val result = data.json2Obj<ChickCustomData.Result>()
            if (result != null) {
                var publishUserId = 0
                result.data?.serviceInfo?.apply {
                    publishUserId = publisherId
                }
                result.data?.courseInfo?.apply {
                    publishUserId = userId
                }
                val roomId = result.data?.roomId
                if (roomId?.isNotEmpty() == true) {
                    //直播中
                    VideoLiveRoomActivityLauncher.startActivity(activity, roomId, LiveEnterType.course_introduce_page, data)
                } else {
                    toImChat(publishUserId.toString(), false, result)
                }
            }
        }
    } else if (url.matches(AmanLink.URL.USER_CHAT_PATTERN.toRegex())) {
        //网页跳转到私聊
        val chatId = MeiUtil.getOneID(url, AmanLink.URL.USER_CHAT_PATTERN)
        toImChat(chatId, false)

    } else if (url.startsWith("tel:")) {
        //紫茵说没有私信客服这个需求了 直接让拨打电话就行
//        showPhoneDialog(url.substring("tel:".length))
        val phoneNum = url.substring("tel:".length)
        ifUserConfirm(getString(R.string.call_phone_confirm, phoneNum), phoneNum)
                .compose(this.bindToLifecycle())
                .subscribeBy { doAddContacts(it) }

    } else if (url.matches(AmanLink.URL.jump_to_wechat.toRegex())) {
        TdSdk.openWeixin(this)
    } else if (url.startsWith(AmanLink.URL.is_show_system_invite_join)) {
        val isShow = url.parseValue("isShow")
        isSystemInviteJoinShow = isShow.toInt() == 1

    } else if (url.startsWith(AmanLink.URL.jump_home)) {
        //跳转至首页
        postAction(SELECT_HOME, true)
        Nav.toMain(this)
    } else if (url.startsWith(AmanLink.URL.login_out) || url.startsWith(AmanLink.URL.account_over) || url.startsWith(AmanLink.URL.account_back)) {
        Nav.logout(this, false)
        postAction(CHANG_LOGIN, false)
    } else if (url.startsWith(AmanLink.URL.account_out_ok)) {
        Nav.clearInfo()
        postAction(CHANG_LOGIN, false)
    } else if (url.startsWith(AmanLink.URL.report_user)) {
        val userId = url.parseValue("userId")
        showReportDialog(userId.toInt(), "")
    } else if (url.matches(AmanLink.URL.set_refresh_callback.toRegex())) {
        refreshMethod = MeiUtil.getOneID(url, AmanLink.URL.set_refresh_callback)
    } else if (url.startsWith(AmanLink.URL.player_controller)) {
        val state = url.parseValue("state", -100)
        isShowPlayerBar = url.parseValue("showBar", 1) == 1
        playerFloatBar.refreshView()
        if (getPlayInfo().mPlayType != PlayType.NONE)
            when (state) {
                0 -> player.pause()
                1 -> player.resume()
                -1 -> player.stop()
            }
    } else if (url.startsWith(AmanLink.URL.apply_service)) {
        val publisherId = url.parseValue("publisherId").getInt(0)
        val specialServiceOrderId = url.parseValue("specialServiceOrderId")
        val from = url.parseValue("from")
        startPrivateUpstream(publisherId, specialServiceOrderId, from)
    } else if (url.startsWith(AmanLink.URL.coupon_dialog)) {
        val couponId = url.parseValue("couponId").getInt(0)
        val couponNum = url.parseValue("couponNum")
        val publisherId = url.parseValue("publisherId").getInt(0)
        showCouponDialog(couponId, couponNum, publisherId)
    } else if (url.startsWith(AmanLink.URL.snap_up_coupon)) {
        val success = url.parseValue("success").getInt(-1)
        if (success != -1) {
            val couponNum = url.parseValue("couponNum").getLong(-1)
            showSnapUpSuccessDialog(success, couponNum)
            return true
        }
        val remindTime = url.parseValue("remindTime")
        val couponId = url.parseValue("couponId").getInt(0)
        val bookDate = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).parse(remindTime)
        if (PermissionCheck.hasPermission(activity, Manifest.permission.READ_CALENDAR, Manifest.permission.WRITE_CALENDAR)
                && CalendarUtil.isHasAppointment(activity, couponId)) {
            val previousTime = CalendarUtil.getBeginTime(this, couponId)
            if (previousTime == bookDate?.time) {
                UIToast.toast("已经预约")
            } else {
                CalendarUtil.deleteCalendarEvent(this, couponId)
                bookFreeCouponTime(bookDate, remindTime, couponId)
            }
        } else {
            requestMulPermissionZip(Manifest.permission.READ_CALENDAR, Manifest.permission.WRITE_CALENDAR) {
                if (it) {
                    //日历添加事件
                    bookFreeCouponTime(bookDate, remindTime, couponId)
                }
            }
        }
        mei_web.loadUrl("javascript:remindSuccess(${couponId})")
    } else if (url.startsWith(AmanLink.URL.snap_up_consult)) {
        val couponNum = url.parseValue("couponNum").getLong(0)
        val userStatus = url.parseValue("userStatus").getInt(0)
        showConsultDirectionDialog(couponNum, userStatus)
    } else if (url.startsWith(AmanLink.URL.LOAD_FINISH)) {
        //webView加载完数据会通知
        setStatusBarHeight(px2dip(getStatusBarHeight()).toInt())
    } else if (MelkorWebViewClient.isAppLink(url)) {
        if (url.matches(AmanLink.URL.COURSE_SPEC_ORDER.toRegex())) {
            needRefresh = true
        }
        Nav.toAmanLink(this, url)
    } else if (URLUtil.isNetworkUrl(url)) {
        return false
    }
    return true
}

private fun MeiWebActivity.bookFreeCouponTime(bookDate: Date?, remindTime: String, couponId: Int) {
    if (bookDate == null) {
        return
    }
    val endTime = bookDate.time
    CalendarUtil.addCalendarEvent(this, "知心免费咨询券开抢啦！！！", "${remindTime}开始抢券", couponId, MeiUser.getSharedUser().info?.userId
            ?: 0, endTime, endTime)
    UIToast.toast(this, "预约成功！开始时为你发送提醒")
}

/**
 * 网页支付失败处理
 */
fun MeiWebActivity.gotoFailResult(failure: PayFailure?) {
    if (failure != null) {
        when (failure.err_code) {
            PayErrorCode.Pay_Not_reback_Info_code -> UIToast.toast(this, failure.err_reason)
            PayErrorCode.Pay_User_Cancel_code -> UIToast.toast(this, getString(R.string.pay_cancel))
            PayErrorCode.pay_failure_code -> mei_web.loadUrl("javascript:payFailure()")

            else -> {
            }
        }
    }
}

/**
 * 网页支付失败处理
 */
fun Activity.gotoFailResultV2(failure: PayFailure?) {
    if (failure != null && !isDestroyed) {
        when (failure.err_code) {
            PayErrorCode.Pay_Not_reback_Info_code -> UIToast.toast(this, failure.err_reason)
            PayErrorCode.Pay_User_Cancel_code -> {
                UIToast.toast(this, getString(R.string.pay_cancel))
                mei_web.loadUrl("javascript:payCancelV2()")
            }
            PayErrorCode.pay_failure_code -> mei_web.loadUrl("javascript:payFailureV2()")

            else -> {
                UIToast.toast(this, failure.err_msg)
                mei_web.loadUrl("javascript:payFailureV2()")
            }
        }
    }
}


