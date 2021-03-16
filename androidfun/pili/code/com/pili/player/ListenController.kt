package com.pili.player

import android.app.Application
import android.app.Service
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import com.live.player.module.ListenBinder
import com.live.player.presenter.ListenAudioPresenter
import com.live.player.presenter.ListenAudioView
import com.live.player.presenter.ListenView
import com.mei.init.BaseApp
import com.mei.orc.util.app.isServiceRunning
import com.mei.orc.util.json.toJson
import com.mei.wood.util.logDebug
import com.pili.PlayInfo
import com.pili.business.PlayerBusiness
import com.pili.stream.bindStreamService
import java.util.concurrent.CopyOnWriteArraySet

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2018/9/27
 *
 * 全局播放器控制器，分发所有回调
 */


val listenController: ListenController by lazy {
    ListenController()
}


fun bindPlayerService(): ListenController {
    return BaseApp.instance.bindPlayerService()
}

fun Application.bindPlayerService(): ListenController {
    if (listenController.presenter == null || !isServiceRunning(applicationContext, ListenService::class.java.name)) {
        try {
            bindService(Intent(applicationContext, ListenService::class.java), object : ServiceConnection {
                override fun onServiceDisconnected(name: ComponentName?) {
                    listenController.presenter = null
                }

                @Suppress("UNCHECKED_CAST")
                override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                    val presenter = (service as? ListenBinder<PlayInfo>)?.presenter()
                    listenController.presenter = presenter
//                    val looper = listenController.viewSet.find { TextUtils.equals(it::class.java.name, PlayerBusiness::class.java.name) }
//                    if (looper == null) listenController.bindView(PlayerBusiness())
                }

            }, Service.BIND_AUTO_CREATE)
        } catch (e: Exception) {
            e.printStackTrace()
            logDebug("android.os.DeadSystemException现在普遍发生在Android7.0上，是由android os抛出来的，一般是启动服务的时候catch到了异常,暂时来看我们是什么都做不了。")
        }
    }
    return listenController
}

class ListenController : ListenAudioPresenter<PlayInfo> {


    val viewSet: CopyOnWriteArraySet<ListenView> by lazy {
        CopyOnWriteArraySet<ListenView>().apply {
            add(PlayerBusiness())
        }
    }

    var presenter: ListenAudioPresenter<PlayInfo>? = null
        set(value) {
            field = value
            value?.bindView(object : ListenAudioView {
                override fun onCompletion() {
                    logDebug("player", "onCompletion:  playInfo = ${getPlayInfo().toJson()}")
                    looperView { (it as? ListenAudioView)?.onCompletion() }
                }

                override fun onBufferingUpdate(percent: Int) {
                    looperView { (it as? ListenAudioView)?.onBufferingUpdate(percent) }
                }

                override fun onSeekComplete() {
                    logDebug("player", "onSeekComplete:   playInfo = ${getPlayInfo().toJson()}")
                    looperView { (it as? ListenAudioView)?.onSeekComplete() }
                }

                override fun onStateChange(isPlaying: Boolean) {
                    looperView { (it as? ListenAudioView)?.onStateChange(isPlaying) }
                }

                override fun onPrepared(preparedTime: Int) {
                    logDebug("player", "onPrepared: $preparedTime   playInfo = ${getPlayInfo().toJson()}")
                    looperView { it.onPrepared(preparedTime) }
                }

                override fun onStopPlayer() {
                    logDebug("player", "onStopPlayer:  playInfo = ${getPlayInfo().toJson()} ")
                    looperView { it.onStopPlayer() }
                }

                override fun onInfo(what: Int, extra: Int) {
                    looperView { it.onInfo(what, extra) }
                }

                override fun onError(code: Int) {
                    logDebug("player", "onError: $code  playInfo = ${getPlayInfo().toJson()}")
                    looperView { it.onError(code) }
                }
            })
        }


    @Synchronized
    private fun looperView(action: (ListenView) -> Unit) {
        viewSet.forEach { action(it) }
    }

    override fun pause() {
        presenter?.pause()
    }

    override fun resume() {
        presenter?.resume()
    }

    override fun getDuration(): Long = presenter?.getDuration() ?: 0L

    override fun getProgress(): Long = presenter?.getProgress() ?: 0L


    override fun seekTo(progress: Long) {
        presenter?.seekTo(progress)
    }

    override fun bindView(view: ListenView) {
        viewSet.add(view)
    }

    override fun unBindView(view: ListenView) {
        viewSet.remove(view)
    }

    override fun start(info: PlayInfo) {
        presenter?.start(info)
        bindStreamService().stop()
    }

    override fun stop() {
        presenter?.stop()
    }

    override fun isPlaying(): Boolean = presenter?.isPlaying() ?: false

    override fun getPlayInfo(): PlayInfo = presenter?.getPlayInfo() ?: PlayInfo.NONE

}