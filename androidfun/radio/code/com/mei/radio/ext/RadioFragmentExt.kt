package com.mei.radio.ext

import android.graphics.drawable.Drawable
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.mei.GrowingUtil
import com.mei.login.checkLogin
import com.mei.orc.imageload.glide.glideDisplay
import com.mei.orc.john.model.JohnUser
import com.mei.orc.ui.toast.UIToast
import com.mei.orc.unit.TimeUnit
import com.mei.orc.util.click.setOnClickNotDoubleListener
import com.mei.radio.RadioFragment
import com.mei.radio.RadioPlayMode
import com.mei.radio.showRadioFavoriteDialog
import com.mei.radio.showRadioScheduleDialog
import com.mei.radio.tool.AdvanceCountDownTimer
import com.mei.wood.R
import com.mei.wood.extensions.executeKt
import com.net.model.radio.RadioSchedulingInfo
import com.net.network.radio.*
import com.opensource.svgaplayer.SVGACallback
import com.opensource.svgaplayer.SVGADrawable
import com.opensource.svgaplayer.SVGAParser
import com.opensource.svgaplayer.SVGAVideoEntity
import com.pili.PlayInfo
import com.pili.player
import kotlinx.android.synthetic.main.fragment_radio.*
import kotlin.math.round

var firstOpen: Boolean = true

/**
 *
 * @author Created by lenna on 2020/9/23
 */
fun RadioFragment.initBottomListener() {
    radio_favorite.setOnClickNotDoubleListener(500, tag = "radio_favorite") {
        if (player.getPlayInfo().url() == mDetailData?.welcomeAudio) {
            return@setOnClickNotDoubleListener
        }
        likeRadioOrNot()
        statRadioClickEvent(selectChannel?.name.orEmpty(), mDetailData?.current?.title.orEmpty(), "点赞")
    }
    radio_favorite_unselected.callback = object : SVGACallback {
        override fun onFinished() {
            if (mDetailData?.current?.audioId == curFavoriteAudioId.get() && isAdded) {
                radio_favorite_unselected.glideDisplay(R.drawable.ic_radio_favorite_selected)
            }
        }

        override fun onPause() {

        }

        override fun onRepeat() {

        }

        override fun onStep(frame: Int, percentage: Double) {

        }
    }

    radio_matter_img.setOnClickNotDoubleListener(500, tag = "radio_matter") {
        if (player.getPlayInfo().url() == mDetailData?.welcomeAudio) {
            return@setOnClickNotDoubleListener
        }
        mDetailData?.scheduling?.let {
            if (defaultScheduleId != "") {
                it.defaultTab = defaultScheduleId
            }
            activity?.showRadioScheduleDialog(it) { scheduleInfo ->
                handleSchedule(scheduleInfo)
                statRadioClickEvent(selectChannel?.name.orEmpty(), mDetailData?.current?.title.orEmpty(), "定时-${scheduleInfo.title}")
            }
        }
        statRadioClickEvent(selectChannel?.name.orEmpty(), mDetailData?.current?.title.orEmpty(), "定时")
    }
    radio_matter_duration.setOnClickListener { radio_matter_img.performClick() }
    radio_my_favorite.setOnClickNotDoubleListener(500, tag = "radio_favorite_list") {
        if (player.getPlayInfo().url() == mDetailData?.welcomeAudio) {
            return@setOnClickNotDoubleListener
        }
        if (context.checkLogin()) {
            showLoading(true)
            apiSpiceMgr.executeKt(RadioFavoriteListRequest(), success = {
                if (it.isSuccess && !it.data.list.isNullOrEmpty()) {
                    activity?.showRadioFavoriteDialog(it.data.list, playAudioId) { audioInfo ->
                        val curChannelId = selectChannel?.channelId
                        selectChannel = mDetailData?.channels?.list?.find { channels -> channels.channelId == 0 }
                        if (curChannelId != 0) {
                            isFavoriteListClick = true
                            loadBg(selectChannel?.coverBlur.orEmpty(), selectChannel?.cover.orEmpty())
                        }
                        cycle_text_list.setCurrentTitle(favoriteInfo.name)
                        requestNextAudio(0, audioInfo.audioId, RadioPlayMode.LIKE.type)
                        statRadioClickEvent(selectChannel?.name.orEmpty(), audioInfo.title.orEmpty(), "我喜欢的-${audioInfo.title}")
                    }
                }
            }, finish = {
                showLoading(false)
            })
            statRadioClickEvent(selectChannel?.name.orEmpty(), mDetailData?.current?.title.orEmpty(), "我喜欢的")
        }
    }
}

fun RadioFragment.handleSchedule(scheduleInfo: RadioSchedulingInfo.RadioScheduleBean) {
    val count = scheduleInfo.stopAfterPlayAudios
    val seconds = scheduleInfo.stopAfterPlaySeconds
    defaultScheduleId = scheduleInfo.id
    when {
        count == 0 && seconds == 0 -> {
            radio_matter_img.isSelected = false
            radio_matter_duration.isVisible = false
            remainAudioCount = -1
            countDownTimer?.cancel()
        }
        count != 0 -> {
            if (count == 1) {
                var remainTime = player.getDuration() - player.getProgress()
                if (remainTime < TimeUnit.SECOND) remainTime = player.getDuration()
                setCountDownTimer(remainTime / TimeUnit.SECOND)
                if (!player.isPlaying()) countDownTimer?.pause()
            } else {
                radio_matter_img.isSelected = true
                radio_matter_duration.isVisible = false
                countDownTimer?.cancel()
            }
            remainAudioCount = count
            reportRadioSchedule(scheduleInfo.id)
        }
        seconds > 0 -> {
            remainAudioCount = -1
            setCountDownTimer(seconds.toLong())
            if (!player.isPlaying()) countDownTimer?.pause()
            reportRadioSchedule(scheduleInfo.id)
        }
        else -> {
            remainAudioCount = -1
            radio_matter_img.isSelected = false
            radio_matter_duration.isVisible = false
        }
    }
}

fun RadioFragment.setCountDownTimer(duration: Long) {
    radio_matter_img.isSelected = true
    radio_matter_duration.isVisible = true
    radio_matter_duration.text = (duration * TimeUnit.SECOND).formatRadioCountDown()
    countDownTimer?.cancel()
    countDownTimer = object : AdvanceCountDownTimer(duration * TimeUnit.SECOND, TimeUnit.SECOND) {
        override fun onTick(millisUntilFinished: Long) {
            val restSeconds = (round(millisUntilFinished * 1F / TimeUnit.SECOND) * TimeUnit.SECOND).toLong()
            if (isAdded) {
                radio_matter_duration.text = restSeconds.formatRadioCountDown()
            }
        }

        override fun onFinish() {
            if (isAdded) {
                radio_matter_img.isSelected = false
                radio_matter_duration.isVisible = false
            }
            controller.changePlay(false)
            defaultScheduleId = "NONE"
            remainAudioCount = -1
        }
    }
    countDownTimer?.start()
}

fun RadioFragment.reportRadioSchedule(id: String) {
    apiSpiceMgr.executeKt(RadioScheduleReportRequest(id))
}

fun RadioFragment.likeRadioOrNot() {
    val radioId = playAudioId
    val liked = mDetailData?.current?.liked ?: false
    view_motion_dispatch.isIntercept = true
    apiSpiceMgr.executeKt(RadioLikeRequest(radioId, liked), success = {
        if (it.isSuccess && radioId == playAudioId) {
            if (liked) {
                setDisLikeStyle()
            } else {
                curFavoriteAudioId.set(radioId)
                setLikeStyle()
            }
            mDetailData?.current?.liked = !liked
            val showFavoriteList = it.data.showMyLike && JohnUser.getSharedUser().hasLogin()
            if (radio_my_favorite.isInvisible && showFavoriteList) {
                mDetailData?.channels?.list?.let { list ->
                    if (list.last().channelId == 0) return@let
                    list.add(list.size, favoriteInfo)
                    cycle_text_list.setTitleWithLocation(list.map { channels -> channels.name })
                }
            } else if (!radio_my_favorite.isInvisible && !showFavoriteList) {
                mDetailData?.channels?.list?.let { list ->
                    if (list.last().channelId != 0) return@let
                    list.removeLast()
                    if (selectChannel?.channelId == 0) {
                        cycle_text_list.setTitleWithLocation(list.map { channels -> channels.name }, list[0].name)
                    } else {
                        cycle_text_list.setTitleWithLocation(list.map { channels -> channels.name })
                    }
                }
            }
            radio_my_favorite.isInvisible = !showFavoriteList
        }
    }, finish = {
        view_motion_dispatch.isIntercept = false
    })
}

private fun RadioFragment.setDisLikeStyle() {
    radio_favorite_unselected.glideDisplay(R.drawable.ic_radio_favorite_unselected)
}

private fun RadioFragment.setLikeStyle() {
    if (likeEntry != null) {
        playLikeAnimation()
    } else {
        loadLikeEffect()
    }
}

private fun RadioFragment.playLikeAnimation() {
    likeEntry?.apply {
        val drawable = SVGADrawable(this)
        radio_favorite_unselected.setImageDrawable(drawable)
        radio_favorite_unselected.startAnimation()
    }
}

private fun RadioFragment.loadLikeEffect() {
    SVGAParser(context).decodeFromAssets("effect_radio_like.svga", object : SVGAParser.ParseCompletion {
        override fun onComplete(videoItem: SVGAVideoEntity) {
            likeEntry = videoItem
            playLikeAnimation()
        }

        override fun onError() {

        }
    })
}

fun RadioFragment.requestDetailData() {
    showLoading(true)
    apiSpiceMgr.executeKt(RadioDetailRequest(firstOpen), success = {
        if (it.isSuccess) {
            if (isAdded)
                radio_empty_page.isVisible = false
            mDetailData = it.data
            loadRadioDetailData()
        } else {
            if (isAdded)
                radio_empty_page.isVisible = true
        }

    }, failure = {
        if (isAdded)
            radio_empty_page.isVisible = true
    }, finish = {
        showLoading(false)
        firstOpen = false
    })
}

/**
 * 切换专辑时不传audioId
 */
fun RadioFragment.requestNextAudio(channelId: Int, audioId: Int, direction: Int) {
    startLoading()
    apiSpiceMgr.executeKt(RadioNextAudioInfoRequest(channelId, audioId, direction), success = {
        if (it.isSuccess) {
            if (isAdded) {
                mDetailData?.current = it.data
                voice_name_tv.text = mDetailData?.current?.title.orEmpty()

                initFavoriteStyle()
                //播放音频
                playAudioId = mDetailData?.current?.audioId ?: 0
                controller.audio_id = playAudioId
                /** 停止播放且拉取当前id信息进行播放 **/
                player.start(PlayInfo.Builder()
                        .url(mDetailData?.current?.url.orEmpty())
                        .playType(playType)
                        .channelId(channelId)
                        .setStartPosition((mDetailData?.current?.playTime ?: 0 / 1000).toInt())
                        .audioId(playAudioId)
                        .title(mDetailData?.current?.title.orEmpty()))

                if (remainAudioCount == 1 && !isAudioCompleted) {
                    isAudioCompleted = false
                    setCountDownTimer(it.data.duration)
                } else {
                    countDownTimer?.resume()
                }

                //切换频道时统计
                if (audioId == 0) {
                    statRadioClickEvent(selectChannel?.name.orEmpty(), mDetailData?.current?.title.orEmpty(), "")
                }

            }


        } else {
            if (it.rtn == 404) {
                //频道有变动
                requestDetailData()
            } else {
                UIToast.toast(it.errMsg)
            }
            stopLoading()
        }
    }, failure = {
        UIToast.toast(it?.currMessage.orEmpty())
        stopLoading()
    })

}

fun RadioFragment.loadRadioDetailData() {
    if (isAdded) {
        //title
        radio_title_tv.text = mDetailData?.title.orEmpty()
        //正在听的用户
        listening_rlv.isVisible = mDetailData?.listeners?.list?.isNotEmpty() == true
        listening_count_tv.isVisible = mDetailData?.listeners?.tips.orEmpty().isNotEmpty() == true
        listeningList.clear()
        listeningList.addAll(mDetailData?.listeners?.list ?: arrayListOf())
        listeningAdapter.notifyDataSetChanged()
        listening_count_tv.text = mDetailData?.listeners?.tips.orEmpty()
        voice_name_tv.text = mDetailData?.current?.title.orEmpty()
        //加载频道
        var showFavoriteList = false
        if (mDetailData?.channels?.list?.isNotEmpty() == true
                && mDetailData?.channels?.list?.size ?: 0 >= 5) {
            val titles = arrayListOf<String>()
            mDetailData?.channels?.list?.forEach {
                titles.add(it.name.orEmpty())
                if (it.channelId == 0) {
                    it.cover = mDetailData?.myLikeCover
                    it.coverBlur = mDetailData?.myLikeCoverBlur
                }
                if (!showFavoriteList && it.channelId == 0) {
                    showFavoriteList = true
                }
            }
            cycle_text_list.setTitles(titles)
            cycle_text_list.setCurrentTitle(mDetailData?.channels?.list?.find { it.channelId == mDetailData?.current?.channelId ?: 0 }?.name.orEmpty())
            cycle_text_list.isVisible = true
        } else {
            cycle_text_list.isVisible = false
        }

        radio_my_favorite.isInvisible = showFavoriteList.not() && JohnUser.getSharedUser().hasLogin()
        initFavoriteStyle()

        //是否加载时间表
        radio_matter.isVisible = mDetailData?.scheduling != null

        if (mDetailData?.welcomeAudio.orEmpty().isNotEmpty()) {
            /**欢迎语音频*/
            startWelcomePlayVoice()
        } else {
            // 是否自动播放播放当前音频
            if (mDetailData?.autoPlay == true) {
                playAudioId = mDetailData?.current?.audioId ?: 0
                controller.audio_id = playAudioId
                /** 没有欢迎语或者今天已经播放过一次了， 停止播放且拉取当前id信息进行播放 **/
                player.start(PlayInfo.Builder()
                        .url(mDetailData?.current?.url.orEmpty())
                        .playType(playType)
                        .channelId(mDetailData?.current?.channelId ?: 0)
                        .setStartPosition((mDetailData?.current?.playTime ?: 0 / 1000).toInt())
                        .audioId(playAudioId)
                        .title(mDetailData?.current?.title.orEmpty()))

                if (remainAudioCount == 1 && !isAudioCompleted) {
                    isAudioCompleted = false
                    setCountDownTimer(mDetailData?.current?.duration ?: 0)
                }
            }

        }

    }
}

fun RadioFragment.initFavoriteStyle() {
    val isLike = mDetailData?.current?.liked
    if (isLike == null) {
        radio_favorite_unselected.isVisible = false
    } else {
        radio_favorite_unselected.isVisible = true
        radio_favorite_unselected.glideDisplay(if (isLike) R.drawable.ic_radio_favorite_selected else R.drawable.ic_radio_favorite_unselected)
    }
}


/**
 * 加载电台背景和封面
 */
fun RadioFragment.loadBg(url: String, voiceCoverUrl: String) {
    //背景
    Glide.with(radio_bg_image_switcher)
            .asDrawable()
            .load(if (url.isNotEmpty()) url else R.drawable.voice_private_radio_default_bg)
            .into(object : CustomTarget<Drawable>() {
                override fun onLoadCleared(placeholder: Drawable?) {

                }

                override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
                    if (isAdded) {
                        radio_bg_image_switcher.setImageDrawable(resource)
                    }
                }

            })

    //右边音频封面
    Glide.with(iv_voice_cover)
            .asDrawable()
            .load(if (voiceCoverUrl.isNotEmpty()) voiceCoverUrl else R.drawable.voice_private_radio_default_bg)
            .apply(RequestOptions.circleCropTransform())
            .into(object : CustomTarget<Drawable>() {
                override fun onLoadCleared(placeholder: Drawable?) {

                }

                override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
                    if (isAdded) {
                        iv_voice_cover.setImageDrawable(resource)
                    }
                }

            })


}

fun RadioFragment.radioPlayVoiceReport(channelId: Int, audioId: Int, playTime: Long, completed: Boolean) {
    // playTime  播放当前进度改为单位秒
    apiSpiceMgr.executeKt(RadioReportRequest(channelId, audioId, playTime / 1000, completed))
    statRadioVoicePlayTime(selectChannel?.name.orEmpty(), mDetailData?.current?.title.orEmpty(), (System.currentTimeMillis() - audioStartPlayTime) / 1000)
    audioStartPlayTime = System.currentTimeMillis()
}

fun RadioFragment.startLoading() {
    if (isAdded) {
        if (!radio_voice_loading.isVisible) {
            radio_voice_loading.isVisible = true
            radio_voice_loading.start()
        }
        if (radio_play.isVisible)
            radio_play.isVisible = false
    }
}

fun RadioFragment.stopLoading() {
    if (isAdded) {
        radio_voice_loading.isVisible = false
        radio_voice_loading.stop()
        radio_play.isVisible = true
    }
}

private fun Long?.formatRadioCountDown(): String {
    if (this == null) return ""
    var time = this
    val result = StringBuilder()
    if (this > TimeUnit.DAY) throw IllegalArgumentException("time$this")
    if (this < 0) time = 0
    val hour = (time / TimeUnit.HOUR).toInt()
    if (hour > 0) {
        result.append("${String.format("%02d", hour)}:")
        time -= hour * TimeUnit.HOUR
    }

    val min = (time / TimeUnit.MINUTE).toInt()
    result.append("${String.format("%02d", min)}:")
    time -= min * TimeUnit.MINUTE

    val seconds = (time / TimeUnit.SECOND).toInt()
    result.append(String.format("%02d", seconds))
    return result.toString()
}

/**欢迎语播放*/
fun RadioFragment.startWelcomePlayVoice() {
    /** 停止播放且拉取当前id信息进行播放 播放欢迎语**/
    player.start(PlayInfo.Builder()
            .url(mDetailData?.welcomeAudio.orEmpty())
            .playType(playType)
            .channelId(0)
            .setStartPosition(0)
            .audioId(-1)
            .title(""))
}


/**电台统计*/
fun statRadioClickEvent(channelName: String, audioName: String, clickContent: String) = try {
    GrowingUtil.track("common_page_click", "page_name", "电台页",
            "page_type", channelName,
            "click_type", audioName,
            "click_content", clickContent,
            "time_stamp", "${System.currentTimeMillis() / 1000}")
} catch (e: Exception) {
    e.printStackTrace()
}


/**音频播放时长统计*/
fun RadioFragment.statRadioVoicePlayTime(channelName: String, audioName: String, time: Long) = try {
    GrowingUtil.track("common_video_time", "page_name", "电台页",
            "page_type", channelName,
            "click_type", audioName,
            "time", time.toString(),
            "time_stamp", "${startPlayVoiceTime / 1000}")
} catch (e: Exception) {
    e.printStackTrace()
}