package com.mei.init

import android.app.Application
import android.net.http.HttpResponseCache
import com.didiglobal.booster.instrument.ActivityThreadHooker
import com.didiglobal.booster.instrument.FinalizerWatchdogDaemonKiller
import com.didiglobal.booster.instrument.ShadowWebView
import com.joker.im.provider.registerIMUserProfile
import com.mei.GrowingUtil
import com.mei.dialog.getReportReason
import com.mei.im.push.registeredOfflinePush
import com.mei.im.quickLoginIM
import com.mei.im.registerIMEvent
import com.mei.init.interceptor.MeiLoginHandleInterceptor
import com.mei.orc.Cxt
import com.mei.orc.channel.channelId
import com.mei.orc.ext.dip
import com.mei.orc.ext.runAsync
import com.mei.orc.ext.screenHeight
import com.mei.orc.ext.screenWidth
import com.mei.orc.http.retrofit.ClientHelper
import com.mei.orc.http.retrofit.RetrofitClient
import com.mei.orc.john.model.JohnUser
import com.mei.orc.john.network.JohnClient
import com.mei.orc.util.app.getAppVersion
import com.mei.orc.util.save.getNonValue
import com.mei.orc.util.save.initMMKV
import com.mei.orc.util.save.sp
import com.mei.provider.ProjectExt
import com.mei.push.PushHandle
import com.mei.shanyan.initSY
import com.mei.splash.ui.ExternalLifeCycle
import com.mei.wood.AppLifeManager
import com.mei.wood.BuildConfig
import com.mei.wood.TimingRunnable
import com.mei.wood.cache.IMUserProfileImpl
import com.mei.wood.extensions.executeKt
import com.net.MeiUser
import com.net.model.chick.tab.TabBar
import com.net.model.chick.tab.tabbarConfig
import com.net.model.config.requestConfigLoad
import com.net.network.chick.tab.TabInfoRequest
import com.opensource.svgaplayer.SVGAParser
import com.pili.business.initPili
import java.io.File

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2017/3/28.
 */
/**
 * 所有进程初始化
 */


fun Application.initAll() {
    try {
        JohnUser.getSharedUser().loadPersistent()
        initBoot()
        GrowingUtil.init(this)
        /** 初始化数据 **/
        applicationContext.initMMKV()
        sp.initShared(applicationContext)
        RetrofitClient.setIsTestClient(BuildConfig.IS_TEST && "retrofit_url_is_test".getNonValue(false))
        RetrofitClient.setIsHttpScheme(BuildConfig.IS_TEST && "current_scheme_is_http".getNonValue(false))
        //interceptor
        ClientHelper.putMeiInterceptor(MeiLoginHandleInterceptor())

        registerIMEvent()
        registeredOfflinePush()
        registerIMUserProfile(IMUserProfileImpl())
        SVGAParser.shareParser().init(this)
        /**Setup HttpResponseCache for SVGA*/
        val cacheDir = File(this.cacheDir, "http")
        HttpResponseCache.install(cacheDir, 1024 * 1024 * 128)
        channelId
        getReportReason()
        if (JohnUser.getSharedUser().hasLogin()) {
            MeiUser.resetUser(spiceHolder().apiSpiceMgr, null)
        }

    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun Application.initBoot() {
    //webkit预加载
    ShadowWebView.preloadWebView(this)
    //ActivityThread 异常hook
    ActivityThreadHooker.hook(null)
    //修复 finalizer 导致的 TimeoutException
    FinalizerWatchdogDaemonKiller.kill()
}

fun Application.registerActivityLifecycle() {
    registerActivityLifecycleCallbacks(AppLifeManager())
    registerActivityLifecycleCallbacks(ExternalLifeCycle())
    registerActivityLifecycleCallbacks(PushHandle())
    initPili()
}

/**
 * app进程初始化
 */
fun Application.initApp() {
    try {
        //前面加载过了
        JohnClient.setUserAgent(ProjectExt.FullUserAgent)
        registerActivityLifecycle()
        //init data
        if (!BuildConfig.IS_TEST) {
            //设置该CrashHandler为程序的默认处理器
            val catchExcep = UnCeHandler(this)
            Thread.setDefaultUncaughtExceptionHandler(catchExcep)
        }
        if (JohnUser.getSharedUser().hasLogin()) {
            MeiUser.resetUser(spiceHolder().apiSpiceMgr, null)
        }
        runAsync {
            requestConfigLoad()
            initSY()
        }
        TimingRunnable(10f) {
            if (JohnUser.getSharedUser().hasLogin()) quickLoginIM()
        }.start(10f)
    } catch (e: Exception) {
        e.printStackTrace()
    }

}

fun requestTabBar(back: (TabBar?) -> Unit = {}) {
    val specifications = "$screenWidth--${screenHeight - dip(120)}"
    spiceHolder().apiSpiceMgr.executeKt(TabInfoRequest(getAppVersion(Cxt.get()), specifications), success = {
        if (it.isSuccess && it.data != null && it.data.tabs.orEmpty().isNotEmpty()) {
            /**tab_info单独保存*/
            it.data?.let { tabbarConfig = it }
            back(it.data)
        } else {
            back(null)
        }
    }, failure = {
        back(null)
    })
}

