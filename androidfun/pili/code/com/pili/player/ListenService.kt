package com.pili.player

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.IBinder
import com.live.player.BaseListenService
import com.live.player.module.ListenBinder
import com.live.player.presenter.ListenAudioPresenter
import com.mei.base.network.netchange.NetWorkListener
import com.mei.base.network.netchange.networkController
import com.mei.orc.net.NetInfo
import com.pili.PlayInfo
import com.pili.pldroid.player.PLOnErrorListener

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2018/9/28
 */
internal const val MUSIC_NOTIFICATION_ACTION_PRE = "MUSIC_NOTIFICATION_ACTION_PRE"
internal const val MUSIC_NOTIFICATION_ACTION_NEXT = "MUSIC_NOTIFICATION_ACTION_NEXT"
internal const val MUSIC_NOTIFICATION_ACTION_PLAY = "MUSIC_NOTIFICATION_ACTION_PLAY"
internal const val MUSIC_NOTIFICATION_ACTION_CLOSE = "MUSIC_NOTIFICATION_ACTION_CLOSE"

class ListenService : BaseListenService<PlayInfo>(), NetWorkListener {
    private var errCode = 0

    private val playActionBroadcastReceiver by lazy {
        object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                when (intent?.action) {
                    Intent.ACTION_HEADSET_PLUG -> {
                        /** 拔掉耳机暂停 **/
                        if (intent.getIntExtra("state", -1) == 0 && player().isPlaying) {
                            pause()
                        }
                    }
                    MUSIC_NOTIFICATION_ACTION_PLAY -> {
                        if (isPlaying()) {
                            pause()
                        } else {
                            resume()
                        }
                    }
                    MUSIC_NOTIFICATION_ACTION_NEXT -> {
//                        getPlayerBusiness()?.preOrNext(1)
                    }

                    MUSIC_NOTIFICATION_ACTION_PRE -> {
//                        getPlayerBusiness()?.preOrNext(-1)
                    }
                    MUSIC_NOTIFICATION_ACTION_CLOSE -> {
                        //如果当前是直播并且  当前栈有LiveRoomActivity 则点击关闭无效
//                        if (playInfo?.mPlayType == PlayType.LISTENING_LIVE &&
//                                AppManager.getInstance().getTargetActivity(LiveRoomActivity::class.java) as? LiveRoomActivity != null) {
//                            Log.d("ListenService", "playType == LISTENING_LIVE && has LiveRoomActivity stack")
//                        } else {
                        stop()
//                        }
                    }
                }
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        networkController().bindView(this)
        registerReceiver(playActionBroadcastReceiver, IntentFilter().apply {
            addAction(MUSIC_NOTIFICATION_ACTION_PLAY)
            addAction(MUSIC_NOTIFICATION_ACTION_NEXT)
            addAction(MUSIC_NOTIFICATION_ACTION_PRE)
            addAction(MUSIC_NOTIFICATION_ACTION_CLOSE)
            addAction(Intent.ACTION_HEADSET_PLUG)
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        networkController().unBindView(this)

        unregisterReceiver(playActionBroadcastReceiver)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return object : ListenBinder<PlayInfo>() {
            override fun presenter(): ListenAudioPresenter<PlayInfo> {
                return this@ListenService
            }
        }
    }

    override fun getPlayInfo(): PlayInfo {
        val info = super.getPlayInfo() ?: PlayInfo.NONE
        info.setPlaying(isPlaying())
        return info
    }

    override fun start(info: PlayInfo) {
        super.start(info)
        errCode = 0
    }

    override fun resume() {
        super.resume()
        errCode = 0
    }

    override fun stop() {
        super.stop()
        errCode = 0
    }

    override fun onError(code: Int): Boolean {
        errCode = code
        return super.onError(code)
    }


    override fun netChange(netInfo: NetInfo) {
        /** 断网 恢复后重连 **/
        if (netInfo.currTye != NetInfo.NetType.NONE && getPlayInfo().url().isNotEmpty()
                && errCode == PLOnErrorListener.ERROR_CODE_IO_ERROR) {
            if (player().dataSource.orEmpty().startsWith("rtmp")) {//直播重新开始
                start(getPlayInfo())
            } else {
                resume()
            }
        }
    }
}