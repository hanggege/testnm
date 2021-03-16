package com.pili.stream

import android.app.Service
import android.content.Intent
import android.os.IBinder

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2018/9/27
 */
//class SteamService : BaseStreamService<PlayInfo>() {
//
//    override fun onBind(intent: Intent): IBinder {
//        return object : StreamBinder<PlayInfo>() {
//            override fun presenter(): StreamPresenter<PlayInfo> {
//                return this@SteamService
//            }
//        }
//    }
//
//    override fun getPlayInfo(): PlayInfo {
//        val info = super.getPlayInfo() ?: PlayInfo.NONE
//        info.setPlaying(isStreaming())
//        return info
//    }
//}
class SteamService : Service() {

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

}