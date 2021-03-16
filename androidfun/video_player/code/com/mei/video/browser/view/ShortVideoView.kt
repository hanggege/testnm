package com.mei.video.browser.view

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.graphics.Color
import android.view.GestureDetector
import android.view.MotionEvent
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.doOnNextLayout
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.joker.im.custom.chick.SplitText
import com.mei.base.common.*
import com.mei.base.ui.nav.Nav
import com.mei.init.spiceHolder
import com.mei.live.LiveEnterType
import com.mei.live.ext.createSplitSpannable
import com.mei.live.manager.genderAvatar
import com.mei.live.ui.VideoLiveRoomActivityLauncher
import com.mei.login.checkLogin
import com.mei.login.toLogin
import com.mei.orc.Cxt
import com.mei.orc.event.bindAction
import com.mei.orc.event.postAction
import com.mei.orc.ext.dip
import com.mei.orc.ext.getStatusBarHeight
import com.mei.orc.ext.layoutInflaterKtToParentAttach
import com.mei.orc.imageload.glide.glideDisplay
import com.mei.orc.ui.toast.UIToast
import com.mei.orc.util.click.ClickMovementMethod
import com.mei.orc.util.save.getBooleanMMKV
import com.mei.orc.util.save.putMMKV
import com.mei.orc.util.string.getInt
import com.mei.video.ShortVideoPlayerNewActivity
import com.mei.video.browser.adapter.ShortVideoRefresh
import com.mei.video.browser.video.IjkAspectRatio
import com.mei.video.browser.video.doMeasure
import com.mei.video.browser.video.httpProxyCacheServer
import com.mei.video.shortVideoCompletion
import com.mei.wood.BuildConfig
import com.mei.wood.R
import com.mei.wood.extensions.executeKt
import com.mei.wood.ui.TabMainActivity
import com.net.MeiUser
import com.net.model.chick.video.ShortVideoInfo
import com.net.network.chick.video.LikeVideoRequest
import com.pili.business.toDivide
import com.pili.pldroid.player.*
import kotlinx.android.synthetic.main.item_video_layout.view.*
import kotlinx.android.synthetic.main.new_short_video_mentor_option_layout.view.*
import kotlinx.android.synthetic.main.short_video_player_complete_layout.view.*
import kotlinx.android.synthetic.main.short_video_player_new_layout.*
import tv.danmaku.ijk.media.player.IMediaPlayer

/**
 *
 * @author Created by lenna on 2020/8/18
 */
class ShortVideoView(context: Context) : ConstraintLayout(context), ShortVideoRefresh,
        PLOnPreparedListener,
        PLOnErrorListener,
        PLOnInfoListener,
        PLOnCompletionListener,
        PLOnSeekCompleteListener,
        LifecycleEventObserver {
    private var isPause: Boolean = false
    var shortVideoInfo: ShortVideoInfo.VideoInfo? = null
    private var position: Int = 0
    private var initPosition: Int = 0
    private var isPrepared = false
    private var adapterSelectedPosition = 0 //当前显示的position
    var isPositionSelected
        get() = position == adapterSelectedPosition
        set(_) {}

    /**是否点赞了视频*/
    var isLike = false

    /** 快速滑动时做了一堆无用准备导致内存增长，且滑动上百条后无法继续播放 **/
    private var delayPlayRunnable: Runnable? = null

    /**随机点赞的旋转角度*/
    var num = floatArrayOf(-35f, -25f, 0f, 25f, 35f)

    /**手势监听*/
    private val gestureDetector: GestureDetector by lazy {
        GestureDetector(context,
                object : GestureDetector.SimpleOnGestureListener() {
                    override fun onSingleTapConfirmed(e: MotionEvent?): Boolean {
                        when (short_new_video_view.playerState) {
                            PlayerState.PLAYING -> {
                                short_new_video_view.pause()
                                short_video_play_or_pause_iv.isVisible = true
                            }
                            PlayerState.PAUSED -> short_video_play_or_pause_iv.performClick()
                            else -> {
                            }
                        }
                        return super.onSingleTapConfirmed(e)
                    }

                    override fun onDoubleTap(e: MotionEvent?): Boolean {
                        e?.let { doubleClickAnimation(it) }
                        return super.onDoubleTap(e)
                    }
                })
    }

    init {
        layoutInflaterKtToParentAttach(R.layout.item_video_layout)
        setBackgroundColor(Color.parseColor("#11121C"))
    }

    override fun initPosition(initPosition: Int) {
        super.initPosition(initPosition)
        this.initPosition = position
    }

    override fun performSelected(position: Int) {
        adapterSelectedPosition = position
        if (isPositionSelected) {
            if (isPrepared) {
                short_video_play_or_pause_iv.performClick()
                refreshFollowState()
            } else if (shortVideoInfo != null) {
                initPosition = this.position
                refresh(this.position, this.shortVideoInfo)
            }
        } else {
//            short_video_play_or_pause_iv.isVisible = true
            if (short_new_video_view.isPlaying) {
                short_new_video_view.seekTo(0)
            }
            short_video_complete_info_layout.isVisible = false
        }
    }

    private fun getLifecycleOwner() = (context as? LifecycleOwner)
            ?: ((context as? ContextWrapper)?.baseContext as? LifecycleOwner)

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        getLifecycleOwner()?.lifecycle?.addObserver(this)
        getLifecycleOwner()?.bindAction<Pair<Int, Boolean>>(FOLLOW_USER_STATE) {
            if (shortVideoInfo?.userId == it?.first) {
                shortVideoInfo?.subscribe = it?.second
                refreshFollowState()
            }
        }
        shortVideoInfo?.let {
            val praiseArray = (parent?.parent as? ShortVideoPlayBrowser)?.praiseArray
            if (praiseArray != null && praiseArray.indexOfKey(it.seqId.getInt()) > -1) {
                val like = praiseArray.get(it.seqId.getInt())
                it.like = like
                isLike = like
                refreshPraiseState()
            }
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        getLifecycleOwner()?.lifecycle?.removeObserver(this)
//        short_video_play_or_pause_iv.isVisible = true
        short_video_complete_info_layout.isVisible = false
        short_video_cover_bg.isVisible = true
        isPrepared = false
        short_new_video_view.stopPlayback()
        short_video_like_iv.removeCallbacks(praiseVideoRunnable)
    }


    @SuppressLint("ClickableViewAccessibility")
    override fun refresh(position: Int, data: ShortVideoInfo.VideoInfo?) {
        if (BuildConfig.IS_TEST) {
            short_debug_info_tv.isVisible = true
            short_debug_info_tv.text = if (httpProxyCacheServer.isCached(data?.videoUrl.orEmpty())) "$position  已缓存到磁盘，等待加载到内存（测试环境可见）"
            else "$position  未缓存（测试环境可见）"
        }

        this.position = position
        shortVideoInfo = data
        this.isPrepared = false
        short_video_cover_bg.isVisible = true
        this.isPositionSelected = position == initPosition
        showLoading(false)
//        short_video_play_or_pause_iv.isVisible = true
        new_short_video_progress.progress = 0
        short_video_error_root.isVisible = false
        short_video_img.doOnNextLayout {
            if (data != null) {
                val whPair = IjkAspectRatio.DOUYIN_TYPE.doMeasure(short_video_cover_bg.measuredWidth, short_video_cover_bg.measuredHeight, data.videoWidth, data.videoHeight)
                if (whPair.first > short_video_cover_bg.measuredWidth) {
                    short_video_img.scaleX = whPair.first.toFloat() / short_video_cover_bg.measuredWidth
                } else {
                    short_video_img.scaleX = 1f
                }
                short_video_img.scaleY = short_video_img.scaleX
            }
        }

        short_video_img.glideDisplay(shortVideoInfo?.videoCoverUrl.orEmpty(), R.color.color_11121C)

        shortVideoInfo?.let {
            loadMentorInfo(it)
        }
        short_new_video_view.apply {
            plOnPreparedListener = this@ShortVideoView
            plOnInfoListener = this@ShortVideoView
            plOnErrorListener = this@ShortVideoView
            plOnSeekCompleteListener = this@ShortVideoView
            plOnCompletionListener = this@ShortVideoView

            keepScreenOn = true
            isLooping = shortVideoInfo?.canAutoReplay == true
            aspectRatio = IjkAspectRatio.DOUYIN_TYPE
        }

        short_video_play_or_pause_iv.setOnClickListener {
            short_new_video_view.start()
            it.isVisible = false
            short_video_complete_info_layout.isVisible = false
        }

        short_video_error_root.setOnClickListener {
            short_video_play_or_pause_iv.isVisible = false
            showLoading(true)
            short_new_video_view.setVideoPath(shortVideoInfo?.videoUrl.orEmpty())
            short_video_error_root.isVisible = false
        }
        /** 这里进行延迟准备播放，快速滑动时无用准备太多，内存增长，准备太多导致无法后面的播放 **/
        delayPlayRunnable?.let { short_video_error_root.removeCallbacks(delayPlayRunnable) }
        delayPlayRunnable = Runnable {
            /** 上下条可预加载 **/
            delayPlayRunnable = null
            if (position >= adapterSelectedPosition - 1 && position <= adapterSelectedPosition + 1) {
                short_video_error_root.performClick()
            }
        }
        short_video_error_root.postDelayed(delayPlayRunnable, 500)

        short_new_video_view.setOnTouchListener { _, event ->
            gestureDetector.onTouchEvent(event)
            true
        }
    }

    /**加载视频发布者信息*/
    @SuppressLint("SetTextI18n", "ClickableViewAccessibility")
    private fun loadMentorInfo(data: ShortVideoInfo.VideoInfo) {
        mentor_avatar_riv.isVisible = data.showPublisherInfo
        mentor_name_tv.isVisible = data.showPublisherInfo
        if (data.showPublisherInfo) {
            mentor_avatar_riv.glideDisplay(data.user?.avatar.orEmpty(), data.user?.gender.genderAvatar())
            mentor_name_tv.text = data.user?.nickName
        }
        living_gll.isVisible = data.living
        living_gll_extend.isVisible = data.living
        refreshFollowState()
        /**标签 start*/
        val titleList = ArrayList<SplitText>()
        if (data.showTags) {
            data.tagItems?.forEach { titleList.add(it) }
        }
        if (data.showTitle) {
            titleList.add(data.desc)
        }
        if (titleList.isNotEmpty()) {
            mentor_info_title.setOnTouchListener(ClickMovementMethod.newInstance())
            mentor_info_title.text = titleList.createSplitSpannable(Cxt.getColor(android.R.color.white), click = {
                Nav.toAmanLink(context, it)
            })
        }
        /**标签 end*/
        short_video_show_id_tv.text = "ID：${data.seqId}"
        short_video_show_id_tv.isVisible = data.seqId?.isNotEmpty() == true
        short_video_show_id_tv.updateLayoutParams<MarginLayoutParams> { topMargin = getStatusBarHeight() + dip(15) }
        short_complete_avatar_img.glideDisplay(data.user?.avatar.orEmpty(), data.user?.gender.genderAvatar())
        short_complete_name_tv.text = data.user?.nickName
        short_complete_avatar_img.setOnClickListener {
            if (!context.checkLogin()) {
                context.toLogin()
            } else {
                Nav.toAmanLink(context, data.action)
            }
        }
        short_complete_hint_tv.text = data.guiding
        val imEntranceHomepage = data.entrance?.find { it.type == "homepage" }
        short_complete_live_icon.glideDisplay(imEntranceHomepage?.icon.orEmpty(), R.drawable.short_video_complete_icon)
        short_complete_live_tv.text = imEntranceHomepage?.text ?: "Ta的主页"
        val imEntrance = data.entrance?.find { it.type == "c2c" }
        short_complete_private_im_layout.isVisible = MeiUser.getSharedUser().info?.isPublisher != true && imEntrance != null
        if (imEntrance != null) {
            short_complete_im_icon.glideDisplay(imEntrance.icon.orEmpty(), R.drawable.short_private_im_icon)
            short_complete_im_tv.text = imEntrance.text ?: "私聊"
            short_complete_private_im_layout.setOnClickListener {
                if (!context.checkLogin()) {
                    context.toLogin()
                } else {
                    imEntrance.let { info -> Nav.toAmanLink(context, info.action) }
                    statLogChat(data.seqId)
                }
            }
        }
        val imEntrancePlaying = data.entranceForPlaying?.find { it.type == "c2c" }
        to_chat_gll.isVisible = MeiUser.getSharedUser().info?.isPublisher != true && imEntrancePlaying != null
        icon_chat_iv.glideDisplay(imEntrancePlaying?.icon.orEmpty(), R.drawable.short_chat_icom)
        chat_tv.text = imEntrancePlaying?.text ?: "私聊"
        mentor_avatar_riv.setOnClickListener { gotoAction(data) }
        mentor_name_tv.setOnClickListener { gotoAction(data) }
        follow_gfl.setOnClickListener {
            if (!context.checkLogin()) {
                context.toLogin()
            } else {
                followMentor(data)
                statLogFollow(data.seqId)
            }
        }
        short_complete_mentor_home_page_layout.isVisible = data.entrance?.find { it.type == "homepage" } != null
        short_complete_mentor_home_page_layout.setOnClickListener {
            if (!context.checkLogin()) {
                context.toLogin()
            } else {
                data.entrance.orEmpty().find { it.type == "homepage" }?.let { info -> Nav.toAmanLink(context, info.action) }
            }
        }
        short_complete_replay_btn.setOnClickListener {
            if (!context.checkLogin()) {
                context.toLogin()
            } else {
                short_video_play_or_pause_iv.isVisible = false
                short_video_complete_info_layout.isVisible = false
                initPosition = position
                refresh(position, data)
            }
        }
        to_chat_gll.setOnClickListener {
            if (!context.checkLogin()) {
                context.toLogin()
            } else {
                data.entrance.orEmpty().find { it.type == "c2c" }?.let { info -> Nav.toAmanLink(context, info.action) }
                statLogChat(data.seqId)
            }
        }
        living_gll.setOnClickListener {
            /**直播中跳转到直播间*/
            if (!context.checkLogin()) {
                context.toLogin()
            } else {
                if (data.livingRoomInfo != null) {
                    VideoLiveRoomActivityLauncher.startActivity(context, data.livingRoomInfo?.roomId, LiveEnterType.course_introduce_page)
                    if (context is ShortVideoPlayerNewActivity) {
                        (context as? Activity)?.finish()
                    }
                }
            }
        }
        living_gll_extend.setOnClickListener { living_gll.performClick() }

        isLike = data.like
        refreshPraiseState()

        short_video_like_ll.isVisible = data.showLikeBtn
        short_video_like_ll.setOnClickListener {
            praiseVideo()
        }
    }

    fun praiseVideo() {
        if (!context.checkLogin()) {
            context.toLogin()
            return
        }
        isLike = !isLike
        shortVideoInfo?.like = isLike
        if (isLike) {
            startLiveButtonAnimation()
            val isFirstPraise = "is_first_praise".getBooleanMMKV(false)
            if (!isFirstPraise) {
                "is_first_praise".putMMKV(true)
                UIToast.toast(context, "赞过的内容可在我-我赞过的进行查看")
            }
        } else {
            short_video_like_iv.setImageDrawable(context.getDrawable(R.drawable.icon_praise))
        }
        postAction(PRAISE_STATE, Pair(shortVideoInfo?.seqId.getInt(), isLike))
        short_video_like_iv.removeCallbacks(praiseVideoRunnable)
        praiseVideoRunnable = Runnable {
            praiseVideoRunnable = null
            spiceHolder().apiSpiceMgr.executeKt(LikeVideoRequest(shortVideoInfo?.seqId.getInt(0), isLike), failure = {
                println("LikeVideoRequest:请求失败")
                isLike = !isLike
                refreshPraiseState()
                postAction(PRAISE_STATE, Pair(shortVideoInfo?.seqId.getInt(), isLike))
            })
        }
        short_video_like_iv.postDelayed(praiseVideoRunnable, 1500)
    }

    private var praiseVideoRunnable: Runnable? = null

    private fun refreshFollowState() {
        follow_gfl.isVisible = shortVideoInfo?.showFollowBtn == true && shortVideoInfo?.subscribe == false && shortVideoInfo?.isGroup != true
    }

    private fun refreshPraiseState() {
        short_video_like_iv.setImageDrawable(context.getDrawable(if (isLike) R.drawable.icon_praise_pre else R.drawable.icon_praise))
    }

    /**
     * 小菊花
     */
    private fun showLoading(show: Boolean) {
        if (show) {
            short_browser_progress.alpha = 0f
            short_browser_progress.animate().alpha(1f).setDuration(300).start()
        }
        short_browser_progress.isVisible = show
    }

    @SuppressLint("SetTextI18n")
    override fun onPrepared(p0: Int) {
        if (BuildConfig.IS_TEST) {
            short_debug_info_tv.text = "$position  缓存到内存完成,耗时：$p0，马上播放！（测试环境可见）"
        }
        if (isAttachedToWindow) {
            isPrepared = true
            if (isPositionSelected && !isPause) short_video_play_or_pause_iv.performClick()
            initPosition = -1
            showLoading(false)
            short_video_error_root.isVisible = false
        } else {
            short_new_video_view.stopPlayback()
        }

    }

    override fun onError(p0: Int): Boolean {
        if (isPositionSelected) {
            if (p0 != PLOnErrorListener.ERROR_CODE_IO_ERROR) {
                short_video_play_or_pause_iv.isVisible = false
                showLoading(false)
                short_video_error_root.isVisible = true
            }
        }
        return true
    }

    override fun onInfo(what: Int, p1: Int) {
        when (what) {
            IMediaPlayer.MEDIA_INFO_BUFFERING_START -> {
                /** 视频加载中 **/
                showLoading(true)
                (context as? ShortVideoPlayerNewActivity)?.short_video_cover_img?.isVisible = false

            }
            IMediaPlayer.MEDIA_INFO_BUFFERING_END,
            IMediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START -> {
                /** 视频加载完成 **/
                showLoading(false)
                short_video_play_or_pause_iv.isVisible = false
                short_video_error_root.isVisible = false
                (context as? ShortVideoPlayerNewActivity)?.short_video_cover_img?.isVisible = false
                postAction(VIDEO_RENDERING_START)
            }
            10086 -> {
                if (short_new_video_view.currentPosition > 100 && short_video_cover_bg.isVisible) {
                    short_video_cover_bg.isVisible = false
                }
                val progress = (short_new_video_view.currentPosition.times(new_short_video_progress.max)
                        / short_new_video_view.duration.toDivide()).toInt()
                new_short_video_progress.progress = progress
                val completion = short_new_video_view.currentPosition >= short_new_video_view.duration - 500
                if (shortVideoInfo?.canAutoReplay == true && completion) {
                    (context as? ShortVideoPlayerNewActivity)?.shortVideoCompletion(shortVideoInfo?.seqId.orEmpty())
                    postAction(SHORT_VIDEO_FIRST_EXIT_DETAIL, true)
                    if (context is TabMainActivity) {
                        postAction(SHORT_VIDEO_COMPLETION, shortVideoInfo?.seqId.orEmpty())
                    }
                }
                (context as? ShortVideoPlayerNewActivity)?.short_video_cover_img?.isVisible = false

                if (!isPositionSelected && short_new_video_view.isPlaying) short_new_video_view.pause()
                else if (!isAttachedToWindow) short_new_video_view.stopPlayback()
                else if (isPause) short_new_video_view.pause()
            }
        }

    }

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        when (event) {
            Lifecycle.Event.ON_RESUME -> {
                if (context is ShortVideoPlayerNewActivity) {
                    if (isPositionSelected && isPrepared) {
                        short_video_play_or_pause_iv.performClick()
                    }
                    isPause = false
                }
            }
            Lifecycle.Event.ON_PAUSE -> {
                if (context is ShortVideoPlayerNewActivity) {
                    short_video_play_or_pause_iv.isVisible = true
                    short_new_video_view.pause()
                    isPause = true
                }
            }
            Lifecycle.Event.ON_DESTROY -> {
                short_new_video_view.stopPlayback()
            }
            else -> {
            }
        }
    }

    override fun pauseVideo() {
        if (!isPause) {
            short_video_play_or_pause_iv.isVisible = true
            short_new_video_view.pause()
            isPause = true
        }
    }

    override fun playVideo() {
        if (isPause && isPositionSelected && isPrepared) {
            short_video_play_or_pause_iv.performClick()
        }
        isPause = false
    }

    override fun onCompletion() {
        short_video_play_or_pause_iv.isVisible = true
        short_video_complete_info_layout.isVisible = true
        short_new_video_view.seekTo(0)
        if (shortVideoInfo?.canAutoReplay != true) {
            (context as? ShortVideoPlayerNewActivity)?.shortVideoCompletion(shortVideoInfo?.seqId.orEmpty())
            postAction(SHORT_VIDEO_FIRST_EXIT_DETAIL, true)
            if (context is TabMainActivity) {
                postAction(SHORT_VIDEO_COMPLETION, shortVideoInfo?.seqId.orEmpty())
            }
        }

    }

    /** seekTo 后会调用开始缓存视频数据，所以，当我们seek完成后把loading隐藏*/
    override fun onSeekComplete() {
        showLoading(false)
        if (!isPositionSelected) short_new_video_view.pause()
    }


}