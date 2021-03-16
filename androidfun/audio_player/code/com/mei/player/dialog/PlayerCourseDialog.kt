package com.mei.player.dialog

import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.URLUtil
import android.webkit.WebView
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.FragmentActivity
import com.live.player.presenter.ListenAudioView
import com.mei.base.ui.nav.Nav
import com.mei.base.util.web.initWebViewSetting
import com.mei.base.weight.loading.LoadingCreator
import com.mei.orc.dialog.BottomDialogFragment
import com.mei.orc.ext.screenHeight
import com.mei.orc.util.click.isNotOnDoubleClick
import com.mei.wood.R
import com.mei.wood.ext.AmanLink
import com.mei.wood.util.MeiUtil
import com.mei.wood.util.parseValue
import com.mei.wood.web.MelkorWebViewClient
import com.net.model.broadcast.PlayType
import com.pili.*
import kotlinx.android.synthetic.main.include_net_error_layout.*
import kotlinx.android.synthetic.main.player_course_dialog.*

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2020/7/9
 */
fun FragmentActivity?.showPlayerCourseDialog(courseId: Int, playOtherBack: (audioId: Int) -> Unit) {
    this?.supportFragmentManager?.let { manager ->
        PlayerCourseDialog().apply {
            this.courseId = courseId
            this.playOtherBack = playOtherBack
        }.show(manager, "PlayerCourseDialog")
    }
}

class PlayerCourseDialog : BottomDialogFragment(), ListenAudioView {

    var playOtherBack: (audioId: Int) -> Unit = {}
    var courseId: Int = 0
    private var currentAudioId: Int = 0
    private var firstLoadFinish = false
    private val loadingView: LoadingCreator by lazy { LoadingCreator(root_container) }
    private val loadUrl: String by lazy {
        MeiUtil.appendParamsUrl(AmanLink.NetUrl.course_list_url,
                "course_id" to courseId.toString(), "from" to "player")
    }

    private val webClient: MelkorWebViewClient by lazy {
        object : MelkorWebViewClient() {
            override fun handleUrlLoading(view: WebView?, url: String): Boolean {
                when {
                    url.startsWith(AmanLink.URL.COURSE_SERVICE_PLAYER) -> {
                        //播放课程操作处理
                        val audioId = url.parseValue("id", 0)
                        val playerStatus = url.parseValue("playerStatus", 0)
                        if (playerStatus == 1) {
                            if (!PlayType.LISTENING_SERVICE.isPlaySelf(audioId) || playerIsCompletion()) {
                                playOtherBack(audioId)
                            } else if (!player.isPlaying()) {
                                player.resume()
                            }
                        } else if (getPlayInfo().mAudioId == audioId) {
                            player.pause()
                        } else {
                            player.stop()
                        }
                    }
                    url.matches(AmanLink.URL.close_webview.toRegex()) -> {
                        dismissAllowingStateLoss()
                    }
                    isAppLink(url) -> {
                        Nav.toAmanLink(activity, url)
                    }
                    URLUtil.isNetworkUrl(url) -> {
                        return false
                    }
                }
                return true
            }

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                if (isAdded)
                    net_error_layout.isVisible = false
                loadingView.showLoading(true)
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                loadingView.showLoading(false)
                firstLoadFinish = true
                notifyWebPlayState()
            }

            override fun handleLoadError() {
                super.handleLoadError()
                if (isAdded)
                    net_error_layout.isVisible = true
            }

        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.player_course_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        currentAudioId = getPlayInfo().mAudioId
        course_webview.setBackgroundColor(0) // 设置背景色
        course_webview.background?.mutate()?.alpha = 0
        course_webview.initWebViewSetting()
        course_webview.webViewClient = webClient
        course_webview.loadUrl(MeiUtil.appendGeneraUrl(loadUrl))
        root_container.updateLayoutParams { height = screenHeight * 2 / 3 }
        net_error_layout.setOnBtnClick(View.OnClickListener { course_webview.reload() })
    }

    /****************************[ListenAudioView]************************************/
    private var currentPercent = 0
    override fun onCompletion() {
        currentPercent = 100
        notifyWebPlayState()
    }

    override fun onSeekComplete() {
        currentPercent = audioProgress
        notifyWebPlayState()
    }

    override fun onPrepared(preparedTime: Int) {
        currentAudioId = getPlayInfo().mAudioId
    }

    override fun onStopPlayer() {
        notifyWebPlayState()
    }

    override fun onInfo(what: Int, extra: Int) {
        currentPercent = audioProgress
        isNotOnDoubleClick(1000, "reportPercent") {
            notifyWebPlayState()
        }
    }

    override fun onError(code: Int) {

    }

    private fun notifyWebPlayState() {
        if (isAdded && firstLoadFinish) {
            course_webview.loadUrl("javascript:courseProgress(${currentPercent},${currentAudioId})")
            course_webview.loadUrl("javascript:coursePlaying(${if (player.isPlaying()) 1 else 0},${currentAudioId})")
        }
    }
}