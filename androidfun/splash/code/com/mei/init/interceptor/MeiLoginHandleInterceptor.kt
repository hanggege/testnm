package com.mei.init.interceptor

import android.content.Intent
import com.mei.orc.Cxt
import com.mei.orc.common.CommonConstant
import com.mei.orc.http.interceptor.MeiInterceptor
import okhttp3.HttpUrl

/**
 * Created by guoyufeng on 29/5/15.
 */
class MeiLoginHandleInterceptor : MeiInterceptor {


    override fun handleResult(url: HttpUrl, rtn: Int, result: String) {
        when (rtn) {
            CommonConstant.Config.BASE_CODE_NO_SESSION,
            CommonConstant.Config.BASE_CODE_T_SESSION,
            CommonConstant.Config.BASE_CODE_SESSION_PAST,
            CommonConstant.Config.BASE_CODE_SESSION_EXPIREED,
            CommonConstant.Config.BASE_CODE_BAD_SESSION -> {
                val intent = Intent(CommonConstant.Action.SESSION_EXPIRED())
                intent.putExtra(CommonConstant.DataKey.session_rtn_code, rtn)
                intent.putExtra(CommonConstant.DataKey.session_rtn_json, result)
                Cxt.get().sendBroadcast(intent)
            }
        }
    }

}
