package com.mei.live.ui.dialog;

import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.webkit.WebView
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.FragmentActivity
import com.joker.im.custom.chick.ChickCustomData
import com.mei.base.common.COUPON_TO_SERVICE
import com.mei.base.common.SELECT_HOME
import com.mei.base.ui.nav.Nav
import com.mei.base.util.web.initWebViewSetting
import com.mei.chat.toImChat
import com.mei.live.ui.VideoLiveRoomActivity
import com.mei.live.ui.fragment.appendRoomHalfScreenParamsUrl
import com.mei.live.views.launchCouponApply
import com.mei.orc.ActivityLauncher
import com.mei.orc.dialog.BottomDialogFragment
import com.mei.orc.event.bindAction
import com.mei.orc.event.postAction
import com.mei.orc.ext.screenHeight
import com.mei.orc.ext.screenWidth
import com.mei.orc.ui.loading.LoadingToggle
import com.mei.orc.util.json.json2Obj
import com.mei.orc.util.string.getInt
import com.mei.wood.R
import com.mei.wood.ext.AmanLink
import com.mei.wood.ui.MeiCustomActivity
import com.mei.wood.util.MeiUtil
import com.mei.wood.util.parseValue
import com.mei.wood.web.MelkorWebViewClient
import kotlinx.android.synthetic.main.dialog_common_bottom.*
import launcher.Boom

/**
 * CouponBottomDialog
 *
 * @author kun
 * @version 1.0.0
 * @since 2020-09-14
 */
class CommonBottomFragment : BottomDialogFragment() {

    @Boom
    var load = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        bindAction<Int>(COUPON_TO_SERVICE) {
            toResourceInfo(it ?: 0)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        ActivityLauncher.bind(this)
        val view = inflater.inflate(R.layout.dialog_common_bottom, container, false)
        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadWebURL()
    }

    private fun loadWebURL() {
        content_root.updateLayoutParams {
            width = screenWidth
            height = (screenHeight * 0.7).toInt()
        }
        bottom_dialog_web?.initWebViewSetting()
        bottom_dialog_web.setBackgroundColor(0) // 设置背景色
        bottom_dialog_web.background?.mutate()?.alpha = 0
        bottom_dialog_web.settings.useWideViewPort = true
        bottom_dialog_web.settings.loadWithOverviewMode = true
        bottom_dialog_web?.loadUrl(MeiUtil.appendGeneraUrl(load))
        net_error_layout.setOnClickListener {
            net_error_layout.isVisible = false
            bottom_dialog_web.isVisible = true
            bottom_dialog_web.loadUrl(MeiUtil.appendGeneraUrl(load))
        }
        bottom_dialog_web.webViewClient = object : MelkorWebViewClient() {
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                bottom_dialog_loading.start()
            }

            override fun handleLoadError() {
                super.handleLoadError()
                net_error_layout.isVisible = true
                bottom_dialog_web.isVisible = false
            }

            override fun handleUrlLoading(view: WebView?, url: String): Boolean {
                Log.i("handleUrlLoading", url)
                if (isAppLink(url)) {
                    when {
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
                        url.matches(AmanLink.URL.close_webview.toRegex()) -> {
                            if (parentFragment?.childFragmentManager?.backStackEntryCount == 1) dismissAllowingStateLoss()
                            else parentFragment?.childFragmentManager?.popBackStackImmediate()
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
                            val cancel = url.parseValue("cancel").getInt(0)
                            (activity as? MeiCustomActivity)?.showCouponDialog(couponId, couponNum, publisherId, cancel)
                        }
                        url.startsWith(AmanLink.URL.jump_home) -> {
                            //跳转至首页
                            postAction(SELECT_HOME, true)
                            Nav.toMain(activity)
                        }
                        url.startsWith(AmanLink.URL.use_coupon_apply_upstream) -> {
                            val couponNum = url.parseValue("couponNum").getInt(0)
                            (activity as? VideoLiveRoomActivity)?.launchCouponApply(couponNum)
                            dismissAllowingStateLoss()
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
                bottom_dialog_loading.stop()
                bottom_dialog_web.setBackgroundColor(0) // 设置背景色
                bottom_dialog_web.background?.mutate()?.alpha = 0
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        bottom_dialog_web?.destroy()
    }

    private fun toResourceInfo(resourceId: Int) {
        bottom_dialog_web.loadUrl("javascript:toResourceId($resourceId)")
    }
}

fun FragmentActivity.showCommonBottomDialog(loadUrl: String): CommonBottomFragment {
    val dialog = CommonBottomFragment().apply {
        this.load = appendRoomHalfScreenParamsUrl(loadUrl)
    }
    dialog.show(supportFragmentManager, "CommonBottomFragment")
    return dialog
}