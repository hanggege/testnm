package com.mei.im.ext

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.util.Log
import com.joker.im.Message
import com.joker.im.bindEventLifecycle
import com.joker.im.listener.IMAllEventManager
import com.joker.im.mapToMeiMessage
import com.joker.im.provider.IMLoginView
import com.joker.im.registered
import com.mei.im.presenters.IMLoginBusiness
import com.mei.im.resetNotifyById
import com.mei.init.spiceHolder
import com.mei.orc.john.model.JohnUser
import com.mei.wood.extensions.executeKt
import com.net.network.chick.login.AccountIsLoginRequest
import com.tencent.imsdk.TIMMessage

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2019-06-20
 */

internal class IMInitExt {
    fun registerIMEvent(any: Any?) {
        if (any is Application) {
            //这里必须调用，这样的话才能添加监听事件到事件管理集合
            resetNotifyById("")

            //这个目前是常驻的监听，没有移除
            checkIMForceOffline(any) {
                //                spiceHolder().getApiSpiceMgr().executeKt(Account_is_login_Request())
                spiceHolder().apiSpiceMgr.executeKt(AccountIsLoginRequest())
            }.registered()

            any.registerActivityLifecycleCallbacks(object : Application.ActivityLifecycleCallbacks {
                override fun onActivityPaused(activity: Activity) {

                }

                override fun onActivityStarted(activity: Activity) {
                }

                override fun onActivityDestroyed(activity: Activity) {
                }

                override fun onActivitySaveInstanceState(activity: Activity, p1: Bundle) {
                }

                override fun onActivityStopped(activity: Activity) {
                }

                override fun onActivityCreated(activity: Activity, p1: Bundle?) {
                }

                override fun onActivityResumed(activity: Activity) {
                    if (JohnUser.getSharedUser().hasLogin()) {
                        loginIM()
                    }
                }

            })

        }
    }

    /**
     * im刷新消息检测
     */
    fun checkIMRefresh(any: Any?, back: () -> Unit): IMAllEventManager =
            object : IMAllEventManager() {
                /**
                 * 数据刷新通知回调（如未读计数，会话列表等）
                 */
                override fun onRefresh() {
                    super.onRefresh()
                    back()
                }

                override fun onUserSigExpired() {
                    super.onUserSigExpired()
                    back()
                }

                override fun onForceOffline() {
                    super.onForceOffline()
                    back()
                }
            }.apply {
                bindEventLifecycle(any)
            }

    /**
     * im新消息接收
     */
    fun checkIMNewMessage(any: Any?, back: (List<Message>) -> Unit): IMAllEventManager =
            object : IMAllEventManager() {
                override fun onNewMessages(msgs: MutableList<TIMMessage>?): Boolean {
                    back(msgs.orEmpty().mapNotNull { it.mapToMeiMessage() })
                    return super.onNewMessages(msgs)
                }
            }.apply {
                bindEventLifecycle(any)
            }


    /**
     * 查检IM被踢回调
     */
    fun checkIMForceOffline(any: Any?, back: () -> Unit): IMAllEventManager =
            object : IMAllEventManager() {
                override fun onForceOffline() {
                    super.onForceOffline()
                    back()
                }
            }.apply {
                bindEventLifecycle(any)
            }


    /**
     * 快速登录IM
     */
    fun loginIM(success: () -> Unit = {}, failure: ((Int, String) -> Unit)? = null) {
        IMLoginBusiness(object : IMLoginView {
            override fun loginSuccess() {
                success()
            }

            override fun loginFailure(code: Int, msg: String) {
                if (failure != null) failure(code, msg)
//                else UIToast.toast(Cxt.get(), "登录失败：$code $msg")
                else Log.e("IMInitExt", "loginImFailure $code $msg")
            }

            override fun knackOut() {
            }

            override fun onConnected() {
            }

            override fun onDisconnected() {
            }

        }).loginAppIM()
    }
}



