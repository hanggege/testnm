package com.mei.radio

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.commit
import com.mei.GrowingUtil
import com.mei.base.common.MUTE_REMOTE_AUDIO_STREAMS
import com.mei.orc.event.postAction
import com.mei.orc.ext.transparentStatusBar
import com.mei.player.fragment.PlayerBaseFragment
import com.mei.wood.R
import com.mei.wood.ui.MeiCustomActivity
import com.mei.wood.util.AppManager

/**
 *
 * @author Created by lenna on 2020/9/24
 * 电台播放器
 */
class MeiRadioPlayerActivity : MeiCustomActivity() {
    override fun customStatusBar() = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_radio_player)
        transparentStatusBar()
        supportFragmentManager.commit(allowStateLoss = true) {
            replace(R.id.radio_player_fragment, provideFragment())
        }
        postAction(MUTE_REMOTE_AUDIO_STREAMS, true)

    }

    private fun provideFragment(): PlayerBaseFragment {
        return RadioFragment()
    }

    override fun onResume() {
        super.onResume()
        statRadioBrowseEvent()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        supportFragmentManager.commit(allowStateLoss = true) {
            replace(R.id.radio_player_fragment, provideFragment())
        }
    }

    override fun finish() {
        super.finish()
        AppManager.getInstance().removeActivity(this)
    }

    /**电台浏览统计*/
    private fun statRadioBrowseEvent() = try {
        GrowingUtil.track("common_page_view", "page_name", "电台页",
                "page_type", "",
                "click_content", "",
                "time_stamp", "${System.currentTimeMillis() / 1000}")
    } catch (e: Exception) {
        e.printStackTrace()
    }
}