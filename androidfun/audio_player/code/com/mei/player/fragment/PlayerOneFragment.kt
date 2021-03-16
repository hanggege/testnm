package com.mei.player.fragment

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.TextView
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.live.player.presenter.ListenAudioView
import com.mei.base.ui.nav.Nav
import com.mei.course_service.ui.MeiWebCourseServiceActivityLauncher
import com.mei.live.ext.parseColor
import com.mei.live.manager.genderAvatar
import com.mei.orc.ActivityLauncher
import com.mei.orc.ext.dip
import com.mei.orc.imageload.glide.glideDisplay
import com.mei.orc.ui.toast.UIToast
import com.mei.orc.util.click.setOnClickNotDoubleListener
import com.mei.player.dialog.showPlayerCourseDialog
import com.mei.wood.R
import com.mei.wood.ext.AmanLink
import com.mei.wood.extensions.executeKt
import com.mei.wood.util.MeiUtil
import com.mei.wood.web.MeiWebData
import com.net.model.broadcast.PlayType
import com.net.model.chick.course.AudioGet
import com.net.network.chick.course.AudioGetRequest
import com.pili.*
import kotlinx.android.synthetic.main.player_controller_layout.*
import kotlinx.android.synthetic.main.player_one_layout.*
import launcher.Boom

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2020/7/3
 */
class PlayerOneFragment : PlayerBaseFragment() {
    override val currentTime: TextView by lazy { player_curr_time_tv }
    override val totalTime: TextView by lazy { player_total_time_tv }
    override val audioProgress: SeekBar by lazy { player_seek_bar }
    override val playBtn: View by lazy { player_state_btn }

    override val playType: String = PlayType.LISTENING_SERVICE

    @Boom
    override var playAudioId: Int = 0

    private var audioGet: AudioGet? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.player_one_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ActivityLauncher.bind(this)
        initView()
        if (playType.isPlaySelf(playAudioId)) controller.audio_id = playAudioId
        requestAudioInfo(AudioGetRequest(playAudioId))
    }

    private fun initView() {
        player_back_btn.setOnClickListener { activity?.finish() }
        goto_course_btn.setOnClickListener {
            //  2020/7/3 去课程
            audioGet?.course?.let {
                val url = MeiUtil.appendParamsUrl(AmanLink.NetUrl.course_introduce,
                        "course_id" to "${it.courseId}", "from" to "player")
                MeiWebCourseServiceActivityLauncher.startActivity(activity, MeiWebData(url, 0))
            }
        }
        document_list_layout.setOnClickListener {
            //  去文稿
            audioGet?.let {
                val url = MeiUtil.appendParamsUrl(AmanLink.NetUrl.course_words_url,
                        "course_id" to it.course.courseId.toString(),
                        "chapter_id" to it.audio.courseChaptersId.toString(),
                        "audio_id" to it.audio.id.toString(),
                        "from" to "player")
                MeiWebCourseServiceActivityLauncher.startActivity(activity, MeiWebData(url, 0))
            }
        }
        mentor_avatar_img.setOnClickListener { audioGet?.publisherInfo?.let { Nav.toPersonalPage(activity, it.userId) } }
        course_list_layout.setOnClickListener {
            // 去课程表
            audioGet?.course?.let { info ->
                activity?.showPlayerCourseDialog(info.courseId) { id ->
                    playAudioId = id
                    audioGet = null
                    player_state_btn.performClick()
                }
            }
        }
        player_pre_btn.setOnClickNotDoubleListener {
            // 请求上一条
            val get = audioGet
            val request = if (get == null) AudioGetRequest(playAudioId)
            else AudioGetRequest(get.course.courseId, playAudioId, -1)
            requestAudioInfo(request)
        }
        player_next_btn.setOnClickNotDoubleListener {
            //  请求下一条
            val get = audioGet
            val request = if (get == null) AudioGetRequest(playAudioId)
            else AudioGetRequest(get.course.courseId, playAudioId, 1)
            requestAudioInfo(request)
        }
        player_state_btn.setOnClickNotDoubleListener(500) {
            /**是播放当前id 且没有播放完成则改变播放状态**/
            if (isPlaySelf && !playerIsCompletion()) {
                controller.changePlay(!controller.isPlaying)
            } else {
                requestAudioInfo(AudioGetRequest(playAudioId))
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun refreshView(info: AudioGet) {
        player_title_tv.text = info.audio.audioTitle
        player_sub_title_tv.text = "${info.course.courseName} | 第${info.index + 1}讲"
        player_display_color_bg.delegate.backgroundColor = info.course.backgroundColor.parseColor(Color.GRAY)
        mentor_avatar_img.radius = if (info.publisherInfo.isGroup) 0f else dip(20).toFloat()
        mentor_avatar_img.glideDisplay(info.publisherInfo.avatar, info.publisherInfo.gender.genderAvatar())
        course_title_tv.text = info.course.courseName
        course_sub_title_tv.text = "${info.course.audioCount}讲 | ${info.publisherInfo.nickname}"
        document_list_layout.isVisible = info.audio.isOpenDraft > 0
        Glide.with(this)
                .load(info.course.personalImage.orEmpty())
                .placeholder(R.drawable.player_splash_default)
                .error(R.drawable.player_splash_default)
                .into(player_display_img)
    }

    private fun requestAudioInfo(request: AudioGetRequest) {
        showLoading(true)
        apiSpiceMgr.executeKt(request, success = {
            if (it.isSuccess) {
                it.data?.apply {
                    val startPosition = if (audioGet == null) this.audio.startPosition else 0
                    audioGet = this
                    playAudioId = audio.id
                    controller.audio_id = playAudioId
                    refreshView(this)
                    if (!isPlaySelf || playerIsCompletion() || player.getProgress() < 1000) {
                        /** 停止播放且拉取当前id信息进行播放 **/
                        player.start(PlayInfo.Builder()
                                .url(this.audio.audioUrl)
                                .playType(playType)
                                .channelId(this.audio.courseId)
                                .audioId(playAudioId)
                                .title(this.audio.audioTitle)
                                .subTitle(this.course.courseName)
                                .groupId(this.publisherInfo.groupId)
                                .mentorUserId(this.publisherInfo.userId)
                                .mentorAvatar(this.course.personalImage)
                                .setStartPosition(startPosition)
                                .backgroundColor(this.course.backgroundColor)
                        )
                    }
                }
                audioGet = it.data
            } else if (it.rtn == 450 && (playerIsCompletion() || player.getProgress() < 1000)) {
                /** 当前没有其它音频了，重新播放当前音频 **/
                player.start(getPlayInfo().setStartPosition(0))
            } else {
                UIToast.toast(activity, it.errMsg)
            }
        }, failure = {
            UIToast.toast(activity, it?.currMessage.orEmpty())
        }, finish = {
            showLoading(false)
        })
    }

    /****************************[ListenAudioView]************************************/
    override fun onCompletion() {
        /** 播放完成 1，如果是别的播放器完成，则马上播放当前  2，如果是播放当前，则播放下一条 **/
        if (isPlaySelf) player_next_btn.performClick()
        else player_state_btn.performClick()
    }


}