package com.mei.live.views

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.core.view.forEach
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.bumptech.glide.Glide
import com.mei.agora.agoraConfig
import com.mei.agora.event.AgoraEventHandler
import com.mei.agora.prepareRtcTextureVideo
import com.mei.agora.removeRtcTextureVideo
import com.mei.base.common.AUDIO_MODE_CHANGE
import com.mei.base.common.VIDEO_MODE_CHANGE
import com.mei.init.spiceHolder
import com.mei.live.ui.fragment.isUpstream
import com.mei.orc.event.postAction
import com.mei.orc.ext.dip
import com.mei.orc.ext.layoutInflaterKt
import com.mei.orc.ext.layoutInflaterKtToParentAttach
import com.mei.orc.imageload.glide.glideDisplay
import com.mei.orc.john.model.JohnUser
import com.mei.wood.R
import com.mei.wood.cache.requestUserInfo
import com.mei.wood.util.logDebug
import com.net.model.room.RoomInfo
import io.agora.rtc.IRtcEngineEventHandler
import kotlinx.android.synthetic.main.item_live_video_view.view.*
import java.util.*

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by hang on 2020-6-13
 */
class LiveVideoItemView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
        FrameLayout(context, attrs, defStyleAttr), AgoraEventHandler {

    val videoContainer: ViewGroup by lazy { live_video_surface_container }

    private val mAnchorParallelItemView by lazy {
        LiveVideoAnchorParallelItemView(context)
    }
    private val mAnchorSmallItemView by lazy {
        LiveVideoAnchorSmallItemView(context)
    }
    private val mUserParallelItemView by lazy {
        LiveVideoUserParallelItemView(context)
    }
    private val mUserSmallItemView by lazy {
        LiveVideoUserSmallItemView(context)
    }

    private val mAudioGifView by lazy {
        ImageView(context).apply {
            layoutParams = LayoutParams(dip(50), dip(25)).apply {
                topMargin = if (roomInfo.roomRole != 0 || (roomInfo.isStudio && roomInfo.groupRights and 1 != 0)) dip(84) else dip(62)
                gravity = Gravity.CENTER_HORIZONTAL
            }
            glideDisplay(R.drawable.audio_upstream_gif)
        }
    }

    private val loadingView: View by lazy {
        context.layoutInflaterKt(R.layout.live_upstream_loading_layout)
    }

    var roomInfo: RoomInfo = RoomInfo()
        set(value) {
            field = value
            live_video_info_container.forEach {
                (it as? LiveVideoItemConfig)?.setRoomInfo(value)
            }
        }

    /**
     * 0 连接中
     * 1 音频连线
     * 2 视频连线
     */
    var videoState = -1
        set(value) {
            if (field != value) {
                field = value
                if (videoState > 0) {
                    live_video_info_container.forEach {
                        (it as? LiveVideoItemConfig)?.enableVideo = videoState == 2
                    }
                }
            }
        }


    var userId: Int = View.NO_ID
        set(value) {
            field = value
            isVisible = userId > 0
        }

    var timeAccount = -1L
        set(value) {
            field = value
            if (!roomInfo.isCreator) {
                logDebug("live_video_info_container", live_video_info_container.childCount.toString())
            }
            live_video_info_container.forEach {
                (it as? LiveVideoItemConfig)?.setTimeAccount(value)
            }
        }

    private var audioEnable = false
        set(value) {
            field = value
            live_video_info_container.forEach {
                (it as? LiveVideoItemConfig)?.switchAudioState(value)
            }
        }

    var state: Int = 1
        set(value) {
            field = value
            if (roomInfo.roomId.isNotEmpty() && userId > 0) {
                initUserState()
            }
        }

    init {
        layoutInflaterKtToParentAttach(R.layout.item_live_video_view)
        agoraConfig.bindView(this)
        (context as? FragmentActivity)?.lifecycle?.addObserver(object : LifecycleEventObserver {
            override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                if (event == Lifecycle.Event.ON_DESTROY) {
                    agoraConfig.unBindView(this@LiveVideoItemView)
                }
            }
        })
        isVisible = false
    }

    /**
     * state
     * 0 -> 并列
     * 1 -> 放大
     * 2 -> 缩小
     */
    private fun initUserState() {
        live_video_info_container.apply {
            forEach {
                it.clearAnimation()
                endViewTransition(it)
                removeView(it)
            }
        }
        if (roomInfo.isCreator || (!context.isUpstream() && (roomInfo.roomRole == 2 || (roomInfo.isStudio && roomInfo.groupRights and 1 != 0) || roomInfo.groupRights and 8 != 0))) {
            when (state) {
                0 -> {
                    live_video_info_container.addView(mAnchorParallelItemView.apply {
                        refreshConfig()
                    })
                    if (mAnchorParallelItemView.mUserId == userId && userId > 0 && videoState == 2 && roomInfo.isCreator) {
                        mAnchorParallelItemView.showDisableVideoHintPop()
                    }
                }
                2 -> {
                    live_video_info_container.addView(mAnchorSmallItemView.apply {
                        refreshConfig()
                    })
                    if (mAnchorSmallItemView.mUserId == userId && userId > 0 && videoState == 2 && roomInfo.isCreator) {
                        mAnchorSmallItemView.showDisableVideoHintPop()
                    }
                }
            }
        } else {
            when (state) {
                0 -> {
                    live_video_info_container.addView(mUserParallelItemView.apply {
                        refreshConfig()
                    })
                    if (mUserParallelItemView.mUserId == userId && userId > 0 && videoState == 2) {
                        mUserParallelItemView.showDisableVideoHintPop()
                    }
                }
                2 -> {
                    live_video_info_container.addView(mUserSmallItemView.apply {
                        refreshConfig()
                    })
                    if (mUserSmallItemView.mUserId == userId && userId > 0 && videoState == 2) {
                        mUserSmallItemView.showDisableVideoHintPop()
                    }
                }
            }
        }
    }

    private fun LiveVideoItemConfig.refreshConfig() {
        enableVideo = videoState == 2 || videoState == -1
        setRoomInfo(roomInfo)
        setTimeAccount(timeAccount)
        setUserId(userId)
        switchAudioState(audioEnable)
    }

    /**
     * 进行接收远程的视频流
     */
    fun addSurfaceView(uid: Int, state: Int = videoState) {
        if ((uid != JohnUser.getSharedUser().userID && userId != uid) || (state != videoState && videoState != 2)) {
            timeAccount = 1
            userId = uid
            videoState = if (uid == roomInfo.createUser?.userId) 2 else state
            videoContainer.apply {
                clearAnimation()
                forEach {
                    it.clearAnimation()
                    endViewTransition(it)
                    removeView(it)
                }
                when (videoState) {
                    0 -> {
                        setBackgroundResource(0)
                        (loadingView.parent as? ViewGroup)?.removeView(loadingView)
                        addView(loadingView)
                    }
                    1 -> {
                        setBackgroundResource(R.drawable.default_audio_upstream)
                        (mAudioGifView.parent as? ViewGroup)?.removeView(mAudioGifView)
                        addView(mAudioGifView)
                    }
                    2 -> {
                        setBackgroundResource(0)
                        val videoView = agoraConfig.prepareRtcTextureVideo(context, uid)
                        addView(videoView)
                    }
                }
            }
            refreshUserInfo()
        }
    }


    fun disableVideo() {
        videoState = 1
        state = 2
        videoContainer.removeAllViews()
        videoContainer.setBackgroundResource(R.drawable.default_audio_upstream)
        (mAudioGifView.parent as? ViewGroup)?.removeView(mAudioGifView)
        videoContainer.addView(mAudioGifView.apply {
            glideDisplay(R.drawable.audio_upstream_gif)
        })
    }

    /**
     * 远程用户下麦
     */
    fun checkUserOffline(uid: Int) {
        if (userId == uid) {
            logDebug("------------checkUserOffline", "uid:$uid")
            timeAccount = -1
            videoState = -1
            agoraConfig.removeRtcTextureVideo(uid)
            videoContainer.removeAllViews()
            live_video_info_container.removeAllViews()
            userId = View.NO_ID

            audioEnable = false

            postAction(AUDIO_MODE_CHANGE, Pair(JohnUser.getSharedUser().userID, 0))

            Glide.with(context).clear(mAudioGifView)
        }
    }

    fun refreshUserInfo() {
        spiceHolder().apiSpiceMgr.requestUserInfo(arrayOf(userId)) {
            initUserState()
        }
    }


    /****************************[AgoraEventHandler]************************************/
    override fun onUserMuteAudio(uid: Int, muted: Boolean) {
        if (userId == uid) {
            audioEnable = muted
        }
    }

    private val volumeLinkedList = LinkedList<IRtcEngineEventHandler.AudioVolumeInfo>()

    override fun onAudioVolumeIndication(speakers: Array<IRtcEngineEventHandler.AudioVolumeInfo>?, totalVolume: Int) {
        if (userId != roomInfo.createUser?.userId && videoState == 1) {
            val joinToString = speakers.orEmpty().joinToString(transform = { info ->
                "uid:${info.uid}---volume:${info.volume}---vad:${info.vad}"
            })
            logDebug("onAudioVolumeIndication", joinToString)
            speakers.orEmpty().forEach {
                if ((userId == JohnUser.getSharedUser().userID && it.uid == 0) || userId == it.uid) {
                    volumeLinkedList.add(it)
                    if (volumeLinkedList.size > 4) {
                        volumeLinkedList.removeAt(0)
                    }
                }
            }
            val string = volumeLinkedList.joinToString(transform = { info ->
                "uid:${info.uid}---volume:${info.volume}---vad:${info.vad}"
            })
            logDebug("onAudioVolumeIndication", string)
            if (volumeLinkedList.any { it.volume > 50 }) {
                mAudioGifView.glideDisplay(R.drawable.audio_upstream_gif)
            } else {
                mAudioGifView.glideDisplay(R.drawable.audio_upstream)
            }
        }
    }
}

interface LiveVideoItemConfig {
    var enableVideo: Boolean
    fun setUserId(userId: Int)
    fun setRoomInfo(roomInfo: RoomInfo)
    fun setTimeAccount(timeAccount: Long)
    fun switchAudioState(isEnable: Boolean)
    fun disableVideoMode(userId: Int) {
        postAction(VIDEO_MODE_CHANGE, Pair(userId, 1))
    }
}