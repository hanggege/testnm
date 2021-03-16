package com.mei.splash.ui

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import com.mei.base.ui.nav.Nav
import com.mei.wood.BuildConfig

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2020/3/26
 */

private var externalLink = ""

fun Activity.parseExternalLink(): Boolean {
    val uri = intent.dataString.orEmpty()
    val rex = "${BuildConfig.APP_SCHEME}://${BuildConfig.APPLICATION_ID}/"
    return if (uri.startsWith(rex)) {
        externalLink = uri.replace(rex, "")
        true
    } else false
}

fun Context.handleExternal() {
    if (externalLink.isNotEmpty()) {
        Nav.toAmanLink(this, externalLink)
        externalLink = ""
    }
}

class ExternalLifeCycle : Application.ActivityLifecycleCallbacks {
    override fun onActivityPaused(activity: Activity) {

    }

    override fun onActivityStarted(activity: Activity) {

    }

    override fun onActivityDestroyed(activity: Activity) {

    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {

    }

    override fun onActivityStopped(activity: Activity) {

    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {

    }

    override fun onActivityResumed(activity: Activity) {
        if (activity is SplashActivity) {

        } else {
            activity.handleExternal()
        }
    }

}