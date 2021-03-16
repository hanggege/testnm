package com.mei.live.ui.dialog

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.WebView
import android.widget.CheckBox
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.FragmentActivity
import com.joker.connect.PayCallBack
import com.joker.im.custom.CustomType
import com.joker.im.custom.chick.ChickCustomData
import com.joker.model.PayErrorCode
import com.joker.model.PayFailure
import com.joker.model.PaySuccess
import com.mei.base.common.COURSE_PAY_SUCCESS
import com.mei.base.common.PAY_SUCCESS
import com.mei.base.common.SHOW_RECOMMENT
import com.mei.base.ui.nav.Nav
import com.mei.base.util.web.initWebViewSetting
import com.mei.chat.toImChat
import com.mei.live.manager.liveSendCustomMsg2
import com.mei.live.ui.VideoLiveRoomActivity
import com.mei.live.ui.ext.getCurrLiveState
import com.mei.live.views.launchCouponApply
import com.mei.orc.Cxt
import com.mei.orc.dialog.BottomDialogFragment
import com.mei.orc.event.bindAction
import com.mei.orc.event.postAction
import com.mei.orc.ext.getIndexOrNull
import com.mei.orc.ext.layoutInflaterKt
import com.mei.orc.ext.screenHeight
import com.mei.orc.ext.screenWidth
import com.mei.orc.ui.loading.LoadingToggle
import com.mei.orc.ui.toast.UIToast
import com.mei.orc.util.json.json2Obj
import com.mei.orc.util.save.getBooleanMMKV
import com.mei.orc.util.save.putMMKV
import com.mei.orc.util.string.getInt
import com.mei.pay.thirdPartPaymentV2
import com.mei.wood.R
import com.mei.wood.dialog.DialogData
import com.mei.wood.dialog.NormalDialog
import com.mei.wood.ext.AmanLink
import com.mei.wood.ext.AmanLink.URL.exclusive_service_ask_dialog
import com.mei.wood.ui.MeiCustomActivity
import com.mei.wood.util.MeiUtil
import com.mei.wood.util.parseValue
import com.mei.wood.web.MelkorWebViewClient
import com.net.MeiUser
import kotlinx.android.synthetic.main.exlusive_service_h5_bottom_dialog.*

/**
 *
 * @ProjectName:    dove
 * @Package:        com.mei.live.ui.dialog
 * @ClassName:      ExlusiveServiceH5BottomFragment
 * @Description:
 * @Author:         zxj
 * @CreateDate:     2020/5/26 15:24
 * @UpdateUser:
 * @UpdateDate:     2020/5/26 15:24
 * @UpdateRemark:
 * @Version:
 */
class ExclusiveServiceH5BottomFragment : BottomDialogFragment() {
    var load = ""
    var roomId = ""

    override fun onSetInflaterLayout() = R.layout.exlusive_service_h5_bottom_dialog

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadWebURL()
        bindAction<Boolean>(COURSE_PAY_SUCCESS) {
            if (it == true) {
                live_service_web_content?.loadUrl("javaScript:reloadData()")
                live_service_web_content?.loadUrl("javaScript:paySuccesses()")
            }
        }

    }

    private fun loadWebURL() {
        live_service_web_content.updateLayoutParams {
            width = screenWidth
            height = (screenHeight * 0.7).toInt()
        }
        net_error_layout.updateLayoutParams {
            width = screenWidth
            height = (screenHeight * 0.7).toInt()
        }
        live_service_web_content?.initWebViewSetting()
        live_service_web_content.settings.useWideViewPort = true
        live_service_web_content.settings.loadWithOverviewMode = true
        live_service_web_content?.loadUrl(MeiUtil.appendGeneraUrl(load))
        net_error_layout.setOnClickListener {
            live_service_web_content.loadUrl(MeiUtil.appendGeneraUrl(load))
        }
        live_service_web_content.webViewClient = object : MelkorWebViewClient() {
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                short_video_loading.start()
            }

            override fun handleLoadError() {
                super.handleLoadError()
                net_error_layout.isVisible = true
                live_service_web_content.isVisible = false
            }

            override fun handleUrlLoading(view: WebView?, url: String): Boolean {
                Log.i("handleUrlLoading", url)
                if (isAppLink(url)) {
                    when {
                        url.startsWith(AmanLink.URL.COURSE_SERVICE_PLAYER) -> {
                            if (activity is VideoLiveRoomActivity) {
                                UIToast.toast(activity, when (getCurrLiveState()) {
                                    1 -> "当前正在直播中，无法播放"
                                    2 -> "当前正在连线中，无法播放"
                                    else -> "在直播间内，无法播放课程音频"
                                })
                            }
                        }
                        url.startsWith(AmanLink.URL.AUDIO_PLAYER) -> {
                            if (activity is VideoLiveRoomActivity && getCurrLiveState() != 0) {
                                UIToast.toast(activity, when (getCurrLiveState()) {
                                    1 -> "当前正在直播中，无法播放"
                                    2 -> "当前正在连线中，无法播放"
                                    else -> "在直播间内，无法播放课程音频"
                                })
                            } else {
                                Nav.toAmanLink(activity, url)
                            }
                        }
                        url.startsWith(AmanLink.URL.COURSE_PAY_DIALOG) -> {
                            val payUrl = url.parseValue("url")
                            activity?.showExclusiveServiceDialog(payUrl, roomId)
                        }
                        url.startsWith(AmanLink.URL.COURSE_PAY_SUCCESS) -> {
                            //课程充值成
                            postAction(COURSE_PAY_SUCCESS, true)
                        }
                        url.matches(AmanLink.URL.USER_CHAT_PATTERN.toRegex()) -> {
                            val chatId = MeiUtil.getOneID(url, AmanLink.URL.USER_CHAT_PATTERN)
                            context?.toImChat(chatId, false)
                        }
                        url.startsWith(AmanLink.URL.USER_CHAT_SEND_MESSAGE) -> {
                            val data: String? = url.parseValue("custom_message")
                            if (data?.isNotEmpty() == true) {
                                val result = data.json2Obj<ChickCustomData.Result>()
                                if (result != null) {
                                    var publishUserId = ""
                                    result.data?.serviceInfo?.apply {
                                        publishUserId = publisherId.toString()
                                    }
                                    result.data?.courseInfo?.apply {
                                        publishUserId = userId.toString()
                                    }
                                    context?.toImChat(publishUserId, false, result)
                                }
                            }
                        }
                        url.matches(AmanLink.URL.web_send_to_im.toRegex()) -> {
                            MeiUtil.getJsonObject<ChickCustomData.Result>(url, AmanLink.URL.web_send_to_im)?.let { info ->
                                if (MeiUser.getSharedUser().info?.isPublisher == true) {
                                    if (!SHOW_RECOMMENT.getBooleanMMKV(false) && info.data?.content?.getIndexOrNull(0)?.action == exclusive_service_ask_dialog) {
                                        showDialog(info)
                                        return@let
                                    }
                                    if (info.data?.content?.getIndexOrNull(0)?.action == exclusive_service_ask_dialog) {
                                        if (info.data?.type == CustomType.special_service_card) {
                                            UIToast.toast("推荐服务已发送")
                                        } else if (info.data?.type == CustomType.course_card) {
                                            UIToast.toast("推荐课程已发送")
                                        }
                                    }
                                }
                                liveSendCustomMsg2(roomId, type = info.getType(), data = info.data
                                        ?: ChickCustomData())

                            }
                        }
                        url.matches(AmanLink.URL.close_webview.toRegex()) -> {
                            dismissAllowingStateLoss()
                        }
                        url.matches(AmanLink.URL.WEB_CALL_APP_PAY_SDK_V2.toRegex()) -> {
                            activity?.thirdPartPaymentV2(url, object : PayCallBack {
                                override fun onSuccess(success: PaySuccess?) {
                                    view?.loadUrl("javascript:paySuccessV2()")
                                    postAction(PAY_SUCCESS, success)
                                }

                                override fun onFailure(failure: PayFailure?) {
                                    if (failure != null) {
                                        when (failure.err_code) {
                                            PayErrorCode.Pay_Not_reback_Info_code -> UIToast.toast(failure.err_reason)
                                            PayErrorCode.Pay_User_Cancel_code -> {
                                                UIToast.toast(Cxt.getStr(R.string.pay_cancel))
                                                view?.loadUrl("javascript:payCancelV2()")
                                            }
                                            PayErrorCode.pay_failure_code -> live_service_web_content.loadUrl("javascript:payFailureV2()")
                                            else -> {
                                                UIToast.toast(failure.err_msg)
                                                view?.loadUrl("javascript:payFailureV2()")
                                            }
                                        }
                                    }
                                }
                            })
                        }
                        url.startsWith(AmanLink.URL.is_hand_show_webview) -> {
                            when (url.parseValue("switch", "")) {
                                "0" -> {
                                    //不可以关闭
                                    dialog?.setCancelable(false)
                                    dialog?.setCanceledOnTouchOutside(false)
                                }
                                else -> {
                                    //可以关闭
                                    dialog?.setCancelable(true)
                                    dialog?.setCanceledOnTouchOutside(true)
                                }
                            }
                        }
                        url.startsWith(AmanLink.URL.coupon_dialog) -> {
                            val couponId = url.parseValue("couponId").getInt(0)
                            val couponNum = url.parseValue("couponNum")
                            val publisherId = url.parseValue("publisherId").getInt(0)
                            (activity as? MeiCustomActivity)?.showCouponDialog(couponId, couponNum, publisherId)
                        }
                        else -> {
                            Nav.toAmanLink(activity, url)
                        }
                    }
                    return true
                } else {
                    return false
                }
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                (activity as? LoadingToggle)?.showLoading(false)
                short_video_loading.stop()
                live_service_web_content.setBackgroundColor(0) // 设置背景色
                live_service_web_content.background?.mutate()?.alpha = 0
                net_error_layout.isVisible = false
                live_service_web_content.isVisible = true
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        live_service_web_content?.destroy()
    }


    @SuppressLint("SetTextI18n")
    fun showDialog(info: ChickCustomData.Result) {
        var dialog: NormalDialog? = null
        val customView = context?.layoutInflaterKt(R.layout.service_recommend_dialog)?.apply {
            findViewById<TextView>(R.id.invite_title).text = "推荐该${when (info.data?.type) {
                CustomType.special_service_card -> "服务"
                CustomType.course_card -> "课程"
                else -> "卡片"
            }}到直播间"
            findViewById<CheckBox>(R.id.cb_no_next_show_dialog).setOnCheckedChangeListener { _, isChecked -> SHOW_RECOMMENT.putMMKV(isChecked) }
            findViewById<View>(R.id.confirm_dialog_btn).setOnClickListener {
                liveSendCustomMsg2(roomId, type = info.getType(), data = info.data
                        ?: ChickCustomData())
                dialog?.dismissAllowingStateLoss()
            }
        }
        dialog = NormalDialog().showDialog(activity, DialogData(customView = customView, canceledOnTouchOutside = false, backCanceled = false), okBack = null)
    }
}

@JvmOverloads
fun FragmentActivity.showExclusiveServiceDialog(loadUrl: String, roomId: String = ""): ExclusiveServiceH5BottomFragment {
    val dialog = ExclusiveServiceH5BottomFragment().apply {
        this.load = loadUrl
        this.roomId = roomId
    }
    dialog.show(supportFragmentManager, "ExlusiveServiceH5BottomFragment")
    return dialog
}
