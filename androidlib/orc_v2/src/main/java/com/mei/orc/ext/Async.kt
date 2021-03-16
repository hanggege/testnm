package com.mei.orc.ext

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2017/3/7.
 */

import android.os.*
import com.mei.orc.util.handler.GlobalUIHandler


fun runAsync(action: () -> Unit) {
    val asyncTask = object : AsyncTask<Unit, Unit, Unit>() {
        override fun doInBackground(vararg params: Unit?) {
            action()
        }
    }
    asyncTask.execute()
}

fun <T> runAsyncCallBack(action: () -> T?, uiBack: (T?) -> Unit) {
    val asyncTask = object : AsyncTask<Unit, Unit, T?>() {

        override fun doInBackground(vararg params: Unit?): T? {
            return action()
        }

        override fun onPostExecute(result: T?) {
            super.onPostExecute(result)
            uiBack(result)

        }
    }
    asyncTask.execute()
}

fun runOnUiThread(action: () -> Unit) = GlobalUIHandler.postUi { action() }

fun runDelayedOnUiThread(delayMillis: Long, action: () -> Unit) = GlobalUIHandler.postUiDelay(Runnable(action), delayMillis)

fun doOnIdle(action: () -> Unit, timeout: Long? = null) {
    val handler = Handler(Looper.getMainLooper())
    val idleHandler = MessageQueue.IdleHandler {
        handler.removeCallbacksAndMessages(null)
        action()
        false
    }

    fun setupIdleHandler(queue: MessageQueue) {
        if (timeout != null) {
            handler.postDelayed({
                queue.removeIdleHandler(idleHandler)
                action()
            }, timeout)
        }
        queue.addIdleHandler(idleHandler)
    }

    if (Looper.myLooper() == Looper.getMainLooper()) {
        setupIdleHandler(Looper.myQueue())
    } else {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            setupIdleHandler(Looper.getMainLooper().queue)
        } else {
            handler.post { setupIdleHandler(Looper.myQueue()) }
        }
    }
}
