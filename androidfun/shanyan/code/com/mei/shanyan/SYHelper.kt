package com.mei.shanyan

import android.content.Context
import android.util.Log
import com.mei.wood.BuildConfig

/**
 *  Created by zzw on 2020-01-19
 *  Des:
 */


fun shanyanLog(tag: String = "shanyan", msg: String) {
    if (BuildConfig.IS_TEST) {
        Log.e(tag, msg)
    }
}


fun Context.getSYAppId(): String {
    return BuildConfig.SHANYAN_APP_ID
}

fun Context.getSYAppKey(): String {
    return BuildConfig.SHANYAN_APP_KEY
}