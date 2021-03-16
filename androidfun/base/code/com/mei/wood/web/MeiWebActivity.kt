package com.mei.wood.web

import android.app.Activity
import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.webkit.URLUtil
import android.webkit.WebView
import androidx.core.view.isGone
import androidx.core.view.isVisible
import com.joker.im.custom.chick.InviteJoinInfo
import com.mei.orc.ActivityLauncher
import com.mei.player.view.IgnorePlayerBar
import com.mei.wood.R
import com.mei.wood.dialog.NormalDialogLauncher
import com.mei.wood.event.ISystemInviteJoinShow
import com.mei.wood.ext.AmanLink
import com.mei.wood.ui.MeiCustomActivity
import com.mei.wood.util.MeiUtil
import com.net.model.broadcast.PlayType
import com.pili.getPlayInfo
import kotlinx.android.synthetic.main.mei_web_layout.*
import launcher.Boom
import launcher.MakeResult

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2018/8/30
 *
 * 通用Web界面
 */
@MakeResult(includeStartForResult = true)
open class MeiWebActivity : MeiCustomActivity(), ISystemInviteJoinShow, IgnorePlayerBar {

    @Boom
    open var webData: MeiWebData = MeiWebData()

    /**web内部页码**/
    var index: Int = 0

    /**web内部有改变，退出需要弹框提示**/
    var webHasChange: Boolean = false
    var needRefresh = false

    /**清除WebView的历史记录，当需要清除时需要赋值true*/
    var clearHistory = false

    /**第一次加载用遮罩*/
    var first = true

    /**当前页面是否展示系统邀请弹窗*/
    var isSystemInviteJoinShow = true

    /** 当前页面是否显示播放条 **/
    var isShowPlayerBar = true

    var refreshMethod = ""


    val openFileClient: OpenFileWebChromeClient by lazy { OpenFileWebChromeClient(this) }
    open val meiWebClient: MelkorWebViewClient by lazy {
        object : MelkorWebViewClient() {
            override fun handleUrlLoading(view: WebView?, url: String): Boolean {
                return if (subIntercept(url)) true else handleLink(url)
            }

            override fun handleLoadError() {
                super.handleLoadError()
                net_error_layout.isVisible = true
                if (first) {
                    mei_web?.postDelayed({
                        loadFinish()
                        first = false
                    }, 500)
                } else {
                    loadFinish()
                }
            }

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)

                if (first) {
                    showLoadingCover()
                } else {
                    if (webData.not_show_loading != 1) {
                        showLoading(true)
                    }
                }

                net_error_layout.isGone = true
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                if (first) {
                    mei_web?.postDelayed({
                        loadFinish()
                        first = false
                    }, 500)
                } else {
                    loadFinish()
                }


            }
        }
    }

    /**
     * 如果继承这个能用Web的话，是否拦截内链处理
     */
    open fun subIntercept(url: String): Boolean = false

    open fun loadFinish() {
        if (!isFinishing) {
            showLoading(false)
            mei_refresh.isRefreshing = false
            if (webData.title.isNullOrEmpty()) {
                web_title.visibility = View.VISIBLE
                web_title.text = mei_web.title
            }
            if (clearHistory) {
                mei_web.clearHistory()
                clearHistory = false
            }
        }
    }

    override fun customStatusBar(): Boolean = true


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        readWebData()
        setContentView(R.layout.mei_web_layout)
        if (URLUtil.isNetworkUrl(webData.url)) {
            initView()
            initWeb()
        } else {
            finish()
        }

    }

    override fun onResume() {
        super.onResume()
        if (needRefresh) {
            mei_web.reload()
            needRefresh = false
        }
    }

    override fun onBackPressed() {
        when {
            webHasChange -> NormalDialogLauncher.newInstance().showDialog(this, "填写内容未提示,是否退出？", okBack = { finish() })
            index > 0 || webData.go_back_android == 1 -> {
                if (net_error_layout.isGone) mei_web.loadUrl("javascript:goBack()")
            }
            mei_web.canGoBack() -> mei_web.goBack()
            else -> super.onBackPressed()
        }
    }

    override fun finish() {
        setResult(Activity.RESULT_OK, intent)
        super.finish()
    }

    override fun isShow(sendId: String, info: InviteJoinInfo): Boolean = isSystemInviteJoinShow

    override fun isShow(): Boolean = isShowPlayerBar && getPlayInfo().mPlayType != PlayType.LISTENING_RADIO

    open fun readWebData() {
        when {
            intent.dataString?.matches(AmanLink.URL.NEW_JSON_WEBVIEW.toRegex()) == true -> {
                webData = MeiUtil.getJsonObject(intent.dataString.orEmpty(), AmanLink.URL.NEW_JSON_WEBVIEW)
                        ?: MeiWebData()
            }
            intent.dataString?.matches(AmanLink.URL.NEW_WEBVIEW.toRegex()) == true -> {
                val url = MeiUtil.getOneID(intent.dataString.orEmpty(), AmanLink.URL.NEW_WEBVIEW)
                webData = MeiWebData(url, "")
            }
            else -> ActivityLauncher.bind(this)
        }

    }

    /**
     * 设置状态栏高度
     */
    open fun setStatusBarHeight(height: Int) {
        mei_web.loadUrl("javaScript:setBarStatusHeight($height)")
    }

}