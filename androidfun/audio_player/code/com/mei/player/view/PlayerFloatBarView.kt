package com.mei.player.view

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import com.live.player.presenter.ListenAudioView
import com.mei.base.ui.nav.Nav
import com.mei.live.ext.parseColor
import com.mei.orc.ext.dip
import com.mei.orc.ext.layoutInflaterKtToParentAttach
import com.mei.orc.imageload.glide.glideDisplay
import com.mei.orc.util.date.formatTimeVideo
import com.mei.wood.R
import com.mei.wood.ext.AmanLink
import com.mei.wood.util.AppManager
import com.net.model.broadcast.PlayType
import com.pili.getPlayInfo
import com.pili.player
import com.pili.player.listenController
import com.pili.pldroid.player.PLOnInfoListener
import kotlinx.android.synthetic.main.player_float_bar.view.*

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2020/7/7
 */

private var oldTop: Int = -1
private var oldLeft: Int = -1

class PlayerFloatBarView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
    : FloatDragView(context, attrs, defStyleAttr), ListenAudioView {

    override val dragItemView: ViewGroup by lazy { root_container }

    init {
        listenController.bindView(this)
        layoutInflaterKtToParentAttach(R.layout.player_float_bar)
        initView()
        layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
    }

    /** 是否是折叠状态 **/
    var isFold: Boolean
        get() = float_detail_group.isGone
        set(_) {}

    var attachActivity: Activity? = null
    var runnable: Runnable? = null
    private var lastIsLeft = false// 最后一次收起是否是在左边

    private fun initView() {
        dragItemView.updateLayoutParams { width = dip(87) }
        player_avatar_img.setOnClickListener {
            if (isFold) {
                unfoldPlayerBar(true)
                runnable = Runnable { if (isVisible) unfoldPlayerBar(false) }
                postDelayed(runnable, 3000)
            } else player_container_layout.performClick()
        }
        player_state_btn.setOnClickListener {
            if (player.isPlaying()) player.pause() else player.resume()
        }
        float_close_btn.setOnClickListener {
            player.stop()
            unfoldPlayerBar(false)
        }
        player_container_layout.setOnClickListener {
            val context = attachActivity ?: AppManager.getInstance().currentActivity()
            if (context != null) {
                Nav.toAmanLink(context, "${AmanLink.URL.AUDIO_PLAYER}?audioId=${getPlayInfo().mAudioId}")
            }

        }
    }

    /**
     * 是否展开播放条
     * @param unfold 展开
     */
    fun unfoldPlayerBar(unfold: Boolean) {
        TransitionManager.beginDelayedTransition(this, AutoTransition())
        if (dragItemView.top > 0) oldTop = dragItemView.top
        oldLeft = if (unfold) {
            lastIsLeft = dragItemView.left < measuredWidth / 2
            dragItemView.updateLayoutParams { width = ViewGroup.LayoutParams.MATCH_PARENT }
            0
        } else {
            dragItemView.updateLayoutParams { width = dip(87) }
            if (lastIsLeft) 0 else measuredWidth - dip(87)
        }
        float_detail_group.isVisible = unfold
        dragItemView.removeCallbacks(runnable)
    }

    @SuppressLint("SetTextI18n")
    fun refreshView() {
        isVisible = if (getPlayInfo().mPlayType != PlayType.NONE && getPlayInfo().mPlayType != PlayType.LISTENING_RADIO) {
            (attachActivity as? IgnorePlayerBar)?.isShow() ?: true
        } else false
        val avatarUrl = if (getPlayInfo().mMentorAvatar.orEmpty().contains("?") || getPlayInfo().groupId > 0) {
            getPlayInfo().mMentorAvatar
        } else {
            getPlayInfo().mMentorAvatar + "?imageMogr2/gravity/North/crop/250x250"
        }
        player_avatar_img.glideDisplay(avatarUrl, R.drawable.player_bar_default)
        player_title_tv.text = getPlayInfo().mTitle
        player_subtitle_tv.text = "${(player.getDuration() / 1000).formatTimeVideo()}  ${getPlayInfo().mSubTitle}"
        player_state_btn.isSelected = player.isPlaying()
        player_is_playing_view.isVisible = player.isPlaying()
        player_is_pause_view.isVisible = !player.isPlaying()
        player_color_bg.delegate.backgroundColor = getPlayInfo().mBackgroundColor.parseColor(Color.GRAY)
        if (getPlayInfo().mPlayType == PlayType.NONE && isVisible) unfoldPlayerBar(false)
    }

    override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
        if (!checkIsRect(event) && isVisible) unfoldPlayerBar(false)
        return super.onInterceptTouchEvent(event)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        if (oldTop < 0) {
            oldTop = measuredHeight - dragItemView.measuredHeight - dip(140)
            oldLeft = measuredWidth - dragItemView.measuredWidth
        }
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        dragItemView.let {
            val mTop = if (oldTop > 0) oldTop else 0
            it.top = mTop
            it.bottom = mTop + it.measuredHeight
            val mLeft = if (oldLeft > 0) oldLeft else 0
            it.left = mLeft
            it.right = mLeft + it.measuredWidth
        }
    }

    override fun endDrag(top: Int, left: Int) {
        oldTop = top
        oldLeft = left
        lastIsLeft = left < measuredWidth / 2
    }

    /****************************[ListenAudioView]************************************/
    override fun onCompletion() {
        refreshView()
    }

    override fun onSeekComplete() {
        refreshView()
    }

    override fun onPrepared(preparedTime: Int) {
        refreshView()
    }

    override fun onStateChange(isPlaying: Boolean) {
        refreshView()
    }

    override fun onStopPlayer() {
        refreshView()
    }

    override fun onInfo(what: Int, extra: Int) {
        if (what == PLOnInfoListener.MEDIA_INFO_BUFFERING_START) {
            /** 音频加载中 **/
            refreshView()
        } else if (what == PLOnInfoListener.MEDIA_INFO_BUFFERING_END || what == PLOnInfoListener.MEDIA_INFO_AUDIO_RENDERING_START) {
            /** 音频加载完成 **/
            refreshView()
        }
    }

    override fun onError(code: Int) {
        refreshView()
    }


}
