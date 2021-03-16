package com.mei.video.browser.view

import android.annotation.SuppressLint
import android.view.MotionEvent
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.view.children
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import com.mei.base.common.DRAWER_LAYOUT_STATE
import com.mei.orc.event.postAction
import com.mei.orc.ext.dip
import com.mei.orc.ext.layoutInflaterKtToParentAttach
import com.mei.orc.ext.screenHeight
import com.mei.orc.ext.screenWidth
import com.mei.orc.util.save.getNonValue
import com.mei.orc.util.save.getObjectMMKV
import com.mei.orc.util.save.putValue
import com.mei.wood.R
import com.mei.wood.util.logDebug
import com.net.model.room.GenericEffectConfig
import com.opensource.svgaplayer.SVGADrawable
import com.opensource.svgaplayer.SVGAParser
import com.opensource.svgaplayer.SVGAVideoEntity
import kotlinx.android.synthetic.main.view_short_video_guide.view.*
import java.net.URL
import kotlin.math.abs

/**
 * Created by hang on 2020/8/26.
 * 短视频播放页面手势引导
 */
fun ViewGroup.showGuideGesture() {
    val hasGuideView by lazy { children.any { it is ShortVideoGuideView } }
    val guideView by lazy { ShortVideoGuideView(this) }

    val isGuideUpDown = "is_guide_up_down_first_show".getNonValue(false)
    if (!isGuideUpDown && !hasGuideView) {
        "is_guide_up_down_first_show".putValue(true)
        guideView.showGuideAnim(0)
    } else {
        val isGuideLeftRight = "is_guide_right_left_first_show".getNonValue(false)
        if (!isGuideLeftRight && !hasGuideView) {
            "is_guide_right_left_first_show".putValue(true)
            guideView.showGuideAnim(1)
        }
    }
}

@SuppressLint("ViewConstructor")
class ShortVideoGuideView(viewGroup: ViewGroup) : FrameLayout(viewGroup.context) {

    var type = -1

    init {
        layoutInflaterKtToParentAttach(R.layout.view_short_video_guide)

        (parent as? ViewGroup)?.removeView(this)
        viewGroup.addView(this)
    }

    fun showGuideAnim(type: Int) {
        this.type = type
        if (type == 0) {
            guide_tv.text = "上滑查看更多内容"
        } else {
            guide_tv.text = "左滑查看更多推荐"
        }
        startAnimation(getAnimUrl(type))
    }

    /**
     * @param type 0 上下  1左右
     */
    private fun getAnimUrl(type: Int): String {
        val genericEffectConfig = GenericEffectConfig::class.java.name.getObjectMMKV(GenericEffectConfig::class.java)
        return if (type == 0) {
            genericEffectConfig?.shortVideoEffect?.upAndDown.orEmpty()
        } else {
            genericEffectConfig?.shortVideoEffect?.leftAndRight.orEmpty()
        }
    }

    private fun startAnimation(urlStr: String) {
        var url: URL? = null
        try {
            url = URL(urlStr)
        } catch (e: Exception) {
            logDebug(e.toString())
        }
        url?.let {
            SVGAParser.shareParser().decodeFromURL(it, object : SVGAParser.ParseCompletion {
                override fun onComplete(videoItem: SVGAVideoEntity) {
                    guide_siv.isVisible = true
                    val svgDrawable = SVGADrawable(videoItem)
                    guide_siv.updateLayoutParams<ViewGroup.LayoutParams> {
                        width = dip(103)
                        height = dip(140)
                    }
                    guide_siv.clearsAfterStop = false
                    guide_siv.setImageDrawable(svgDrawable)
                    guide_siv.startAnimation()
                }

                override fun onError() {
                }
            })
        }
    }

    var startX = 0f
    var startY = 0f

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                startX = event.x
                startY = event.y
            }
            MotionEvent.ACTION_MOVE -> {
                val offsetX = event.x - startX
                val offsetY = event.y - startY

                val upDownScroll = type == 0 && abs(offsetY) > abs(offsetX) && abs(offsetY) > 30
                val leftRightScroll = type == 1 && abs(offsetX) > abs(offsetY) && abs(offsetX) > 30
                var direction = -1
                if (upDownScroll) {
                    direction = if (offsetY > 0) 3 else 1
                }
                if (leftRightScroll) {
                    /**暂时屏蔽右滑*/
                    direction = if (offsetX > 0) -1 else 0
                }
                if (direction == 0) {
                    postAction(DRAWER_LAYOUT_STATE, true)
                }
                if (direction > -1) {
                    hideGuideAnim(direction)
                }
            }
        }
        return true
    }

    /**
     * @param direction 0 left 1 top 2 right 3 bottom
     */
    private fun hideGuideAnim(direction: Int) {
        var xOffset = 0f
        var yOffset = 0f
        when (direction) {
            0 -> {
                xOffset = -screenWidth.toFloat()
                yOffset = 0f
            }
            1 -> {
                xOffset = 0f
                yOffset = -screenHeight.toFloat()
            }
            2 -> {
                xOffset = screenWidth.toFloat()
                yOffset = 0f
            }
            3 -> {
                xOffset = 0f
                yOffset = screenHeight.toFloat()
            }
        }
        animate().withLayer().withStartAction {
            translationX = 0f
            translationY = 0f
        }.translationX(xOffset).translationY(yOffset).withEndAction {
            if (isAttachedToWindow) {
                guide_siv.stopAnimation()
                (parent as? ViewGroup)?.removeView(this)
            }
        }
    }
}