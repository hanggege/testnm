package com.pili.business

import android.app.Activity
import android.app.Application
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.live.player.presenter.ListenView
import com.live.stream.presenter.StreamView
import com.mei.player.view.registerFloatBar
import com.pili.player.bindPlayerService
import com.pili.stream.bindStreamService

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2018/9/27
 * Activity生命周期自动监听音频回调
 */
fun Application.initPili() {
    registerActivityLifecycleCallbacks(PlayerLifeManager(this))
    registerFloatBar()
}

class PlayerLifeManager(@Suppress("UNUSED_PARAMETER") application: Application) : Application.ActivityLifecycleCallbacks {

    init {
        //100-200ms
//        StreamingEnv.init(application.applicationContext)
    }


    override fun onActivityCreated(activity: Activity?, savedInstanceState: Bundle?) {
        if (activity is ListenView) {
            bindPlayerService().bindView(activity)
        }
        if (activity is StreamView) {
            bindStreamService().bindView(activity)
        }
        if (activity is FragmentActivity) {
            activity.supportFragmentManager.registerFragmentLifecycleCallbacks(PiliFragmentLifeCycle(), true)
        }
    }


    override fun onActivityStarted(activity: Activity?) {
    }

    override fun onActivityResumed(activity: Activity?) {

    }

    override fun onActivityPaused(activity: Activity?) {
    }

    override fun onActivityStopped(activity: Activity?) {
    }

    override fun onActivitySaveInstanceState(activity: Activity?, outState: Bundle?) {
    }

    override fun onActivityDestroyed(activity: Activity?) {
        if (activity is ListenView) {
            bindPlayerService().unBindView(activity)
        }
        if (activity is StreamView) {
            bindStreamService().unBindView(activity)
        }
    }


}