package com.mei.orc.net

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build
import android.os.Handler
import android.os.Looper
import androidx.core.net.ConnectivityManagerCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner


fun Context.isNetworkConnected(): Boolean {
    // 获取手机所有连接管理对象(包括对wi-fi,net等连接的管理)
    val manager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        val capabilities = manager.getNetworkCapabilities(manager.activeNetwork)
        return capabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
    } else {
        @Suppress("DEPRECATION")
        return manager.activeNetworkInfo?.isConnected == true
    }
}

fun Context.registerNetwork(callback: (NetInfo) -> Unit = {}) {
    val back: (NetInfo) -> Unit = {
        /** 回调到主线程再回调 **/
        Handler(Looper.getMainLooper()).post { callback(it) }
    }
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        NetWorkStateCallBack(this, back)
    } else {
        NetWorkStateReceiver(this, back)
    }
}


class NetWorkStateReceiver(context: Context, val callback: (NetInfo) -> Unit = {}) : BroadcastReceiver() {
    private var preType: NetInfo.NetType = NetInfo.NetType.MOBILE
    private val filter = IntentFilter().apply {
        @Suppress("DEPRECATION")
        addAction(ConnectivityManager.CONNECTIVITY_ACTION)
    }

    init {
        context.registerReceiver(this, filter)
        /** 绑定生命周期 **/
        (context as? FragmentActivity)?.lifecycle?.addObserver(object : LifecycleEventObserver {
            override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                if (event == Lifecycle.Event.ON_DESTROY) {
                    context.registerReceiver(this@NetWorkStateReceiver, filter)
                }
            }

        })
    }

    @Suppress("DEPRECATION")
    override fun onReceive(context: Context, intent: Intent) {
        //获得ConnectivityManager对象
        val connMgr = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val info = ConnectivityManagerCompat.getNetworkInfoFromBroadcast(connMgr, intent)
        val netInfo = if (info == null || !info.isConnected) {
            NetInfo(preType, NetInfo.NetType.NONE)
        } else {
            NetInfo(preType, NetInfo.NetType.parseType(info.typeName))
        }
        preType = netInfo.currTye
        callback(netInfo)
    }
}

class NetWorkStateCallBack(context: Context, val callback: (NetInfo) -> Unit = {}) : ConnectivityManager.NetworkCallback() {

    private val connMgr = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    private val request = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
            .build()

    init {
        connMgr.registerNetworkCallback(request, this)
        /** 绑定生命周期 **/
        (context as? FragmentActivity)?.lifecycle?.addObserver(object : LifecycleEventObserver {
            override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                if (event == Lifecycle.Event.ON_DESTROY) {
                    connMgr.unregisterNetworkCallback(this@NetWorkStateCallBack)
                }
            }

        })
    }

    private var preType: NetInfo.NetType = NetInfo.NetType.MOBILE

    /**
     * 网络不可用时调用和onAvailable成对出现
     * 在网络 wifi 与 移动网络切换时 会出现短暂无网情况
     */
    override fun onLost(network: Network) {
        super.onLost(network)
        callback(NetInfo(preType, NetInfo.NetType.NONE))
        preType = NetInfo.NetType.NONE
    }


    /**
     * 字面直接能理解
     * @param network 新连接网络
     * @param networkCapabilities 新连接网络的一些能力参数
     */
    override fun onCapabilitiesChanged(network: Network, networkCapabilities: NetworkCapabilities) {
        super.onCapabilitiesChanged(network, networkCapabilities)
        val type = when {
            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> NetInfo.NetType.WIFI
            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> NetInfo.NetType.MOBILE
            else -> NetInfo.NetType.NONE
        }
        callback(NetInfo(preType, type))
        preType = type
    }

}
