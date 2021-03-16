package com.live.stream.module

import android.os.Binder
import com.live.stream.presenter.StreamPresenter

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2018/9/27
 */
abstract class StreamBinder<Info : StreamInfo> : Binder() {
    abstract fun presenter(): StreamPresenter<Info>
}