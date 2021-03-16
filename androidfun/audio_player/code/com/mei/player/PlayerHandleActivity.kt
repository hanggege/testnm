package com.mei.player

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.WindowManager
import androidx.fragment.app.commit
import com.gyf.immersionbar.ImmersionBar
import com.mei.base.common.MUTE_REMOTE_AUDIO_STREAMS
import com.mei.orc.ActivityLauncher
import com.mei.orc.event.postAction
import com.mei.player.fragment.PlayerBaseFragment
import com.mei.player.fragment.PlayerOneFragmentLauncher
import com.mei.player.view.IgnorePlayerBar
import com.mei.wood.R
import com.mei.wood.ui.MeiCustomActivity
import com.mei.wood.util.AppManager
import com.pili.player
import kotlinx.android.synthetic.main.player_handle_layout.*
import launcher.Boom

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2020/7/3
 *
 * 为了防止之后出现多个播放器，进行拆分，播放器同时只能存在一个进行播放
 */
class PlayerHandleActivity : MeiCustomActivity(), IgnorePlayerBar {

    override fun customStatusBar() = true

    @Boom
    var audioId: Int = 0
    var playerFragment: PlayerBaseFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        setContentView(R.layout.player_handle_layout)
        ActivityLauncher.bind(this)
        ImmersionBar.with(this).apply {
            statusBarDarkFont(true)
            statusBarColorInt(Color.WHITE)
            transparentStatusBar()
            statusBarView(status_bar_view)
        }.init()

        supportFragmentManager.commit(allowStateLoss = true) {
            replace(R.id.player_fragment, provideFragment())
        }
        postAction(MUTE_REMOTE_AUDIO_STREAMS, true)
    }


    private fun provideFragment(): PlayerBaseFragment {
        val fragment = PlayerOneFragmentLauncher.newInstance(audioId)
        playerFragment = fragment
        return fragment
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)
        ActivityLauncher.bind(this)
        supportFragmentManager.commit(allowStateLoss = true) {
            replace(R.id.player_fragment, provideFragment())
        }
    }

    override fun finish() {
        super.finish()
        AppManager.getInstance().removeActivity(this)
        if (!player.isPlaying()) {
            postAction(MUTE_REMOTE_AUDIO_STREAMS, false)
            player.stop()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }
}