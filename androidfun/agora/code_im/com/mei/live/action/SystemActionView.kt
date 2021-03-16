package com.mei.live.action

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.view.*
import com.mei.live.ext.createSplitSpannable
import com.mei.live.manager.genderAvatar
import com.mei.orc.ext.*
import com.mei.orc.imageload.glide.glideDisplay
import com.mei.orc.john.model.JohnUser
import com.mei.wood.R
import com.net.model.chick.global.TipsGet
import kotlinx.android.synthetic.main.live_system_action_item.view.*

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2020/8/4
 */

@SuppressLint("ViewConstructor")
class SystemActionView(context: Context, val looperNext: () -> TipsGet.ActionInfo?)
    : FrameLayout(context) {

    var attachActivity: Activity? = null
    private var showTime = 3000L//默认停留时间

    init {
        layoutInflaterKtToParentAttach(R.layout.live_system_action_item)
        setPadding(dip(20), dip(75) + getStatusBarHeight(), dip(15), 0)
        layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        clipToPadding = false
        action_container_view.isVisible = false
        action_container_view.translationX = screenWidth.toFloat()

    }

    fun checkAnimLooper() {
        if (action_container_view.isGone) {
            val data = looperNext()
            // 是否忽略当前这条消息
            val ignoreAction = (attachActivity as? IgnoreSystemAction)?.isShow(data) == false
            when {
                data == null -> action_container_view.isVisible = false
                !JohnUser.getSharedUser().hasLogin() -> checkAnimLooper()
                ignoreAction -> checkAnimLooper()
                true -> {
                    refreshInfo(data)
                    enterAnim()
                }
            }
        }
    }

    private fun refreshInfo(data: TipsGet.ActionInfo) {
        showTime = if (data.lastMs > 0) data.lastMs else 3000
        live_system_avatar_img.glideDisplay(data.avatar, data.gender.genderAvatar(false))
        live_system_action_tv.text = data.text.orEmpty().createSplitSpannable(Color.WHITE)
        live_system_action_tv.doOnLayout {
            flash_bg_view.updateLayoutParams { height = it.measuredHeight }
        }
    }

    private fun enterAnim() {
        action_container_view.clearAnimation()
        action_container_view.translationX = measuredWidth.toFloat()
        action_container_view.isVisible = true
        action_container_view.animate()
                .translationX(0f)
                .setDuration(500)
                .setStartDelay(0)
                .setListener { exitAnim() }
                .start()
    }

    private fun exitAnim() {
        // 控件进入后多久才开始闪光效果，从展示的时间中间开始显示
        val flashStartDelay: Long = when {
            showTime >= 3000 -> 1000
            showTime > 2000 -> (showTime - 900) / 2
            else -> 0
        }
        flash_bg_view.translationX = 0f
        flash_bg_view.postDelayed(flashStartDelay) {
            flash_bg_view.alpha = 1f
            flash_bg_view.animate()
                    .translationX(measuredWidth.toFloat() - dip(80))
                    .alpha(0f)
                    .setDuration(900)
                    .setListener {
                        flash_bg_view.translationX = 0f
                    }
                    .start()
        }

        action_container_view.animate()
                .translationX(-measuredWidth.toFloat())
                .setDuration(500)
                .setStartDelay(showTime)
                .setListener {
                    action_container_view.isVisible = false
                    checkAnimLooper()
                }
                .start()
    }

}


