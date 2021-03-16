package com.mei.wood

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.mei.orc.util.handler.GlobalUIHandler

/**
 *  Created by zzw on 2019-12-28
 *  Des: 轮训一段时间执行某一个任务的封装
 */
class TimingRunnable(private val delaySecond: Float, private val back: () -> Unit) : Runnable {
    override fun run() {
        GlobalUIHandler.postUiDelay(this, (delaySecond * 1000L).toLong())
        back()
    }

    fun start(startDelaySecond: Float = 0f) {
        GlobalUIHandler.postUiDelay(this, (startDelaySecond * 1000L).toLong())
    }

    fun cancel() {
        GlobalUIHandler.remove(this)
    }
}


/**
 * @param firstDelaySecond 第一次运行延时
 * @param delaySecond 之后的执行任务延时
 * @param back 执行任务
 */
fun LifecycleOwner.runTimingRunnable(firstDelaySecond: Float = 0f, delaySecond: Float, back: () -> Unit): TimingRunnable {
    val run = TimingRunnable(delaySecond, back)
    run.start(firstDelaySecond)

    lifecycle.addObserver(object : LifecycleEventObserver {
        override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
            if (event == Lifecycle.Event.ON_DESTROY) {
                run.cancel()
            }
        }
    })
    return run
}