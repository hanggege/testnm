package com.mei.short_video

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.commit
import com.mei.orc.ext.transparentStatusBar
import com.mei.wood.R
import com.mei.wood.ui.MeiCustomActivity
import launcher.Boom
import launcher.MulField

/**
 *
 * @author Created by lenna on 2020/10/20
 * 广场or推荐tab 页面
 */
class SquareOrRecommendActivity : MeiCustomActivity() {
    @MulField
    @Boom
    var tabId: String = ""

    @MulField
    @Boom
    var tagId: Int = 0
    override fun customStatusBar() = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_square_recommend)
        transparentStatusBar()
        supportFragmentManager.commit(allowStateLoss = true) {
            replace(R.id.square_recommend_fragment, ShortVideoTabFragment().apply {
                tabStrId = tabId
                tagsId = tagId
            })
        }

    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        val tabId = intent?.getStringExtra("com.mei.short_video.tabIdIntentKey").orEmpty()
        val tagId = intent?.getIntExtra("com.mei.short_video.tagIdIntentKey", 0)
        supportFragmentManager.commit(allowStateLoss = true) {
            replace(R.id.radio_player_fragment, ShortVideoTabFragment().apply {
                tabStrId = tabId
                tagsId = tagId ?: 0
            })
        }
    }
}