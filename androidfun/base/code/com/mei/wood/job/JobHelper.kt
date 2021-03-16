package com.mei.wood.job

import android.app.Activity
import com.mei.orc.util.app.setEnabledBlocking
import com.mei.wood.BuildConfig

/**
 * 佛祖保佑 永无BUG
 *
 * @author Created by joker on 2017/8/14.
 * modify by Ling on 2019-07-23
 */


/**
 * 测试入口触发
 */
fun checkTestEnvironment(activity: Activity) {
    try {
        if (BuildConfig.IS_TEST) {
            setEnabledBlocking(activity, true)
        } else {
            setEnabledBlocking(activity, false)
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }

}