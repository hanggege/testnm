package com.mei.red_packet

import android.os.Bundle
import android.view.View
import com.mei.orc.dialog.MeiSupportDialogFragment
import com.mei.orc.util.save.getObjectMMKV
import com.mei.wood.R
import com.mei.wood.ui.MeiCustomActivity
import com.mei.wood.util.logDebug
import com.net.model.room.GenericEffectConfig
import com.opensource.svgaplayer.SVGADrawable
import com.opensource.svgaplayer.SVGAParser
import com.opensource.svgaplayer.SVGAVideoEntity
import kotlinx.android.synthetic.main.receive_red_packet_dialog.*
import java.net.URL

/**
 *
 * @author Created by lenna on 2020/7/27
 * 红包领取后动画展示dialog
 */
class ReceiveRedPacketDialog : MeiSupportDialogFragment() {
    override fun onSetInflaterLayout(): Int {
        return R.layout.receive_red_packet_dialog
    }

    override fun isFullScreen(): Boolean {
        return true
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() {

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
                        val svgaDrawable = SVGADrawable(videoItem)
                        svg_image.clearsAfterStop = false
                        svg_image.setImageDrawable(svgaDrawable)
                        svg_image.startAnimation()
                    }
                }

                override fun onError() {

                }

            })
        }
        close_receive_red_packet_iv.setOnClickListener {
            dismissAllowingStateLoss()
        }
    }
}

fun MeiCustomActivity.showReceiveRedPacketDialog() {
    ReceiveRedPacketDialog().show(supportFragmentManager, "ReceiveRedPacketDialog")
}