package com.mei.live.views.room

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import androidx.core.view.isVisible
import com.mei.orc.dialog.MeiSupportDialogFragment
import com.mei.orc.event.bindAction
import com.mei.orc.util.date.formatTimeDown
import com.mei.red_packet.showLiveRoomReceiveRedPacketDialog
import com.mei.red_packet.toReceiveRedPacket
import com.mei.wood.R
import com.mei.wood.constant.Preference
import com.mei.wood.extensions.executeKt
import com.mei.wood.extensions.subscribeBy
import com.mei.wood.ui.CustomSupportFragment
import com.mei.wood.ui.MeiCustomActivity
import com.mei.wood.util.logDebug
import com.net.MeiUser
import com.net.model.room.RoomRedPacket
import com.net.network.chick.room.RoomRedPacketStatusRequest
import com.trello.rxlifecycle3.android.FragmentEvent
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.view_live_receive_red_packet.*
import java.util.concurrent.TimeUnit

/**
 * Created by hang on 2020/11/16.
 */
class LiveReceiveRedPacketFragment : CustomSupportFragment() {
    var roomId = ""

    var packetInfo: RoomRedPacket? = null

    /**点击红包的全屏弹窗动画*/
    private var receiveRedPacketAnimDialog: MeiSupportDialogFragment? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? = inflater.inflate(R.layout.view_live_receive_red_packet, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.setOnClickListener {
            clickRedPacket()
        }

        bindAction<Boolean>(Preference.ROOM_ENTER) {
            refreshData()
        }
        refreshData()
    }

    /**
     * 刷新红包数据
     */
    private fun refreshData() {
        if (packetInfo == null) {
            requestRedPacketStatus {
                loadUserRedPacketStatus()
                updateRedPacket()
            }
        }
    }

    private fun updateRedPacket() {
        // update text & image
        val bindPhone = MeiUser.getSharedUser().extra?.bindPhone ?: false
        bind_phone_red_packet.isVisible = !bindPhone

        receive_red_packet_bg.isVisible = bindPhone
        need_duration_tv.isVisible = bindPhone
        receive_red_packet_btn.isVisible = bindPhone

        packetInfo?.duration?.let { refreshCountDownText(it) }

        // startAnimation
        if (packetInfo?.needAnimation == true) {
            receive_red_packet_bg.startAnimation()
        }
    }

    private fun View.startAnimation() {
        clearAnimation()
        val shakeDegrees = 5f
        val rotateAnim: Animation = RotateAnimation(-shakeDegrees, shakeDegrees, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f)
        rotateAnim.duration = 100
        rotateAnim.repeatCount = 5
        rotateAnim.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationRepeat(animation: Animation?) {
            }
            override fun onAnimationEnd(animation: Animation?) {
                logDebug("onAnimationEnd")
                if (isAdded && packetInfo?.needAnimation == true) {
                    postDelayed({
                        if (isAdded) {
                            receive_red_packet_bg.startAnimation()
                        }
                    }, 3000)
                }
            }

            override fun onAnimationStart(animation: Animation?) {
            }
        })
        startAnimation(rotateAnim)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        receive_red_packet_bg.clearAnimation()
    }

    /**
     * 点击红包
     */
    private fun clickRedPacket() {
        val bindPhone = MeiUser.getSharedUser().extra?.bindPhone ?: false
        clickRedPacketStat(bindPhone)
        if (bindPhone) {
            //实时获取直播间红包状态
            requestRedPacketStatus {
                if (packetInfo != null) {
                    redPacketBrowseDialog(bindPhone)
                    receiveRedPacketAnimDialog = activity?.showLiveRoomReceiveRedPacketDialog(roomId, result = {
                        if (it) {
                            receive_red_packet_bg.clearAnimation()
                        }
                        requestRedPacketStatus()
                    })
                }
            }
        } else {
            redPacketBrowseDialog(bindPhone)
            (activity as? MeiCustomActivity)?.toReceiveRedPacket(back = { result ->
                if (result) {
                    requestRedPacketStatus {
                        loadUserRedPacketStatus()
                    }
                }
            })
        }
    }

    /**
     * 获取直播间红包详情和状态
     */
    private fun requestRedPacketStatus(back: (RoomRedPacket?) -> Unit = {}) {
        apiSpiceMgr.executeKt(RoomRedPacketStatusRequest(roomId), success = {
            val info = it.data?.config
            if (it.isSuccess) {
                packetInfo = info
                back(info)
            } else {
                hidePacketView()
            }
        }, failure = {
            hidePacketView()
        })
    }

    /**
     * 加载直播间红包
     */
    private fun loadUserRedPacketStatus() {
        val bindPhone = MeiUser.getSharedUser().extra?.bindPhone ?: false
        //未绑定手机号一定会显示，已绑定手机号需要读取后端接口进行判断
        val showRedPacket = !bindPhone || packetInfo?.hasChance == true
        if (!showRedPacket) {
            hidePacketView()
        } else {
            showPacketView()
            redPacketBrowse(bindPhone)
        }
        if (bindPhone && packetInfo?.duration != 0L) {
            countDownTime()
        }
    }

    private var mDownTask: Disposable? = null
    private fun countDownTime() {
        if (mDownTask?.isDisposed == false) {
            mDownTask?.dispose()
        }
        Observable.interval(1, 1, TimeUnit.SECONDS)
                .doOnSubscribe {
                    mDownTask = it
                }
                .observeOn(AndroidSchedulers.mainThread())
                .compose(bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .subscribeBy { time ->
                    packetInfo?.also {
                        it.duration--
                        if (it.duration < 15 && it.duration > -10 && time % 5 == 0L) {
                            requestRedPacketStatus {
                                refreshCountDownText(packetInfo?.duration ?: 0)
                            }
                        }
                        refreshCountDownText(it.duration)
                    }
                }
    }

    private fun refreshCountDownText(duration: Long) {
        if (duration >= 0) {
            need_duration_tv.text = (duration * 1000L).formatTimeDown()
        } else {
            receive_red_packet_bg.startAnimation()
            mDownTask?.dispose()
        }
    }

    private fun showPacketView() {
        parentFragmentManager.beginTransaction().show(this).commitAllowingStateLoss()
    }

    private fun hidePacketView() {
        parentFragmentManager.beginTransaction().hide(this).commitAllowingStateLoss()
    }
}