package com.mei.red_packet

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentActivity
import com.mei.live.ui.VideoLiveRoomActivity
import com.mei.live.views.room.clickRedPacketDialogStat
import com.mei.orc.dialog.MeiSupportDialogFragment
import com.mei.orc.imageload.glide.glideDisplay
import com.mei.orc.ui.toast.UIToast
import com.mei.orc.util.click.setOnClickNotDoubleListener
import com.mei.orc.util.date.formatTimeDown
import com.mei.orc.util.save.getObjectMMKV
import com.mei.wood.R
import com.mei.wood.extensions.executeKt
import com.mei.wood.extensions.subscribeBy
import com.mei.wood.ui.MeiCustomActivity
import com.mei.wood.util.logDebug
import com.net.model.room.GenericEffectConfig
import com.net.model.room.RoomRedPacket
import com.net.network.chick.room.RoomOpenRedPacketRequest
import com.net.network.chick.room.RoomRedPacketStatusRequest
import com.opensource.svgaplayer.SVGADrawable
import com.opensource.svgaplayer.SVGAParser
import com.opensource.svgaplayer.SVGAVideoEntity
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.live_room_receive_red_packet_dialog.*
import java.net.URL
import java.util.concurrent.TimeUnit

/**
 *
 * @author Created by lenna on 2020/7/27
 * 直播间中显示打开红包dialog
 */
class LiveRoomReceiveRedPacketDialog : MeiSupportDialogFragment() {
    private var mRedPacketInfo: RoomRedPacket? = null
    var result: (Boolean) -> Unit = { _ -> }
    var roomId: String = ""
    private var mDownTask: Disposable? = null
    override fun onSetInflaterLayout(): Int {
        return R.layout.live_room_receive_red_packet_dialog
    }

    override fun isFullScreen(): Boolean {
        return true
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.apply {
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        open_red_packet_iv.setOnClickNotDoubleListener(tag = "openRedPacket") {
            clickRedPacketDialogStat(true, "抢", mRedPacketInfo)
            (activity as? VideoLiveRoomActivity)?.openRoomRedPacket(back = {
                if (it) loadAnimationUrl()
                result(it)

            })
        }
        close_receive_red_packet_iv.setOnClickListener {
            dismissAllowingStateLoss()
        }
    }

    private fun initView() {
        if (isAdded) {
            time_detail_rf.isVisible = true
            close_receive_red_packet_iv.isVisible = true
            red_packet_bg_iv.glideDisplay(mRedPacketInfo?.closeBackgroundImage.orEmpty())
            open_red_packet_iv.glideDisplay(mRedPacketInfo?.openRedPacketButtonImage.orEmpty())
            updateTime()
            if (mRedPacketInfo?.duration == 0L) {
                countDownTimeComplete()
            } else {
                startDownTime()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mDownTask?.isDisposed == false) {
            mDownTask?.dispose()
        }

    }

    override fun onResume() {
        super.onResume()
        (activity as? MeiCustomActivity)?.showLoading(true)
        (activity as? MeiCustomActivity)?.apiSpiceMgr
                ?.executeKt(RoomRedPacketStatusRequest(roomId), success = { response ->
                    if (response.isSuccess) {
                        mRedPacketInfo = response.data.config
                        initView()

                    }
                }, failure = {
                    UIToast.toast(it?.currMessage)
                }, finish = {
                    (activity as? MeiCustomActivity)?.showLoading(false)
                })
    }

    @SuppressLint("SetTextI18n")
    private fun startDownTime() {
        if (mDownTask?.isDisposed == false) {
            mDownTask?.dispose()
        }
        Observable.interval(1, TimeUnit.SECONDS)
                .doOnSubscribe {
                    mDownTask = it
                }
                .observeOn(AndroidSchedulers.mainThread())
                .compose(bindToLifecycle())
                .subscribeBy {
                    if (mRedPacketInfo != null) {
                        countDownTime()
                    } else {
                        mDownTask?.dispose()
                    }
                }

    }

    /**
     * 倒计时
     */
    private fun countDownTime() {
        mRedPacketInfo?.let { it ->
            if (it.duration > 0) {
                if (it.duration < 30) {
                    (activity as? VideoLiveRoomActivity)?.refreshRedPacketStatus(back = { data ->
                        mRedPacketInfo = data
                        updateTime()
                    })
                } else {
                    it.duration -= 1
                    updateTime()
                }
            } else {
                mDownTask?.dispose()
                countDownTimeComplete()
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun updateTime() {
        if (isAdded) {
            mRedPacketInfo?.let {
                if (it.duration != 0L) {
                    val downTime = (it.duration * 1000L).formatTimeDown()
                    down_time_tv.text = downTime
                    hint_tv.text = "$downTime 后可领取"
                    receive_time_progress.setProgress(((1 - it.duration * 1.0f / it.needDuration) * 100).toInt())
                }
            }
        }
    }

    /**
     * 倒计时完成
     */
    private fun countDownTimeComplete() {
        if (isAdded) {
            time_detail_rf.isVisible = false
            hint_tv.isVisible = false
            open_red_packet_iv.isVisible = true
        }
    }

    private fun loadAnimationUrl() {
        val genericEffectConfig = GenericEffectConfig::class.java.name.getObjectMMKV(GenericEffectConfig::class.java)
        var url: URL? = null
        try {
            url = URL(genericEffectConfig?.redPacket)
        } catch (e: Exception) {
            logDebug(e.toString())
        }
        url?.let {
            SVGAParser.shareParser().decodeFromURL(it, object : SVGAParser.ParseCompletion {
                override fun onComplete(videoItem: SVGAVideoEntity) {
                    if (isAdded) {
                        red_packet_bg_iv.glideDisplay("")
                        svg_image.isVisible = true
                        val svgDrawable = SVGADrawable(videoItem)
                        svg_image.clearsAfterStop = false
                        svg_image.setImageDrawable(svgDrawable)
                        svg_image.startAnimation()
                    }
                }

                override fun onError() {

                }

            })
        }
    }

}

/**
 * 领取直播间红包请求
 */
fun VideoLiveRoomActivity.openRoomRedPacket(back: (Boolean) -> Unit = {}) {
    showLoading(true)
    apiSpiceMgr.executeKt(RoomOpenRedPacketRequest(roomId), success = {
        back(it.isSuccess)
    }, failure = {
        back(false)
        UIToast.toast(it?.currMessage)
    }, finish = { showLoading(false) })
}

/**
 *  刷新当前dialog红包状态
 */
fun VideoLiveRoomActivity.refreshRedPacketStatus(back: (RoomRedPacket?) -> Unit = {}) {
    apiSpiceMgr.executeKt(RoomRedPacketStatusRequest(roomId), success = {
        if (it.isSuccess) back(it.data.config)
    }, failure = {
        back(null)
    })
}

/**
 * 弹出直播间红包详情dialog
 */
fun FragmentActivity.showLiveRoomReceiveRedPacketDialog(roomId: String, result: (Boolean) -> Unit = { _ -> }): MeiSupportDialogFragment {
    val dialog: LiveRoomReceiveRedPacketDialog = LiveRoomReceiveRedPacketDialog()
            .apply {
                this.result = result
                this.roomId = roomId
            }
    dialog.show(supportFragmentManager, "LiveRoomReceiveRedPacketDialog")
    return dialog
}