package com.mei.base.network.netchange

import com.mei.init.meiApplication
import com.mei.orc.ext.runOnUiThread
import com.mei.orc.net.NetInfo
import com.mei.orc.net.registerNetwork
import com.mei.orc.util.json.toJson
import com.mei.wood.util.logDebug
import java.util.concurrent.CopyOnWriteArraySet


/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2018/11/21
 */

fun networkController() = NetWorkController.instance

/**
 * 检查网络是否从不可用变可用
 */
fun NetInfo?.netChangeToAvailable(): Boolean = this != null && preType == NetInfo.NetType.NONE && currTye != NetInfo.NetType.NONE
fun NetInfo?.netChangeToNone(): Boolean = this != null && preType != NetInfo.NetType.NONE && currTye == NetInfo.NetType.NONE

interface NetWorkListener {
    fun netChange(netInfo: NetInfo)
}

class NetWorkController {

    companion object {
        val instance: NetWorkController by lazy { NetWorkController() }
    }

    init {
        meiApplication().applicationContext.registerNetwork { net ->
            runOnUiThread {
                logDebug("${net.toJson()}")
                looperView { it.netChange(net) }
            }
        }
    }

    private val viewSet: CopyOnWriteArraySet<NetWorkListener> by lazy { CopyOnWriteArraySet<NetWorkListener>() }


    @Synchronized
    private fun looperView(action: (NetWorkListener) -> Unit) {
        viewSet.forEach { action(it) }
    }

    fun bindView(l: NetWorkListener) = viewSet.add(l)
    fun unBindView(l: NetWorkListener) = viewSet.remove(l)
}