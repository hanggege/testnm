package com.mei.radio

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.LinearLayoutManager
import com.mei.base.common.MUTE_REMOTE_AUDIO_STREAMS
import com.mei.orc.event.postAction
import com.mei.orc.ext.dip
import com.mei.orc.ext.getStatusBarHeight
import com.mei.orc.util.click.setOnClickNotDoubleListener
import com.mei.player.fragment.PlayerBaseFragment
import com.mei.radio.adapter.ListeningUserAdapter
import com.mei.radio.ext.*
import com.mei.radio.tool.AdvanceCountDownTimer
import com.mei.wood.R
import com.net.model.broadcast.PlayType
import com.net.model.radio.RadioDetailInfo
import com.net.model.user.UserInfo
import com.opensource.svgaplayer.SVGAVideoEntity
import com.pili.PlayInfo
import com.pili.getPlayInfo
import com.pili.player
import com.pili.playerIsCompletion
import com.pili.pldroid.player.PLOnInfoListener
import kotlinx.android.synthetic.main.fragment_radio.*
import java.util.concurrent.atomic.AtomicInteger

/**
 * RadioFragment
 *
 * @author kun
 * @version 1.0.0
 * @since 2020-09-21
 * 电台页面
 */
class RadioFragment : PlayerBaseFragment() {
    var selectChannel: RadioDetailInfo.Channels? = null
    val listeningList = arrayListOf<UserInfo>()
    var mDetailData: RadioDetailInfo? = null
    val listeningAdapter by lazy {
        ListeningUserAdapter(listeningList)
    }

    var likeEntry: SVGAVideoEntity? = null // 喜欢动画

    var isFavoriteListClick = false
    var curFavoriteAudioId = AtomicInteger(0)
    val favoriteInfo by lazy {
        RadioDetailInfo.Channels().apply {
            channelId = 0
            name = "我喜欢的"
            cover = mDetailData?.myLikeCover
            coverBlur = mDetailData?.myLikeCoverBlur
        }
    }

    var defaultScheduleId = ""
    var isAudioCompleted = false //区分 点击下一首 和 剩余两首->一首 得情况
    var remainAudioCount: Int = -1 // 剩余几首
    var countDownTimer: AdvanceCountDownTimer? = null //分钟倒计时
    var startPlayVoiceTime: Long = 0
    var audioStartPlayTime: Long = 0

    override val currentTime: TextView by lazy { TextView(context) }
    override val totalTime: TextView by lazy { TextView(context) }
    override val audioProgress: SeekBar by lazy { SeekBar(context) }
    override val playBtn: View by lazy { radio_play }
    override val playType: String
        get() = PlayType.LISTENING_RADIO
    override var playAudioId: Int = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_radio, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initBottomListener()
        requestDetailData()
    }


    private fun initView() {
        startPlayVoiceTime = System.currentTimeMillis()
        audioStartPlayTime = System.currentTimeMillis()
        radio_empty_page.setBtnClickListener(View.OnClickListener {
            requestDetailData()
        })
        radio_back_fl.setOnClickListener {
            activity?.finish()
        }
        //正在听的用户view
        val layoutManager = LinearLayoutManager(context)
        layoutManager.orientation = LinearLayoutManager.HORIZONTAL
        listening_rlv.layoutManager = layoutManager
        listening_rlv.adapter = listeningAdapter
        //轮盘和文本选择器
        view_motion_dispatch.addView(cycle_text_list)
        view_motion_dispatch.addView(oval_scale_view)
        radio_back_fl.updateLayoutParams<ConstraintLayout.LayoutParams> {
            topMargin = getStatusBarHeight()
        }
        val barHeight = getStatusBarHeight().toFloat() + dip(20)
        cycle_text_list.translationY = barHeight
        oval_scale_view.translationY = barHeight
        iv_voice_cover.translationY = barHeight
        cycle_text_list.setTextClickScrollListener {
            if (isAdded) {
                oval_scale_view.onScrollEvent(it)
            }
        }
        cycle_text_list.setTextSelectedListener { selectTitle ->
            //频道选择
            if (isAdded) {
                if (isFavoriteListClick) {
                    isFavoriteListClick = false
                    return@setTextSelectedListener
                }
                //上报上一个播放的音频播放信息
                if (selectChannel != null) {
                    //上报切换前播放的音频
                    radioPlayVoiceReport(mDetailData?.current?.channelId ?: 0,
                            mDetailData?.current?.audioId ?: 0,
                            player.getProgress(),
                            player.getProgress() == mDetailData?.current?.duration)
                    /**更换被选中的频道*/
                    selectChannel = mDetailData?.channels?.list?.find { it.name == selectTitle }
                    loadBg(selectChannel?.coverBlur.orEmpty(), selectChannel?.cover.orEmpty())
                    /**请求播放当前频道音频，切换专辑时 不传audioId*/
                    requestNextAudio(selectChannel?.channelId ?: 0, 0, RadioPlayMode.SWITCH.type)
                } else {
                    //第一次进入电台被定为到被选中的频道，不需要自动播放音频
                    selectChannel = mDetailData?.channels?.list?.find { it.name == selectTitle }
                    loadBg(selectChannel?.coverBlur.orEmpty(), selectChannel?.cover.orEmpty())
                }


            }

        }
        radio_play_or_pause_v.setOnClickNotDoubleListener(500, "playRadio") {
            if (player.getPlayInfo().url() == mDetailData?.welcomeAudio) {
                return@setOnClickNotDoubleListener
            }
            /**是播放当前id 且没有播放完成则改变播放状态**/
            if (isAdded) {
                if (isPlaySelf && !playerIsCompletion()) {
                    controller.changePlay(!controller.isPlaying)
                } else {
                    playAudioId = mDetailData?.current?.audioId ?: 0
                    controller.audio_id = playAudioId
                    player.start(PlayInfo.Builder()
                            .url(mDetailData?.current?.url.orEmpty())
                            .playType(playType)
                            .channelId(selectChannel?.channelId ?: 0)
                            .setStartPosition((mDetailData?.current?.playTime ?: 0 / 1000).toInt())
                            .audioId(playAudioId)
                            .title(mDetailData?.current?.title.orEmpty()))

                    if (remainAudioCount == 1) {
                        setCountDownTimer(mDetailData?.current?.duration ?: 0)
                    }
                }

                statRadioClickEvent(selectChannel?.name.orEmpty()
                        , mDetailData?.current?.title.orEmpty()
                        , if (controller.isPlaying) "播放" else "暂停")
            }

        }
        radio_next.setOnClickNotDoubleListener(500, "playNext") {
            if (player.getPlayInfo().url() == mDetailData?.welcomeAudio) {
                return@setOnClickNotDoubleListener
            }
            /**请求播放当前频道下一首音频*/
            requestNextAudio(mDetailData?.current?.channelId ?: 0, mDetailData?.current?.audioId
                    ?: 0, RadioPlayMode.SWITCH.type)

            statRadioClickEvent(selectChannel?.name.orEmpty(), mDetailData?.current?.title.orEmpty(), "下一首")
        }


    }

    override fun onInfo(what: Int, extra: Int) {
        super.onInfo(what, extra)
        if (what == PLOnInfoListener.MEDIA_INFO_BUFFERING_START) {
            /** 音频加载中 **/
            if (isAdded) {
                startLoading()
            }
        } else if (what == PLOnInfoListener.MEDIA_INFO_BUFFERING_END || what == PLOnInfoListener.MEDIA_INFO_AUDIO_RENDERING_START) {
            /** 音频加载完成 **/
            if (isAdded) {
                startPlayVoiceTime = System.currentTimeMillis()
                stopLoading()
            }
        }
    }

    override fun onStateChange(isPlaying: Boolean) {
        super.onStateChange(isPlaying)
        if (isPlaying) {
            countDownTimer?.resume()
        } else {
            countDownTimer?.pause()
        }
    }


    override fun onCompletion() {
        //播放欢迎语时页面不能触动
        if (mDetailData?.welcomeAudio.orEmpty().isNotEmpty()) {
            /** 判断是否是自动播放 播放完欢迎语后直接播放当前频道的当前音频*/
            if (mDetailData?.autoPlay == true) {
                playAudioId = mDetailData?.current?.audioId ?: 0
                controller.audio_id = playAudioId
                player.start(PlayInfo.Builder()
                        .url(mDetailData?.current?.url.orEmpty())
                        .playType(playType)
                        .channelId(selectChannel?.channelId ?: 0)
                        .setStartPosition((mDetailData?.current?.playTime ?: 0 / 1000).toInt())
                        .audioId(playAudioId)
                        .title(mDetailData?.current?.title.orEmpty()))

                if (remainAudioCount == 1) {
                    setCountDownTimer(mDetailData?.current?.duration ?: 0)
                }
            }

        } else {
            //定时
            if (remainAudioCount == 1) {
                controller.changePlay(false)
                remainAudioCount = -1
                radioPlayVoiceReport(mDetailData?.current?.channelId ?: 0,
                        mDetailData?.current?.audioId ?: 0,
                        player.getProgress(),
                        true)
                return
            } else if (remainAudioCount > 1) {
                remainAudioCount--
                isAudioCompleted = true
            }
            //播放完成监听
            /**请求播放当前频道下一首音频*/
            requestNextAudio(selectChannel?.channelId ?: 0, mDetailData?.current?.audioId
                    ?: 0, RadioPlayMode.SWITCH.type)

            radioPlayVoiceReport(mDetailData?.current?.channelId ?: 0,
                    mDetailData?.current?.audioId ?: 0,
                    player.getProgress(),
                    true)
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        radioPlayVoiceReport(mDetailData?.current?.channelId ?: 0,
                mDetailData?.current?.audioId ?: 0,
                player.getProgress(),
                true)
        if (getPlayInfo().mPlayType == PlayType.LISTENING_RADIO && player.isPlaying()) {
            player.stop()
            postAction(MUTE_REMOTE_AUDIO_STREAMS, false)
        }
    }


}