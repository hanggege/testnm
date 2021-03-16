package com.mei.player.view

import android.app.Activity
import android.app.Application
import android.view.ViewGroup
import androidx.core.view.doOnLayout
import com.mei.init.meiApplication
import com.mei.wood.AppLifecycle
import com.mei.wood.ui.MeiCustomBarActivity

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2020/7/7
 */

fun Application.registerFloatBar() {
    registerActivityLifecycleCallbacks(AppLifecycle(onResume = {
        it.checkFloatBar()
    }, onPaused = {
        playerFloatBar.unfoldPlayerBar(false)
    }))
}

val playerFloatBar: PlayerFloatBarView by lazy { PlayerFloatBarView(meiApplication().applicationContext) }
private fun Activity.checkFloatBar() {
    (window.decorView as? ViewGroup)?.let { contentView ->
        /** 当前界面没添加了，且是App内的界面 **/
        if (playerFloatBar.parent != contentView && this is MeiCustomBarActivity) {
            (playerFloatBar.parent as? ViewGroup)?.removeView(playerFloatBar)
            contentView.addView(playerFloatBar)
            playerFloatBar.attachActivity = this
            playerFloatBar.refreshView()
            playerFloatBar.dragItemView.doOnLayout {
                playerFloatBar.bringToFront()
            }

        }
    }
}

interface IgnorePlayerBar {
    fun isShow(): Boolean = false
}
