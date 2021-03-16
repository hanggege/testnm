package com.mei.login.ext

import android.content.Context
import com.joker.model.BackResult
import com.mei.base.util.td.parseState
import com.mei.init.spiceHolder
import com.mei.orc.common.CommonConstant
import com.mei.orc.http.retrofit.RetrofitClient
import com.mei.orc.john.model.Oauth_Connect
import com.mei.orc.john.network.JohnClient
import com.mei.wood.extensions.executeKt
import com.net.model.chick.login.LoginWithOauthResult
import com.net.network.chick.login.LoginWithOauthRequest
import com.net.network.chick.login.OauthConnectRequest

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2019/2/19
 */


/**
 * 第三方登录完成后 ,这里与家超沟通过，第三方登录是不会触发图形验证码的
 * 第一步： 查询是否已经注册，已经注册则直接返回用户信息，登录完成 ；由于和小鹿用户系统混在一起，所以这里通过这个查询昵称id等，然后每次都进行第二步
 * 第二步： 如果未注册则进行注册，返回用户信息，登录完成
 *
 * [RetrofitClient] 需要的是[JohnClient]
 */
fun Context.queryRegistered(
        result: BackResult,
        success: (LoginWithOauthResult?) -> Unit = {},
        failure: (code: Int, String) -> Unit = { _, _ -> }
) {
    val request = OauthConnectRequest(result.type.parseState(), result.code)
    spiceHolder().apiSpiceMgr.executeKt(request, success = {
        val data = it.data
        if (it?.rtn == CommonConstant.Config.LOGIN_BASE_CODE_BAD_SESSION) {
            failure(it.rtn, it.errMsg.orEmpty())
            this.showForbiddenDialog(it.errMsg)
        } else if (data != null && it?.rtn == 20201) {
            /** 第三方自动注册 **/
            registerNewUser(data, success, failure)
        } else if (data != null) {
            /** 登录完成 **/
            success(LoginWithOauthResult().apply {
                login_user_id = data.login_user_id
                session_id = data.session_id
                login_user_name = data.login_user_name
                ifPerfect = data.ifPerfect
                isReg = data.isReg
            })
        } else {
            /** 登录失败 **/
            failure(it.rtn, it.errMsg.orEmpty())
        }
    }, failure = {
        /** 登录失败 **/
        failure(-1, it?.currMessage.orEmpty())
    })
}

private fun Context.registerNewUser(
        data: Oauth_Connect,
        success: (LoginWithOauthResult?) -> Unit = { },
        failure: (code: Int, String) -> Unit
) {
    val request = LoginWithOauthRequest(data.user_name, data.avatar, data.seq_id)
    spiceHolder().apiSpiceMgr.executeKt(request, success = { resp ->
        val info = resp?.data
        if (info != null && resp.isSuccess) {
            info.isReg = 1
            success(info)
        } else {
            /** 注册失败 **/
            failure(resp.rtn, resp?.errMsg.orEmpty())
        }
    }, failure = {
        /** 注册失败 **/
        failure(-1, it?.currMessage.orEmpty())
    })
}