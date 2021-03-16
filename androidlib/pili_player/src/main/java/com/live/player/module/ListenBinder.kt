package com.live.player.module

import android.os.Binder
import com.live.player.presenter.ListenAudioPresenter

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2018/9/27
 */
abstract class ListenBinder<Info : ListenInfo> : Binder() {
    abstract fun presenter(): ListenAudioPresenter<Info>
}