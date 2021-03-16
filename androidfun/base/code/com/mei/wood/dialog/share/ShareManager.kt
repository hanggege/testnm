package com.mei.wood.dialog.share

import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.joker.TdManager
import com.joker.TdType
import com.joker.connect.TdCallBack
import com.joker.constant.TdErrorCode
import com.joker.flag.ClientFlags
import com.joker.flag.TdFlags
import com.joker.model.BackResult
import com.joker.model.ErrResult
import com.joker.model.ShareInfo
import com.mei.base.common.SHARE_WEB_GIO
import com.mei.orc.Cxt
import com.mei.orc.event.postAction
import com.mei.orc.ui.loading.LoadingToggle
import com.mei.orc.ui.toast.UIToast
import com.mei.orc.util.app.copyToClipboard
import com.mei.provider.ProjectExt
import com.mei.wood.R
import com.mei.wood.dialog.share.td.TdImageAdapterImpl
import com.mei.wood.util.MeiUtil

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2018/8/29
 *
 * 分享管理类
 */

const val CMD_MENTOR = "userpage"
const val CMD_FEED = "elite"
const val CMD_COURSE = "course"
const val CMD_BROADCAST = "broadcast"

const val XIAOLU_MINIPROGRAM_ID = "gh_6c35527f96a5"

data class ShareCmd @JvmOverloads constructor(val type: String, val refer_id: String, val ext1: String = "")

data class ShareData @JvmOverloads constructor(val title: String = "",
                                               val content: String = "",
                                               val imgUrl: String = "",
                                               val shareUrl: String = "",
                                               val amanLink: String = "",//内链
                                               val defaultRes: Int = R.drawable.knack_share_img,//默认分享图片
                                               val needCutWeb: Boolean = false,//需要截取webview
                                               /** share type [TdType]  分享纯图片时只传imgUrl defaultRes type = [TdType.image_share]**/
                                               val type: Int = TdType.multi_share,
                                               /** 与 [TdType.mini_program_share] 对应**/
                                               val miniProgramPath: String = "",
                                               /** 小鹿情感+的默认id **/
                                               val miniProgramId: String = XIAOLU_MINIPROGRAM_ID,
                                               /** 直达口令**/
                                               val codeCmd: ShareCmd? = null,
                                               /** 指定显示方式,定制化 **/
                                               val itemType: Int = 0,
                                               /**点击回调 用于打点上传数据 type是ShowFlag**/
                                               val onClick: (type: Int) -> Unit = {})

class ShareManager {
    companion object {
        val instance: ShareManager by lazy { ShareManager() }
    }

    @JvmOverloads
    fun show(activity: FragmentActivity?, data: ShareData, cutWeb: () -> Unit = {}) {
        activity?.let { at ->
            val itemType = when {
                data.itemType != 0 -> data.itemType
                /** 仅分享图片 纯图片 **/
                data.type == TdType.image_share -> TYPE_ITEM_1
                data.needCutWeb -> TYPE_ITEM_3
                data.codeCmd != null -> TYPE_ITEM_4
                else -> TYPE_ITEM_2
            }
            ShareKtDialog().showDialog(at, itemType) {
                /** 长图是用来截取webview,返回业务层，可能需要跳转或调用js **/
                if (it == long_image) {
                    cutWeb()
                } else {
                    data.onClick(it)
                    handleShareChoice(at, it, data)
                    /** 通知web数据埋点 */
                    if (it == wechat) {
                        postAction(SHARE_WEB_GIO, 1)
                    } else if (it == wechat_circle) {
                        postAction(SHARE_WEB_GIO, 2)
                    }
                }
            }
        }
    }

    fun handleShareChoice(activity: FragmentActivity, @ShowFlag type: Int, data: ShareData, backResult: (result: Boolean) -> Unit = { _ -> }) {
        when (type) {
            /**分享到微信**/
            wechat -> shareToThird(activity, provideShareInfoByClient(data, TdType.we_chat), TdType.weixin, backResult)
            /**分享到朋友圈**/
            wechat_circle -> shareToThird(activity, provideShareInfoByClient(data, TdType.friend_circle), TdType.weixin, backResult)
            /**分享到微博**/
            weibo -> shareToThird(activity, provideShareInfoByClient(data, TdType.we_chat), TdType.weibo, backResult)
            copy -> if (activity.copyToClipboard(appendParameters(data.shareUrl))) {
                UIToast.toast(activity, activity.getString(R.string.msg_copy_success))
            }
        }

    }


    /**
     * 所有的url后面追回 share_from_app=xiaolu 用来区分来源
     */
    private fun appendParameters(url: String): String = MeiUtil.appendParamsUrl(url, "share_from_app" to ProjectExt.share_from_app)

    private fun getTitle(data: ShareData): String = if (data.title.isEmpty()) data.content else data.title
    private fun getContent(data: ShareData): String = if (data.content.isEmpty()) data.title else data.content

    /**
     * 通过分享到的客户端组装[ShareInfo]
     */
    private fun provideShareInfoByClient(data: ShareData, @ClientFlags client: Int): ShareInfo {
        return if (data.type == TdType.image_share) {
            ShareInfo(client, data.imgUrl, data.defaultRes)
        } else if (data.type == TdType.mini_program_share
                && data.miniProgramPath.isNotEmpty()) {
            val info = ShareInfo(client, getTitle(data), getContent(data), data.imgUrl,
                    data.defaultRes, appendParameters(data.shareUrl), data.amanLink, Cxt.getStr(R.string.app_name))
            /**分享小程序，暂时只支持消息。不支持朋友圈小程序**/
            if (client == TdType.we_chat) {
                val id = if (data.miniProgramId.isNotEmpty()) data.miniProgramId else XIAOLU_MINIPROGRAM_ID
                info.miniAppInfo = ShareInfo.MiniAppInfo(data.miniProgramPath, id)
                info.shareType = TdType.mini_program_share
            }
            info
        } else {
            ShareInfo(client, getTitle(data), getContent(data), data.imgUrl,
                    data.defaultRes, appendParameters(data.shareUrl), data.amanLink, "小鹿")
        }

    }

    /**
     * 统一分享已经组装好的信息
     */
    private fun shareToThird(activity: FragmentActivity, info: ShareInfo, @TdFlags type: Int, backResult: (result: Boolean) -> Unit = { _ -> }) {
        val success = TdManager.performShare(activity, type, info, TdImageAdapterImpl(activity),
                object : TdCallBack {
                    override fun onSuccess(success: BackResult) {
                        UIToast.toast(activity, "分享成功")
                        if (activity is LoadingToggle) activity.showLoading(false)
                        backResult(true)
                    }

                    override fun onFailure(errResult: ErrResult) {
                        if (errResult.code != TdErrorCode.READING_TO_SHARE) {
                            UIToast.toast(activity, errResult.msg)
                        }
                        if (activity is LoadingToggle) activity.showLoading(false)
                        backResult(false)
                    }

                })
        if (success && activity is LoadingToggle) {
            activity.showLoading(true)
            val life: LifecycleObserver = object : LifecycleEventObserver {
                override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                    if (event == Lifecycle.Event.ON_RESUME) {
                        activity.showLoading(false)
                        activity.lifecycle.removeObserver(this)
                    }
                }
            }
            activity.lifecycle.addObserver(life)
        }

    }

}