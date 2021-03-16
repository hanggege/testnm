package com.mei.live.ui

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import com.gyf.immersionbar.ImmersionBar
import com.mei.live.manager.genderAvatar
import com.mei.orc.ActivityLauncher
import com.mei.orc.imageload.glide.glideDisplay
import com.mei.orc.ui.toast.UIToast
import com.mei.orc.util.date.formatTimeVideo
import com.mei.wood.R
import com.mei.wood.extensions.executeToastKt
import com.mei.wood.ui.MeiCustomActivity
import com.net.MeiUser
import com.net.network.room.DataStatisticsRequest
import kotlinx.android.synthetic.main.activity_live_ending.*
import launcher.Boom

/**
 * Created by hang on 2020-03-24.
 * 直播结束页面
 */
class LiveEndingActivity : MeiCustomActivity() {

    @Boom
    var roomId = ""

    @Boom
    var broadcastId = ""

    @Boom
    var broadcastTime = 0L

    override fun customStatusBar(): Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_live_ending)
        ActivityLauncher.bind(this)

        ImmersionBar.with(this)
                .statusBarColorInt(Color.parseColor("#303030"))
                .statusBarDarkFont(false)
                .init()

        anchor_avatar.glideDisplay(MeiUser.getSharedUser().info?.avatar.orEmpty(), MeiUser.getSharedUser().info?.gender.genderAvatar(MeiUser.getSharedUser().info?.isPublisher))
        live_time.text = broadcastTime.formatTimeVideo()
        requestData()
        back_choice.setOnClickListener {
            finish()
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        val newId = intent?.getStringExtra("com.mei.live.ui.roomIdIntentKey").orEmpty()
        val broadcastTime = intent?.getLongExtra("com.mei.live.ui.broadcastTimeIntentKey", 0L)
        if (newId.isNotEmpty()) {
            roomId = newId
            live_time.text = broadcastTime.formatTimeVideo()
            requestData()
        }
    }

    private fun requestData() {
        apiSpiceMgr.executeToastKt(DataStatisticsRequest(roomId, broadcastId), success = {
            if (it.isSuccess) {
                it.data?.let { data ->
                    total_coin.text = data.receiveCount.toString()
                    audience_count.text = data.userCount.toString()
                    new_follow_count.text = data.followCount.toString()

                    UIToast.toast(data.receiveCountTips)
                }
            }
        })
    }
}