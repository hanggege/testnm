package com.mei.live.ui.dialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.FragmentActivity
import com.mei.orc.Cxt
import com.mei.orc.dialog.MeiSupportDialogFragment
import com.mei.orc.ext.dip
import com.mei.orc.ext.dp
import com.mei.orc.imageload.glide.glideDisplay
import com.mei.widget.gradient.applyViewDelegate
import com.mei.wood.R
import com.net.model.room.RoomExit
import kotlinx.android.synthetic.main.dialog_exit_room_share.*


fun FragmentActivity.showExitRoomShareDialog(
        roomExitInfo: RoomExit,
        exit: () -> Unit = { },
        share: (roomExit: RoomExit?, isWechatCircle: Boolean) -> Unit = { _, _ -> }
) {
    if (supportFragmentManager.findFragmentByTag("ExitRoomDialog") == null) {
        RoomExtShareDialog().apply {
            this.roomExit = roomExitInfo
            this.exit = exit
            this.share = share
        }.show(supportFragmentManager, "ExitRoomDialog")
    }
}

/**
 *
 * @author Created by lenna on 2020/7/1
 * 直播间退出分享弹框提示
 */
class RoomExtShareDialog : MeiSupportDialogFragment() {
    var roomExit: RoomExit? = null
    var exit: () -> Unit = { }
    var share: (roomExit: RoomExit?, isWechatCircle: Boolean) -> Unit = { _, _ -> }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.dialog_exit_room_share, container, false)
        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        return view
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.apply {
            val params = attributes
            params.gravity = Gravity.CENTER
            params.width = dip(300)
            params.dimAmount = getBackgroundAlpha()
            attributes = params
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
        initView()
        setData(roomExit)
    }

    private fun setData(roomExit: RoomExit?) {
        roomExit?.let {
            share_info_top_img.glideDisplay(it.backendGround, R.drawable.icon_exit_room_share_top_bg)
            if (!it.isExitRoom) {
                if (it.isFirstShare) {
                    shareAgainStyle(it.showText)
                } else {
                    exitLivingStyle()
                }
            } else {
                share_msg.isVisible = false
                share_poster.isVisible = true
                share_poster.glideDisplay(it.questionImage.orEmpty())
                share_msg_right_iv.isVisible = false
            }
        }
    }

    private fun exitLivingStyle() {
        share_poster.isVisible = false
        share_msg.isVisible = false
        share_exit_text.isVisible = false
        share_btn_right.isVisible = false
        share_btn_left_img.isVisible = false
        share_btn_left_text.setTextColor(Cxt.getColor(android.R.color.white))
        share_btn_left_text.text = "退出直播间"
        share_btn_left.delegate.applyViewDelegate {
            radius = 23.dp
            startColor = ContextCompat.getColor(context, R.color.color_FEBA2E)
            endColor = ContextCompat.getColor(context, R.color.color_FF8100)
        }
        share_btn_left.updateLayoutParams<ConstraintLayout.LayoutParams> {
            topMargin = dip(20)
            goneBottomMargin = dip(40)
            marginStart = dip(75)
            marginEnd = dip(75)
        }
        share_btn_left.setOnClickListener {
            exit()
            dialog?.dismiss()
        }
    }

    private fun shareAgainStyle(bean: RoomExit.ShowTextBean) {
        share_poster.isVisible = false
        share_exit_text.isVisible = false
        share_msg.isVisible = true
        share_msg.background = ContextCompat.getDrawable(Cxt.get(), R.drawable.icon_exit_room_share_right)
        share_msg_left_iv.isVisible = false
        share_msg_text.text = bean.text
        share_msg_right_iv.glideDisplay(bean.rightImage.orEmpty())

        share_btn_left_img.isVisible = false
        share_btn_left_text.text = "退出直播间"
        share_btn_left_text.paint.isFakeBoldText = false
        share_btn_left_text.setTextColor(ContextCompat.getColor(Cxt.get(), R.color.color_999999))
        share_btn_left.delegate.applyViewDelegate {
            radius = 23.dp
            startColor = ContextCompat.getColor(context, R.color.color_f8f8f8)
        }
        share_btn_right_text.text = "再次分享"
        share_btn_right_text.paint.isFakeBoldText = true
        share_btn_right.delegate.applyViewDelegate {
            radius = 23.dp
            startColor = ContextCompat.getColor(context, R.color.color_FEBA2E)
            endColor = ContextCompat.getColor(context, R.color.color_FF8100)
        }
        share_btn_left.updateLayoutParams<ConstraintLayout.LayoutParams> {
            topMargin = dip(3)
            marginStart = dip(20)
            marginEnd = dip(30)
        }
        share_btn_right.updateLayoutParams<ConstraintLayout.LayoutParams> {
            marginEnd = dip(20)
        }
        share_btn_left.setOnClickListener {
            exit()
            dialog?.dismiss()
        }
        share_btn_right.setOnClickListener {
            share(roomExit, roomExit?.isWechatCircle == true)
            dialog?.dismiss()
        }
    }


    private fun initView() {
        share_btn_left_text.paint.isFakeBoldText = true
        share_btn_left.setOnClickListener {
            share(roomExit, false)
            dialog?.dismiss()
        }
        share_btn_right_text.paint.isFakeBoldText = true
        share_btn_right.setOnClickListener {
            share(roomExit, true)
            dialog?.dismiss()
        }
        share_exit_text.setOnClickListener {
            exit()
            dialog?.dismiss()
        }
    }

    private fun getBackgroundAlpha(): Float {
        return 0.6f
    }
}