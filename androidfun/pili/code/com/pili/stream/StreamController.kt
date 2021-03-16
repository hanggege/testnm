package com.pili.stream

import android.app.Application
import com.live.stream.presenter.StreamView
import com.mei.init.BaseApp
import com.pili.PlayInfo
import java.util.concurrent.CopyOnWriteArraySet

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2018/9/27
 */

val streamController: StreamController by lazy {
    StreamController()
}


fun bindStreamService(): StreamController {
    return BaseApp.instance.bindStreamService()
}

fun Application.bindStreamService(): StreamController {
//    if (streamController.presenter == null || !isServiceRunning(applicationContext, SteamService::class.java.name)) {
//        try {
//            bindService(Intent(applicationContext, SteamService::class.java), object : ServiceConnection {
//                override fun onServiceDisconnected(name: ComponentName?) {
//                    streamController.presenter = null
//                }
//
//                @Suppress("UNCHECKED_CAST")
//                override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
//                    val presenter = (service as? StreamBinder<PlayInfo>)?.presenter()
//                    streamController.presenter = presenter
//                }
//
//            }, Service.BIND_AUTO_CREATE)
//        } catch (e: Exception) {
//            e.printStackTrace()
//            logDebug("android.os.DeadSystemException现在普遍发生在Android7.0上，是由android os抛出来的，一般是启动服务的时候catch到了异常,暂时来看我们是什么都做不了。")
//        }
//    }
    return streamController
}

class StreamController {
    //: StreamPresenter<PlayInfo> {
    val viewSet: CopyOnWriteArraySet<StreamView> by lazy { CopyOnWriteArraySet<StreamView>() }

//    var presenter: StreamPresenter<PlayInfo>? = null
//        set(value) {
//            field = value
//            value?.bindView(object : StreamView {
//                override fun onStreamStatus(streamingState: StreamingState, extra: Any?) {
//                    logDebug("stream", "onStreamStatus:${streamingState.name} ");
//                    looperView { it.onStreamStatus(streamingState, extra) }
//                }
//
//                override fun onStreaming(isSuccess: Boolean) {
//                    looperView { it.onStreaming(isSuccess) }
//                }
//
//                override fun onStopStreaming(isSuccess: Boolean) {
//                    looperView { it.onStopStreaming(isSuccess) }
//                }
//
//            })
//        }


    @Synchronized
    private fun looperView(action: (StreamView) -> Unit) {
        viewSet.forEach { action(it) }
    }

    fun bindView(view: StreamView) {
        viewSet.add(view)
    }

    fun unBindView(view: StreamView) {
        viewSet.remove(view)
    }

    fun start(@Suppress("UNUSED_PARAMETER") info: PlayInfo) {
//        presenter?.start(info)
    }

    fun stop() {
//        presenter?.stop()
    }

    fun isStreaming(): Boolean = false// presenter?.isStreaming() ?: false


    fun getPlayInfo(): PlayInfo = PlayInfo.NONE//presenter?.getPlayInfo() ?: PlayInfo.NONE

    fun resetReady() {
//        presenter?.resetReady()
    }

}