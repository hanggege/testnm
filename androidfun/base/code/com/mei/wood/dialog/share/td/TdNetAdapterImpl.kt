package com.mei.wood.dialog.share.td

import com.joker.support.TdAction
import com.joker.support.listener.TdNetAdapter
import com.mei.orc.callback.Callback
import com.mei.orc.http.retrofit.RetrofitClient
import com.mei.wood.extensions.executeJavaStr

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2017/7/6.
 */

class TdNetAdapterImpl(private val client: RetrofitClient) : TdNetAdapter {

    override fun requestNet(url: String, callBack: TdAction<String>) {
        client.executeJavaStr(url, Callback { callBack.onCallback(it) })
    }
}
