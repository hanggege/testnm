package com.mei.keyboard

import android.R
import android.app.Activity
import android.view.View
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2020/6/4
 */

fun LifecycleOwner.keyboardBindLife(activity: Activity, heightBack: (height: Int) -> Unit): KeyboardHeightProvider {
    val provider = KeyboardHeightProvider(activity).apply {
        setKeyboardHeightObserver { height, _ ->
            heightBack(height)
        }
    }
    val parentView = activity.findViewById<View>(R.id.content)
    parentView.post { provider.start() }
    this.lifecycle.addObserver(object : LifecycleEventObserver {
        override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
            when (event) {
                Lifecycle.Event.ON_DESTROY -> provider.close()
                Lifecycle.Event.ON_RESUME -> provider.start()
                else -> {
                }
            }
        }

    })
    return provider
}