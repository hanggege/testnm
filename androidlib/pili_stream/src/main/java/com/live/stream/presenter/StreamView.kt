package com.live.stream.presenter

import android.os.Handler
import android.os.Looper
import com.qiniu.pili.droid.streaming.StreamingState

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2018/9/26
 */
interface StreamView {
    /**
     * 推流状态变化监听
     */
    fun onStreamStatus(streamingState: StreamingState, extra: Any?)

    /**
     * 进入推流状态
     *
     * @param isSuccess 是否推流（连线）成功
     */
    fun onStreaming(isSuccess: Boolean)

    /**
     * 停止推流结果
     *
     * @param isSuccess 返回true， 停止成功，否则失败。
     */
    fun onStopStreaming(isSuccess: Boolean)

}


internal fun runOnUiThread(action: Runnable) = Handler(Looper.getMainLooper()).post(action)
internal fun runDelayed(delayMillis: Long, action: Runnable) = Handler().postDelayed(action, delayMillis)