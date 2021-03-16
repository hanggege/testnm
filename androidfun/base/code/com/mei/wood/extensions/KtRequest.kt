package com.mei.wood.extensions

import android.app.Activity
import com.mei.orc.Cxt
import com.mei.orc.http.exception.CanceledException
import com.mei.orc.http.exception.RxThrowable
import com.mei.orc.http.listener.RequestListener
import com.mei.orc.http.listener.UiTaskListener
import com.mei.orc.http.response.DataResponse
import com.mei.orc.http.retrofit.RetrofitClient
import com.mei.orc.http.retrofit.RxRequest
import com.mei.orc.ui.toast.UIToast
import com.mei.wood.needToast

/**
 * 佛祖保佑         永无BUG
 *
 * Created by joker on 2017/2/7.  请求辅助类
 */


/**
 * 处理错误信息提示
 */
fun <RESULT : DataResponse, R> RetrofitClient.executeToastKt(request: RxRequest<RESULT, R>,
                                                             success: (RESULT) -> Unit = {},
                                                             failure: (RxThrowable?) -> Unit = {},
                                                             finish: () -> Unit = {}) {
    executeKt(request, success = {
        if (!it.isSuccess && it.rtn.needToast()) {
            UIToast.toast(Cxt.get(), it.errMsg)
        }
        success(it)
    }, failure = {
        failure(it)
        if (it is CanceledException) {

        } else {
            UIToast.toast(Cxt.get(), it?.currMessage ?: "网络出错！")
        }
    }, finish = finish)

}

fun <RESULT : DataResponse, R> RetrofitClient.executeKt(request: RxRequest<RESULT, R>,
                                                        success: (RESULT) -> Unit = {},
                                                        failure: (RxThrowable?) -> Unit = {},
                                                        finish: () -> Unit = {}) {
    this.execute(request, object : RequestListener<RESULT> {
        override fun onRequestFailure(retrofitThrowable: RxThrowable?) {
            try {
                finish()
                failure(retrofitThrowable)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        override fun onRequestSuccess(result: RESULT) {
            try {
                finish()
                success(result)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    })
}


fun <RESULT : DataResponse, R> RetrofitClient.executeKt(activity: Activity?,
                                                        request: RxRequest<RESULT, R>,
                                                        success: (RESULT) -> Unit = {},
                                                        finish: () -> Unit = {},
                                                        failure: (RESULT?) -> Unit = {}) {
    this.execute(request, object : UiTaskListener<RESULT>(activity) {
        override fun onResponseCorrect(result: RESULT) {
            try {
                success(result)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        override fun onRequestFinished() {
            super.onRequestFinished()
            try {
                finish()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        override fun onRequestFail(e: RxThrowable?) {
            super.onRequestFail(e)
            try {
                failure(null)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        override fun onResponseError(result: RESULT) {
            super.onResponseError(result)
            try {
                failure(result)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

    })
}




