package com.mei.wood

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.FragmentActivity
import com.mei.base.network.netchange.NetWorkListener
import com.mei.base.network.netchange.networkController
import com.mei.wood.location.AddressView
import com.mei.wood.location.bindAddressView
import com.mei.wood.location.unbindAddressView
import com.mei.wood.util.AppManager

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2017/3/28.
 */

class AppLifeManager : Application.ActivityLifecycleCallbacks {

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        AppManager.getInstance().addActivity(activity)
        Log.e("currenActivity", "onCreated: ${activity::class.java.name}")
        if (activity is FragmentActivity) {
            activity.supportFragmentManager.registerFragmentLifecycleCallbacks(AppFragmentLifeManager(), true)
        }
        if (activity is NetWorkListener) networkController().bindView(activity)
    }


    override fun onActivityStarted(activity: Activity) {
        if (activity is AddressView) {
            activity.bindAddressView(activity)
        }
    }

    override fun onActivityResumed(activity: Activity) {
        Log.e("currenActivity", "onResumed: ${activity::class.java.name}")
    }

    override fun onActivityPaused(activity: Activity) {
        Log.e("currenActivity", "onPaused: ${activity::class.java.name}")
    }

    override fun onActivityStopped(activity: Activity) {
        if (activity is AddressView) {
            unbindAddressView(activity)
        }
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle?) {

    }

    override fun onActivityDestroyed(activity: Activity) {
        AppManager.getInstance().removeActivity(activity)
        Log.e("currenActivity", "onDestroy: ${activity::class.java.name}")
        if (activity is NetWorkListener) networkController().unBindView(activity)


    }
}
