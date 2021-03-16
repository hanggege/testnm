package com.joker.im.provider

import com.joker.im.bindEventLifecycle
import com.joker.im.ext.imAllEvent
import com.joker.im.imIsLogin
import com.joker.im.imLoginId
import com.joker.im.imLogout
import com.joker.im.listener.IMAllEventManager
import com.tencent.imsdk.TIMCallBack
import com.tencent.imsdk.TIMManager

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2019-06-17
 */

interface IMLoginView {
    fun loginSuccess()
    fun loginFailure(code: Int, msg: String)
    fun knackOut()

    /** 连接上 **/
    fun onConnected()

    /** 断开连接 **/
    fun onDisconnected()
}

abstract class IMLoginProvider(val loginView: IMLoginView? = null) : IMAllEventManager() {

    init {
        bindEventLifecycle(loginView)
    }

    fun loginIM(id: String) {
        getIMSig { sig ->
            if (imIsLogin() && id != imLoginId()) {
                imLogout(success = {
                    timLogin(id, sig)
                }, failure = { _, _ ->
                    timLogin(id, sig)
                })
            } else {
                timLogin(id, sig)
            }
        }

    }

    private fun timLogin(id: String, sig: String) {
        TIMManager.getInstance().login(id, sig, object : TIMCallBack {
            override fun onSuccess() {
                loginView?.loginSuccess()
                imAllEvent.onLoginSuccess()
            }

            override fun onError(code: Int, msg: String?) {
                if ((code in 70001..70016)
                        || code == 70050
                        || code == 70052
                        || code == 70169
                        || code == 6206
                        || code == 6207
                        || code == 6208
                ) {
                    removeSigTryLogin()
                } else if (code == 6023) {
                    // 在登录操作没有完成前进行了登出操作（或者被踢下线）
                    loginView?.knackOut()

                } else if (code == 7501) {
                    //登录正在执行中，例如，第一次 login 或 autoLogin 操作在回调前，后续的 login 或 autoLogin 操作会返回该错误码。

                } else {
                    loginView?.loginFailure(code, msg ?: "")
                }
            }

        })
    }

    abstract fun getIMSig(succ: (String) -> Unit)

    abstract fun removeSigTryLogin()


    override fun onUserSigExpired() {
        super.onUserSigExpired()
        removeSigTryLogin()
    }

    override fun onForceOffline() {
        super.onForceOffline()
        loginView?.knackOut()
    }

    override fun onWifiNeedAuth(name: String?) {
        super.onWifiNeedAuth(name)
    }

    override fun onDisconnected(code: Int, msg: String?) {
        super.onDisconnected(code, msg)
        loginView?.onDisconnected()
    }

    override fun onConnected() {
        super.onConnected()
        loginView?.onConnected()
    }


}