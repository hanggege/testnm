package com.mei.base.network.netchange

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import com.mei.login.toLogin
import com.mei.orc.Cxt
import com.mei.orc.john.model.JohnUser
import com.mei.orc.net.isNetworkConnected
import com.mei.orc.ui.toast.UIToast
import com.mei.wood.R

/**
 *
 * @author Created by Ling on 2019-07-30
 */

/**
 * 检查网络，无明显提示
 */
fun Context.isOpenNetwork(): Boolean {
    return isNetworkConnected()
}

/**
 * 是否是wifi连接
 */
fun Context.isWIFIConnected(): Boolean {
    val manager = getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        val capabilities = manager?.getNetworkCapabilities(manager.activeNetwork)
        return capabilities?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) == true
    } else {
        @Suppress("DEPRECATION")
        return manager?.activeNetworkInfo?.type == ConnectivityManager.TYPE_WIFI
    }
}

/**
 * 验证网络，异常时弹toast
 */
fun Context?.checkNetworkAvailable(): Boolean {
    val context = this ?: Cxt.get().applicationContext
    if (!context.isOpenNetwork()) {
        UIToast.toast(context, context.getString(R.string.intent_to_network))
        return false
    }
    return true
}

/**
 * 静默检查网络链接正常且已登录，无明显提示
 */
fun Context.checkNetAndHasLogin(): Boolean {
    return isOpenNetwork() && JohnUser.getSharedUser().hasLogin()
}

/**
 * 检查网络及登录，有提示
 * 此方法中，如果当前页面有登录后的回调的话，可以不用处理
 * @param loginSuccess 如果当前页面有登录后的回调的话，可以不用额外处理此回调，可以协助调起登录框而已
 * @param back 当有网且有登录时回调true，否则回调false
 */
fun Context?.checkNetAndLogin(loginSuccess: () -> Unit = {}, back: (Boolean) -> Unit = {}) {
    if (!checkNetworkAvailable()) {
        back(false)
    } else if (!JohnUser.getSharedUser().hasLogin()) {
        back(false)
        toLogin { succ, _ ->
            if (succ) {
                loginSuccess()
            }
        }
    } else {
        back(true)
    }
}