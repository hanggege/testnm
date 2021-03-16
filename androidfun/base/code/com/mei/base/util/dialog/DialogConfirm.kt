package com.mei.base.util.dialog

import android.content.Context
import androidx.fragment.app.FragmentActivity
import com.mei.orc.rx.Rx2Observable
import com.mei.wood.R
import com.mei.wood.dialog.DISSMISS_DO_CANCEL
import com.mei.wood.dialog.DISSMISS_DO_OK
import com.mei.wood.dialog.DialogData
import com.mei.wood.dialog.NormalDialogLauncher
import io.reactivex.Observable

/**
 *
 * @author Created by Ling on 2019-07-16
 * 两个按钮的简单确定取消弹窗
 */

fun <T> Context.ifUserConfirm(title: String, message: String,
                              confirm_text: String, cancel_text: String, clickCancle: Boolean,
                              param: T): Observable<T> {
    return Rx2Observable.createNonNull { subscriber ->
        (this as? FragmentActivity)?.let {
            val data = DialogData(message, cancel_text, confirm_text, clickCancle, title)
            NormalDialogLauncher.newInstance().showDialog(it, data, back = { state ->
                if (state == DISSMISS_DO_OK) {
                    subscriber.onNext(param)
                }
                subscriber.onComplete()
            })
        }
    }
}

fun <T> Context.ifUserConfirm(message: String, confirm_str_id: Int, cancel_str_id: Int, param: T): Observable<T> {
    return ifUserConfirm("", message, getString(confirm_str_id), getString(cancel_str_id), true, param)
}

fun <T> Context.ifUserConfirm(message_str_id: Int, confirm_str_id: Int, cancel_str_id: Int, param: T): Observable<T> {
    return ifUserConfirm("", getString(message_str_id), getString(confirm_str_id), getString(cancel_str_id), true, param)
}

fun <T> Context.ifUserConfirm(title_str_id: Int, message_str_id: Int, param: T): Observable<T> {
    return ifUserConfirm(getString(title_str_id), getString(message_str_id), getString(R.string.confirm), getString(R.string.cancel), true, param)
}

fun <T> Context.ifUserConfirm(message_str_id: Int, param: T): Observable<T> {
    return ifUserConfirm("", getString(message_str_id), getString(R.string.confirm), getString(R.string.cancel), true, param)
}

fun <T> Context.ifUserConfirm(message: String, param: T): Observable<T> {
    return ifUserConfirm("", message, getString(R.string.confirm), getString(R.string.cancel), true, param)
}

/**
 * 同时监听回调两个按钮的弹框
 */
fun Context.ifUserConfirmNoFilter(title: String, message: String,
                                  confirm_text: String, cancel_text: String, clickCancle: Boolean): Observable<Boolean> {
    return Rx2Observable.createNonNull { subscriber ->
        (this as? FragmentActivity)?.let {
            val data = DialogData(message, cancel_text, confirm_text, clickCancle, title)
            NormalDialogLauncher.newInstance().showDialog(it, data, back = { state ->
                if (state == DISSMISS_DO_OK) {
                    subscriber.onNext(true)
                } else if (state == DISSMISS_DO_CANCEL) {
                    subscriber.onNext(false)
                }
                subscriber.onComplete()
            })
        }
    }
}