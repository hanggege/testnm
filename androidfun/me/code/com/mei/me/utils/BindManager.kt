package com.mei.me.utils

import android.app.Activity

import com.joker.TdManager
import com.joker.connect.TdCallBack
import com.joker.flag.TdFlags
import com.joker.model.BackResult
import com.joker.model.ErrResult
import com.mei.orc.http.retrofit.RetrofitClient
import com.mei.wood.dialog.share.td.TdNetAdapterImpl


/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2017/7/5.
 */

class BindManager(private var bindBack: BindBack, private var johnClient: RetrofitClient) : TdCallBack {

    fun performBind(activity: Activity?, @TdFlags type: Int) {
        TdManager.performLogin(activity, type, TdNetAdapterImpl(johnClient), this)
    }

    override fun onSuccess(success: BackResult) {
        bindBack.bindSuccess(success)
    }

    override fun onFailure(errResult: ErrResult) {
        bindBack.bindFailure(errResult)
    }
}
