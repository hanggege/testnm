package com.mei.live.views

import android.content.Context
import android.os.SystemClock
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import com.joker.im.custom.chick.RoomEnterAnim
import com.mei.live.manager.genderAvatar
import com.mei.live.ui.ext.LevelSize
import com.mei.live.ui.ext.getBackGroundLevelResource
import com.mei.orc.ext.dip
import com.mei.orc.ext.screenWidth
import com.mei.orc.ext.setListenerEnd
import com.mei.orc.imageload.glide.glideDisplay
import com.mei.wood.R
import com.mei.wood.cache.getCacheUserInfo
import com.opensource.svgaplayer.SVGADrawable
import com.opensource.svgaplayer.SVGAParser
import com.opensource.svgaplayer.SVGAVideoEntity
import kotlinx.android.synthetic.main.item_banner_gift.view.*
import java.net.URL

/**
 * Created by hang on 2019/1/16.
 */
class RoomEnterAnimView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
        FrameLayout(context, attrs, defStyleAttr) {

    private val ENTER_TIME: Long = 600
    private val SHOW_GIFT_TIME: Long = 6000
    var exitRunnable: Runnable? = null

    var callback: (view: RoomEnterAnimView) -> Unit = { _ -> }

    init {
        LayoutInflater.from(context).inflate(R.layout.view_room_enter_anim, this)
    }

    var roomEnterAnim: RoomEnterAnim? = null

    /** 礼物开始时间 **/
    var showTime: Long = 0L

    /**
     * 完整显示动画
     */
    fun refreshData(item: RoomEnterAnim) {
        roomEnterAnim = null
        translationX = screenWidth.toFloat()
        alpha = 0.3f
        refreshBaseUi(item)
        animate().translationX(0f)
                .alpha(1f)
                .setDuration(ENTER_TIME)
                .setListenerEnd {
                    showGiftAnim(item.level)
                }.start()
    }

    private fun refreshBaseUi(item: RoomEnterAnim) {
        roomEnterAnim = item

        getCacheUserInfo(item.userId)?.let { userInfo ->
            if (userInfo.userLevel > 0) {

                level_container.visibility = View.VISIBLE
                level_container.visibility = View.VISIBLE
                if (userInfo.userLevel > 9) {
                    level.setPadding(0, 0, dip(6), 0)
                } else {
                    level.setPadding(0, 0, dip(10), 0)
                }
                level_icon.setImageResource(getBackGroundLevelResource(userInfo.userLevel, LevelSize.Normal))
                level.text = userInfo.userLevel.toString()
            } else {
                level_container.visibility = View.GONE
            }
            head_cover.glideDisplay(userInfo.avatar.orEmpty(), userInfo.gender.genderAvatar(userInfo.isPublisher))
            user_name.text = userInfo.nickname
        }
        gift_name.text = item.text

        listenAnimLife()
    }

    private fun showGiftAnim(userLevel: Int) {
        val anim = getAnimResForLevel(userLevel)
        if (anim != null) {
            SVGAParser.shareParser().decodeFromURL(anim, object : SVGAParser.ParseCompletion {
                override fun onComplete(videoItem: SVGAVideoEntity) {
                    if (isAttachedToWindow) {
                        val svgaDrawable = SVGADrawable(videoItem)
                        svga_image.setImageDrawable(svgaDrawable)
                        svga_image.startAnimation()
                    }
                }

                override fun onError() {
                }
            })
        }
    }


    /***
     * 通过userLevel获取本地存储的动画信息
     */
    private fun getAnimResForLevel(userLevel: Int): URL? {
        val path = when (userLevel) {
            in 4..6 -> "https://img.meidongli.net/0/2020/05/15/dove/join_room/svga/love_bike.svga"
            in 7..8 -> "https://img.meidongli.net/0/2020/05/15/dove/join_room/svga/motorcycle.svga"
            in 9..11 -> "https://img.meidongli.net/0/2020/05/15/dove/join_room/svga/big_public.svga"
            in 12..15 -> "https://img.meidongli.net/0/2020/05/15/dove/join_room/svga/mercedes.svga"
            in 16..19 -> "https://img.meidongli.net/0/2020/05/15/dove/join_room/svga/porsche.svga"
            20 -> "https://img.meidongli.net/0/2020/05/15/dove/join_room/svga/lanbojini.svga"
            else -> "https://img.meidongli.net/0/2020/05/15/dove/join_room/svga/love_bike.svga"
        }
        return try {
            URL(path)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * 监听动画展示周期
     */
    private fun listenAnimLife() {
        showTime = SystemClock.elapsedRealtime()
        removeCallbacks(exitRunnable)
        exitRunnable = Runnable {
            /** 礼物显示完了 **/
            roomEnterAnim = null
            svga_image.stopAnimation(true)
            animate().alpha(0.3f).setDuration(ENTER_TIME).setListenerEnd {
                callback(this)
            }.start()
        }
        postDelayed(exitRunnable, SHOW_GIFT_TIME)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        removeCallbacks(exitRunnable)
    }

}