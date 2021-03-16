package com.mei.agora.config

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import com.mei.agora.agoraConfig
import com.mei.agora.event.AgoraEventHandler

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2019-11-25
 */
class AgoraLifecycle : Application.ActivityLifecycleCallbacks {
    override fun onActivityPaused(activity: Activity) {

    }

    override fun onActivityStarted(activity: Activity) {

    }

    override fun onActivityDestroyed(activity: Activity) {
        if (activity is AgoraEventHandler) {
            agoraConfig.unBindView(activity)
        }
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {

    }

    override fun onActivityStopped(activity: Activity) {

    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        if (activity is AgoraEventHandler) {
            agoraConfig.bindView(activity)
        }
        if (activity is FragmentActivity) {
            activity.supportFragmentManager
                    .registerFragmentLifecycleCallbacks(
                            AgoraFragmentLifecycler(),
                            true
                    )
        }
    }

    override fun onActivityResumed(activity: Activity) {

    }
}


private class AgoraFragmentLifecycler : FragmentManager.FragmentLifecycleCallbacks() {
    override fun onFragmentResumed(fm: FragmentManager, f: Fragment) {
        super.onFragmentResumed(fm, f)
        if (f is AgoraEventHandler) {
            agoraConfig.bindView(f)
        }
    }

    override fun onFragmentViewCreated(fm: FragmentManager, f: Fragment, v: View, savedInstanceState: Bundle?) {
        super.onFragmentViewCreated(fm, f, v, savedInstanceState)
        if (f is AgoraEventHandler) {
            agoraConfig.bindView(f)
        }
    }

    override fun onFragmentViewDestroyed(fm: FragmentManager, f: Fragment) {
        super.onFragmentViewDestroyed(fm, f)
        if (f is AgoraEventHandler) {
            agoraConfig.unBindView(f)
        }
    }
}