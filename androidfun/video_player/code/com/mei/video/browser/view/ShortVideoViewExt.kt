package com.mei.video.browser.view

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.TimeInterpolator
import android.view.MotionEvent
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.animation.addListener
import androidx.core.view.isVisible
import com.mei.base.ui.nav.Nav
import com.mei.live.views.followFriend
import com.mei.login.checkLogin
import com.mei.login.toLogin
import com.mei.orc.ext.dip
import com.mei.orc.util.save.getObjectMMKV
import com.mei.orc.util.string.getInt
import com.mei.wood.R
import com.mei.wood.extensions.executeKt
import com.mei.wood.ui.MeiCustomActivity
import com.mei.wood.ui.MeiCustomBarActivity
import com.mei.wood.util.logDebug
import com.net.model.chick.video.ShortVideoInfo
import com.net.model.room.GenericEffectConfig
import com.net.network.chick.video.ShortVideoLogChatRequest
import com.net.network.chick.video.ShortVideoLogFollowRequest
import com.opensource.svgaplayer.SVGADrawable
import com.opensource.svgaplayer.SVGAParser
import com.opensource.svgaplayer.SVGAVideoEntity
import com.pili.pldroid.player.PlayerState
import kotlinx.android.synthetic.main.item_video_layout.view.*
import kotlinx.android.synthetic.main.new_short_video_mentor_option_layout.view.*
import java.net.URL
import kotlin.random.Random

/**
 *
 * @author Created by lenna on 2020/8/19
 */

fun ShortVideoView.startLiveButtonAnimation() {
    if (short_video_like_iv.isAnimating) {
        short_video_like_iv.stopAnimation()
    }
    val genericEffectConfig = GenericEffectConfig::class.java.name.getObjectMMKV(GenericEffectConfig::class.java)
    var url: URL? = null
    try {
        url = URL(genericEffectConfig?.shortVideoEffect?.shortVideoLike)
    } catch (e: Exception) {
        logDebug(e.toString())
    }
    url?.let {
        SVGAParser.shareParser().decodeFromURL(it, object : SVGAParser.ParseCompletion {
            override fun onComplete(videoItem: SVGAVideoEntity) {
                short_video_like_iv.isVisible = true
                val svgDrawable = SVGADrawable(videoItem)
                short_video_like_iv.clearsAfterStop = false
                short_video_like_iv.setImageDrawable(svgDrawable)
                short_video_like_iv.startAnimation()
            }

            override fun onError() {
                short_video_like_iv.setImageDrawable(context.getDrawable(R.drawable.icon_praise_pre))
            }

        })
    }
}


//vararg可变参数修饰符，此处可以传入多个Float类型值
fun rotation(view: View, time: Long, delayTime: Long, vararg values: Float): ObjectAnimator {
    val ani = ObjectAnimator.ofFloat(view, "rotation", *values)
    ani.duration = time
    ani.startDelay = delayTime
    ani.interpolator = TimeInterpolator { input -> input }
    return ani
}

fun alphaAni(view: View, from: Float, to: Float, time: Long, delayTime: Long): ObjectAnimator {
    val ani = ObjectAnimator.ofFloat(view, "alpha", from, to)
    ani.interpolator = LinearInterpolator()
    ani.duration = time
    ani.startDelay = delayTime
    return ani
}

fun translationY(view: View, from: Float, to: Float, time: Long, delayTime: Long): ObjectAnimator {
    val ani = ObjectAnimator.ofFloat(view, "translationY", from, to)
    ani.interpolator = LinearInterpolator()
    ani.startDelay = delayTime
    ani.duration = time
    return ani
}

fun translationX(view: View, from: Float, time: Long, to: Float, delayTime: Long): ObjectAnimator {
    val ani = ObjectAnimator.ofFloat(view, "translationX", from, to)
    ani.startDelay = delayTime
    ani.duration = time
    ani.interpolator = LinearInterpolator()
    return ani
}

fun scaleAni(view: View, propertyName: String, from: Float, to: Float, time: Long, delayTime: Long): ObjectAnimator {
    val ani = ObjectAnimator.ofFloat(view, propertyName, from, to)
    ani.interpolator = LinearInterpolator()
    ani.startDelay = delayTime
    ani.duration = time
    return ani
}

fun ShortVideoView.doubleClickAnimation(event: MotionEvent) {
    val videoTop = short_new_video_view.y
    val iv = ImageView(context).apply {
        //设置图片资源
        setImageResource(R.drawable.icon_praise_pre)
        //把IV添加到父布局中
        this@doubleClickAnimation.addView(this, ConstraintLayout.LayoutParams(dip(91), dip(95)).apply {
            //设置图片的相对坐标是父布局的左上角开始的
            leftToLeft = 0
            topToTop = 0
            //设置图片相对于点击位置的坐标
            leftMargin = (event.x.minus(dip(45))).toInt()
            topMargin = ((event.y + videoTop).minus(dip(70))).toInt()
        })
    }
    AnimatorSet().apply {
        play(scaleAni(iv, "scaleX", 2f, 0.9f, 100, 0))
                .with(scaleAni(iv, "scaleY", 2f, 0.9f, 100, 0))
                .with(rotation(iv, 0, 0, num[Random.nextInt(4)]))
                .with(alphaAni(iv, 0F, 1F, 100, 0))
                .with(scaleAni(iv, "scaleX", 0.9f, 1F, 50, 150))
                .with(scaleAni(iv, "scaleY", 0.9f, 1F, 50, 150))
                .with(translationY(iv, 0f, -dip(65) * 1.0f, 800, 400))
                .with(alphaAni(iv, 1F, 0F, 300, 400))
                .with(scaleAni(iv, "scaleX", 1F, 3f, 700, 400))
                .with(scaleAni(iv, "scaleY", 1F, 3f, 700, 400))

        addListener(onEnd = {
            //当动画结束，把控件从父布局移除
            removeViewInLayout(iv)
        })
        start()
    }
    if (!isLike) {
        praiseVideo()
    }
    if (short_new_video_view.playerState == PlayerState.PAUSED) {
        short_video_play_or_pause_iv.performClick()
    }

}

/**
 * 关注该知心人
 */
fun ShortVideoView.followMentor(data: ShortVideoInfo.VideoInfo?) {
    follow_gfl.isVisible = false
    data?.subscribe = true
    (context as? MeiCustomBarActivity)?.followFriend(data?.user?.userId
            ?: 0, 6, data?.user?.userId.toString(), back = {
        if (!it) {
            follow_gfl.isVisible = true
            data?.subscribe = false
        }
    })
}

fun ShortVideoView.gotoAction(data: ShortVideoInfo.VideoInfo?) {
    if (!context.checkLogin()) {
        context.toLogin()
    } else {
        data?.let { u -> Nav.toAmanLink(context, u.action) }
    }
}


fun ShortVideoView.showLoading(isShow: Boolean) {
    (context as? MeiCustomActivity)?.showLoading(isShow)
}


fun ShortVideoView.statLogFollow(seqId: String) {
    (context as? MeiCustomActivity)?.apiSpiceMgr?.executeKt(ShortVideoLogFollowRequest(seqId.getInt(0)))
}

fun ShortVideoView.statLogChat(seqId: String) {
    (context as? MeiCustomActivity)?.apiSpiceMgr?.executeKt(ShortVideoLogChatRequest(seqId.getInt(0)))
}
