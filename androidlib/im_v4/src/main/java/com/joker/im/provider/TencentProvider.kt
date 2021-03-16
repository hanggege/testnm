package com.joker.im.provider

import android.annotation.SuppressLint
import android.content.ContentProvider
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.net.Uri
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import com.joker.im.BuildConfig
import com.joker.im.Message
import com.joker.im.listener.IMNotifyLevelInterface
import com.tencent.imsdk.TIMCallBack
import com.tencent.imsdk.TIMLogLevel
import com.tencent.imsdk.TIMManager
import com.tencent.imsdk.TIMSdkConfig
import com.tencent.imsdk.session.SessionWrapper


/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2019-06-13
 *
 * 初始化IM，无需在Application中注册
 */

@SuppressLint("StaticFieldLeak")
var imContext: Context? = null

class TencentProvider : ContentProvider() {

    override fun onCreate(): Boolean {
        context?.apply {
            initIm(this)
        }
        ProcessLifecycleOwner.get().lifecycle.addObserver(TencentActivityLifecycle())
        imContext = context
        return true
    }

    override fun insert(uri: Uri, p1: ContentValues?): Uri? = null

    override fun query(uri: Uri, p1: Array<out String>?, p2: String?, p3: Array<out String>?, p4: String?): Cursor? =
            null

    override fun update(uri: Uri, p1: ContentValues?, p2: String?, p3: Array<out String>?): Int = 0

    override fun delete(uri: Uri, p1: String?, p2: Array<out String>?): Int = 0

    override fun getType(uri: Uri): String? = ""

    private fun initIm(ctx: Context) {
        if (SessionWrapper.isMainProcess(ctx)) {
            initConfig(ctx)
        }
    }

    private fun initConfig(ctx: Context) {
        val config: TIMSdkConfig = TIMSdkConfig(BuildConfig.IM_APP_ID).apply {
            logLevel = TIMLogLevel.WARN
            enableLogPrint(true)
        }
        TIMManager.getInstance().init(ctx, config)
    }

}


/** 是否是在前台 **/
var imIsForeground = false

/**登录跳转到首页时对累积的信息进行缓存**/
var cacheMsg: ArrayList<Message> = arrayListOf()

/** 是否需要忽略通知新信息 **/
fun needIgnoreNotify(notifyID: String = ""): Boolean {
    val notifyInterface = notify
    return if (notifyInterface != null) notifyInterface.isAllIgnore()
            || notifyInterface.IgnoreByID() == notifyID
    else false
}

private var notify: IMNotifyLevelInterface? = null

private class TencentActivityLifecycle : LifecycleEventObserver {
    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        if (event == Lifecycle.Event.ON_STOP) {
            imIsForeground = false
            /** 应用切到前台 **/
            TIMManager.getInstance().doForeground(object : TIMCallBack {
                override fun onSuccess() {

                }

                override fun onError(p0: Int, p1: String?) {
                }

            })
        } else if (event == Lifecycle.Event.ON_START) {
            imIsForeground = true
            /** 应用切到后台 **/
//            TIMManager.getInstance().doBackground(TIMBackgroundParam().apply { c2cUnread = 100 },object : TIMCallBack {
//                override fun onSuccess() {
//
//                }
//
//                override fun onError(p0: Int, p1: String?) {
//                }
//
//            })
        }
    }


}