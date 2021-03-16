package com.mei.im.presenters

import android.util.Log
import com.joker.im.imIsLogin
import com.joker.im.imLoginId
import com.joker.im.provider.IMLoginProvider
import com.joker.im.provider.IMLoginView
import com.mei.init.spiceHolder
import com.mei.orc.john.model.JohnUser
import com.mei.orc.util.save.getStringMMKV
import com.mei.orc.util.save.putMMKV
import com.mei.orc.util.save.removeValuesForKeys
import com.mei.wood.extensions.executeKt
import com.net.network.chick.tim.GetTimSignRequest

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2019-06-05
 */


/**
 * 获取IM的sig用来登录
 */
fun imSigAccountInfo() = "account_info_sig${JohnUser.getSharedUser().userID}"

class IMLoginBusiness(loginView: IMLoginView? = null) : IMLoginProvider(loginView) {

    fun loginAppIM() {
        if (!imIsLogin() && imLoginId() != JohnUser.getSharedUser().userIDAsString && JohnUser.getSharedUser().hasLogin()) {
            loginIM(JohnUser.getSharedUser().userIDAsString)
        } else if (imIsLogin()) {
            loginView?.loginSuccess()
        } else {
            Log.e("info", "xxx:${imIsLogin()} ");
        }
    }

    override fun removeSigTryLogin() {
        removeValuesForKeys(imSigAccountInfo())
        loginAppIM()
    }

    override fun getIMSig(succ: (String) -> Unit) {
        val localSig = imSigAccountInfo().getStringMMKV()
        if (localSig.isNotEmpty()) {
            succ(localSig)
        } else {
            spiceHolder().apiSpiceMgr.executeKt(GetTimSignRequest(JohnUser.getSharedUser().userID), success = {
                val sig = it.data?.sig ?: "登录失败"
                if (it.isSuccess && sig.isNotEmpty()) {
                    imSigAccountInfo().putMMKV(sig)
                    succ(sig)
                } else {
                    loginView?.loginFailure(it.rtn, it.errMsg)
                }
            }, failure = {
                loginView?.loginFailure(it?.code ?: -1, it?.currMessage ?: "登录失败")
            })
        }
    }

}


