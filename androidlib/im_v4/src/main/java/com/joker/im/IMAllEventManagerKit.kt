package com.joker.im

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.joker.im.ext.IMAllEventManagerExt
import com.joker.im.listener.IMAllEventManager
import com.tencent.imsdk.TIMConversation
import com.tencent.imsdk.TIMMessage


/**
 *  Created by zzw on 2019-07-11
 *  Des:
 */

private val impl: IMAllEventManagerExt by lazy {
    IMAllEventManagerExt()
}

/**
 * 注册监听
 */
fun IMAllEventManager.registered() {
    impl.registered(this)
}

/**
 * 移除监听
 */
fun IMAllEventManager.unregistered() {
    impl.unregistered(this)
}


/**
 * 绑定生命周期
 */
fun IMAllEventManager.bindEventLifecycle(any: Any?): IMAllEventManager {
    when (any) {
        is FragmentActivity -> {
            registered()
            any.lifecycle.addObserver(object : LifecycleEventObserver {
                override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                    if (event == Lifecycle.Event.ON_DESTROY) {
                        unregistered()
                    }
                }
            })
        }
        is Fragment -> {
            if (any.isAdded) {
                registered()
            }
            any.activity?.supportFragmentManager?.registerFragmentLifecycleCallbacks(object :
                    FragmentManager.FragmentLifecycleCallbacks() {

                override fun onFragmentResumed(fm: FragmentManager, f: Fragment) {
                    super.onFragmentResumed(fm, f)
                    if (any.isAdded && f == any) {
                        registered()
                    }
                }

                override fun onFragmentViewCreated(
                        fm: FragmentManager,
                        f: Fragment,
                        v: View,
                        savedInstanceState: Bundle?
                ) {
                    super.onFragmentViewCreated(fm, f, v, savedInstanceState)
                    if (f == any) {
                        registered()
                    }
                }

                override fun onFragmentViewDestroyed(fm: FragmentManager, f: Fragment) {
                    super.onFragmentViewDestroyed(fm, f)
                    if (f == any) {
                        unregistered()
                    }
                }

            }, true)
        }
    }
    return this
}

/**
 * 触发全部刷新
 */
fun refreshAll() {
    impl.onRefresh()
}

fun refreshConversation(conversations: List<TIMConversation>) {
    impl.onRefreshConversation(conversations)
}


/**
 * 通知新消息，一般用于本地发送，然后触发所有的监听，让其他地方知道有新消息了
 */
fun newMessages(messages: List<TIMMessage>) {
    impl.onNewMessages(messages)
}

fun refreshMessages(messages: List<TIMMessage>) {
    impl.onRefreshMessage(messages)
}