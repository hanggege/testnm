package com.mei.orc.util.app


import android.annotation.SuppressLint
import android.app.ActivityManager
import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager
import android.text.TextUtils
import android.util.Log

/**
 * 多进程处理器
 */
fun getProcessName(context: Context): String? {
    val am = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    val runningApps = am.runningAppProcesses ?: return null
    for (proInfo in runningApps) {
        if (proInfo.pid == android.os.Process.myPid()) {
            if (proInfo.processName != null) {
                return proInfo.processName
            }
        }
    }
    return null
}

/**
 * 获取application中指定的meta-data
 *
 * @return 如果没有获取成功(没有对应值 ， 或者异常)，则返回值为空
 */
fun getAppMetaData(ctx: Context?, key: String): String? {
    if (ctx == null || TextUtils.isEmpty(key)) {
        return null
    }
    var resultData: String? = null
    try {
        val applicationInfo = ctx.packageManager.getApplicationInfo(ctx.packageName, PackageManager.GET_META_DATA)
        if (applicationInfo.metaData != null) {
            resultData = applicationInfo.metaData.getString(key)
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return resultData
}


/**
 * 判断服务是否启动,context上下文对象 ，className服务的name
 */
@Suppress("DEPRECATION")
fun isServiceRunning(mContext: Context, className: String): Boolean {
    var isRunning = false
    try {
        val activityManager = mContext.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val serviceList: List<ActivityManager.RunningServiceInfo> = activityManager.getRunningServices(100)

        if (serviceList.isEmpty()) {
            return false
        }

        for (i in serviceList.indices) {
            if (serviceList[i].service.className == className) {
                isRunning = true
                break
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return isRunning
}


/**
 * 获取APP版本号
 */
fun getAppVersion(context: Context?): String {
    try {
        val info = context?.packageManager?.getPackageInfo(context.packageName, 0)
        return info?.versionName ?: ""
    } catch (e: Exception) {
        e.printStackTrace()
    }

    return ""
}

/**
 * 开始测试入口
 */
@SuppressLint("WrongConstant")
fun setEnabledBlocking(appContext: Context, enabled: Boolean) {
    try {
        val componentClass = Class.forName("com.joker.debug.TestAliasActivity")
        val component = ComponentName(appContext, componentClass)
        val packageManager = appContext.packageManager
        val newState = if (enabled) 1 else 2
        packageManager.setComponentEnabledSetting(component, newState, 1)
    } catch (e: Exception) {
        e.printStackTrace()
        Log.e("AppUtil", "打开测试入口失败")
    }
}

