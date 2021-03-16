package com.mei.wood.location

import android.Manifest
import android.content.Context
import android.os.Build
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.mei.init.meiApplication
import com.mei.orc.Cxt
import com.mei.orc.ext.runOnUiThread
import com.mei.orc.rxpermission.requestMulPermission
import com.mei.orc.rxpermission.requestSinglePermission
import com.mei.orc.util.permission.PermissionCheck
import java.util.concurrent.CopyOnWriteArraySet

/**
 * Created by zzw on 2019-10-15
 * Des:
 */

private val viewSet: CopyOnWriteArraySet<AddressView> by lazy { CopyOnWriteArraySet<AddressView>() }

fun locationManager() = MeiLocationManager(Cxt.get())

fun Context.bindAddressView(view: AddressView) {
    if (this is FragmentActivity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            requestMulPermission(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_BACKGROUND_LOCATION) {
                _bindAddressView(view)
            }
        } else {
            requestSinglePermission(Manifest.permission.ACCESS_FINE_LOCATION) {
                _bindAddressView(view)
            }
        }
    } else {
        _bindAddressView(view)
    }
}


private fun Context._bindAddressView(view: AddressView) {
    viewSet.add(view)

    if (this is LifecycleOwner) {
        lifecycle.addObserver(object : LifecycleEventObserver {
            override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                if (event == Lifecycle.Event.ON_DESTROY) {
                    unbindAddressView(view)
                }
            }
        })
    }
//    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//        if (PermissionCheck.hasPermission(meiApplication().applicationContext, Manifest.permission.ACCESS_BACKGROUND_LOCATION)) {
//            start()
//        } else {
//            //android Q 以上
//            //只有前台使用权的时候怎么办？
//        }
//    } else {
//        start()
//    }

    locationManager().start(success = {
        looperView(it)
    }, failure = { _, _ ->
        looperView(null)
    })


}

fun unbindAddressView(view: AddressView) {
    viewSet.remove(view)
}

@Synchronized
private fun looperView(location: LocationBean?) {
    runOnUiThread {
        viewSet.forEach {
            it.location(location)
        }
    }
}

interface AddressView {
    fun location(location: LocationBean?)
}


class MeiLocationManager(val context: Context) {

    /**置为null后可重新获取**/
    private var mLocationData: LocationBean? = null

    private val mLocationImpl: LocationImpl by lazy {
        NativeLocationImpl(context)
    }


    fun start(success: (LocationBean?) -> Unit, failure: (Int, String) -> Unit) {
        if (mLocationData != null) {
            success(mLocationData)
            return
        }
        if (PermissionCheck.hasPermission(meiApplication().applicationContext, Manifest.permission.ACCESS_FINE_LOCATION)) {
            mLocationImpl.start(success = { locationData ->
                mLocationData = locationData
                success(mLocationData)
            }, failure = { code, msg ->
                failure(code, msg)
            })
        } else {
            failure(-1, "没有开启定位权限或者手机不支持定位")
        }

    }


    fun stop() {
        mLocationImpl.stop()
    }
}


