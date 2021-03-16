package com.mei.im.ui.dialog

import android.content.Context
import android.graphics.Bitmap
import android.os.Bundle
import android.view.*
import android.webkit.WebView
import android.widget.FrameLayout
import android.widget.RelativeLayout
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.FragmentActivity
import com.joker.im.custom.chick.ChickCustomData
import com.mei.base.ui.nav.Nav
import com.mei.base.util.web.initWebViewSetting
import com.mei.base.weight.loading.LoadingCreator
import com.mei.orc.dialog.BottomDialogFragment
import com.mei.orc.ext.dip
import com.mei.orc.ext.screenHeight
import com.mei.orc.ext.screenWidth
import com.mei.orc.ui.loading.LoadingToggle
import com.mei.orc.util.json.json2Obj
import com.mei.widget.webview.InitWebView
import com.mei.wood.ext.AmanLink
import com.mei.wood.util.MeiUtil
import com.mei.wood.util.parseValue
import com.mei.wood.web.MelkorWebViewClient
import launcher.Boom
import java.lang.ref.WeakReference
import kotlin.math.roundToInt

/**
 *
 * @Version:
 */
class IMServiceWebBottomFragmentDialog : BottomDialogFragment() {
    @Boom
    var load: String? = null

    var mWebView: WebView? = null

    private var loadingView: LoadingCreator? = null
    var callBack: (data: ChickCustomData.Result?) -> Unit = { _ -> }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val wr = WeakReference<Context>(activity)

        val fl = wr.get()?.let { FrameLayout(it) }

        val layoutParams = FrameLayout.LayoutParams(dip(30), dip(30))

        val rl = wr.get()?.let {
            RelativeLayout(it)
        }
        layoutParams.gravity = Gravity.CENTER
        rl?.layoutParams = layoutParams
        rl?.gravity = Gravity.CENTER

        mWebView = wr.get()?.let { InitWebView(it) }
        mWebView?.layoutParams = FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT
                , ViewGroup.LayoutParams.MATCH_PARENT)

        fl?.addView(mWebView)
        fl?.addView(rl)
        loadingView = rl?.let { LoadingCreator(rl) }
        loadingView?.apply {
            viewGroup?.layoutParams = layoutParams
        }
        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        return fl
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadWebURL()
    }

    private fun loadWebURL() {
        mWebView?.updateLayoutParams {
            width = screenWidth
            height = (screenHeight * 0.7).roundToInt()
        }
        mWebView?.initWebViewSetting()
        mWebView?.loadUrl(MeiUtil.appendGeneraUrl(load))
        mWebView?.webViewClient = object : MelkorWebViewClient() {


            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                loadingView?.showLoading(true)
            }

            override fun handleUrlLoading(view: WebView?, url: String): Boolean {


                when {
                    url.startsWith(AmanLink.URL.USER_CHAT_SEND_MESSAGE) -> {
                        val data: String? = url.parseValue("custom_message")
                        if (data?.isNotEmpty() == true) {
                            val result = data.json2Obj<ChickCustomData.Result>()
                            result?.apply {
                                apply(callBack)
                                dismissAllowingStateLoss()
                            }
                        }
                    }
                    url.matches(AmanLink.URL.close_webview.toRegex()) -> {
                        //拦截关闭网易内链,关闭dialog
                        dismissAllowingStateLoss()

                    }
                    isWebAppLink(url) -> {
                        Nav.toAmanLink(activity, url)
                    }
                }

                return true
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                (activity as? LoadingToggle)?.showLoading(false)
                mWebView?.setBackgroundColor(0) // 设置背景色
                mWebView?.background?.alpha = 0
                loadingView?.showLoading(false)
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        mWebView?.destroy()
    }


}

fun FragmentActivity.showIMServiceWebBottomFragmentDialog(loadUrl: String, callBack: (data: ChickCustomData.Result?) -> Unit = {}): BottomDialogFragment {
    val dialog = IMServiceWebBottomFragmentDialogLauncher.newInstance(loadUrl).apply {
        this.callBack = callBack
        this.load = loadUrl
    }
    dialog.show(supportFragmentManager, "IMServiceWebBottomFragmentDialog")
    return dialog
}
