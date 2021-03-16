package com.mei.wood

import android.app.Activity
import android.app.Application
import android.os.Bundle

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2020/8/4
 */
class AppLifecycle(val onCreate: (Activity) -> Unit = {}, val onStarted: (Activity) -> Unit = {},
                   val onResume: (Activity) -> Unit = {}, val onPaused: (Activity) -> Unit = {},
                   val onStopped: (Activity) -> Unit = {}, val onDestroyed: (Activity) -> Unit = {}
) : Application.ActivityLifecycleCallbacks {
    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        onCreate(activity)
    }

    override fun onActivityStarted(activity: Activity) {
        onStarted(activity)
    }

    override fun onActivityResumed(activity: Activity) {
        onResume(activity)
    }

    override fun onActivityPaused(activity: Activity) {
        onPaused(activity)
    }

    override fun onActivityStopped(activity: Activity) {
        onStopped(activity)
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {

    }

    override fun onActivityDestroyed(activity: Activity) {
        onDestroyed(activity)
    }
}