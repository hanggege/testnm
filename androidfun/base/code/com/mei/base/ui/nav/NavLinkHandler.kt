package com.mei.base.ui.nav

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.text.TextUtils
import androidx.fragment.app.FragmentActivity
import com.joker.im.custom.chick.ChickCustomData
import com.joker.support.TdSdk
import com.mei.GrowingUtil
import com.mei.base.common.CANCEL_FOLLOW
import com.mei.base.common.DELETE_BLACKLIST
import com.mei.base.common.FOLLOW_NEW_MENTOR
import com.mei.base.common.SHORT_VIDEO_TAG_CLICK
import com.mei.base.util.dialog.ifUserConfirm
import com.mei.browser.BrowserInfo
import com.mei.chat.toImChat
import com.mei.course_service.ui.MeiWebCourseServiceActivityLauncher
import com.mei.dialog.parsePayFrom
import com.mei.dialog.showPayDialog
import com.mei.dialog.showPeopleGiftBagDialog
import com.mei.dialog.showReportDialog
import com.mei.im.ui.dialog.showChatDialog
import com.mei.im.ui.fragment.toReceiveCourse
import com.mei.intimate.ShortVideoListActivityLauncher
import com.mei.live.LiveEnterType
import com.mei.live.parseLiveEnterType
import com.mei.live.ui.StartLiveRoomActivityLauncher
import com.mei.live.ui.VideoLiveRoomActivity
import com.mei.live.ui.VideoLiveRoomActivityLauncher
import com.mei.live.ui.dialog.showExclusiveServiceDialog
import com.mei.live.ui.ext.getCurrLiveState
import com.mei.live.ui.fragment.appendRoomHalfScreenParamsUrl
import com.mei.live.views.launchCouponApply
import com.mei.login.checkLogin
import com.mei.login.toLogin
import com.mei.mentor_home_page.ui.MentorHomePageActivity
import com.mei.mentor_home_page.ui.MentorHomePageActivityLauncher
import com.mei.orc.callback.Callback
import com.mei.orc.dialog.BottomSelectFragment
import com.mei.orc.event.postAction
import com.mei.orc.john.model.JohnUser
import com.mei.orc.rxpermission.requestMulPermissionZip
import com.mei.orc.ui.toast.UIToast
import com.mei.orc.util.app.copyToClipboard
import com.mei.orc.util.json.json2Obj
import com.mei.orc.util.permission.PermissionCheck
import com.mei.orc.util.string.getInt
import com.mei.player.PlayerHandleActivityLauncher
import com.mei.push.Payload
import com.mei.radio.MeiRadioPlayerActivity
import com.mei.short_video.SquareOrRecommendActivity
import com.mei.short_video.SquareOrRecommendActivityLauncher
import com.mei.video.ShortVideoPlayerNewActivityLauncher
import com.mei.wood.R
import com.mei.wood.dialog.DISSMISS_DO_OK
import com.mei.wood.dialog.NormalDialogLauncher
import com.mei.wood.ext.AmanLink
import com.mei.wood.ext.Constants
import com.mei.wood.modle.TAB_ITEM
import com.mei.wood.rx.SilentSubscriber
import com.mei.wood.ui.MeiCustomActivity
import com.mei.wood.ui.MeiCustomBarActivity
import com.mei.wood.ui.TabMainActivity
import com.mei.wood.util.*
import com.mei.wood.util.MeiUtil.getJsonObject
import com.mei.wood.util.MeiUtil.getOneID
import com.mei.wood.web.MeiWebData
import com.net.MeiUser

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2018/6/13
 *
 *
 * 专门处理全局的内链
 */
internal object NavLinkHandler {
    /**
     * @param context
     * @param url
     * @return 是否已处理
     */
    @JvmStatic
    fun handLink(context: Context, url: String): Boolean {
        try {
            val activity: FragmentActivity? = context as? FragmentActivity
            if (url.matches(AmanLink.URL.USER_LOGIN_CHECK.toRegex())) { //登录检查
                if (!JohnUser.getSharedUser().hasLogin()) {
                    context.toLogin()
                }
            } else if (url.matches(AmanLink.URL.USER_LOGIN.toRegex()) || url.matches(AmanLink.URL.USER_LOGIN_NEW.toRegex())) {
                context.toLogin()
            } else if (url.matches(AmanLink.URL.GO_TO_LOGIN_DIRECTLY.toRegex())) { //直接打开登录页面
                context.checkLogin()
            } else if (url.matches(AmanLink.URL.BASIC_ALERT_SHOW.toRegex())) { //dialog弹框
                // 接受webView 弹出对话框并且处理时间
                val commentDialog = getJsonObject(url, AmanLink.URL.BASIC_ALERT_SHOW, CommentDialog::class.java)
                if (commentDialog != null) {
                    context.ifUserConfirm<Any?>("", commentDialog.msg, commentDialog.action_title, context.getString(R.string.cancel), commentDialog.canCancel, null)
                            .subscribe(object : SilentSubscriber<Any?>() {
                                override fun onNext(aBoolean: Any) {
                                    Nav.toAmanLink(context, commentDialog.action_url)
                                }
                            })
                }
            } else if (url.matches(AmanLink.URL.show_toast.toRegex())) { //show toast
                val tips = getOneID(url, AmanLink.URL.show_toast)
                UIToast.toast(context, tips)
            } else if (url.matches(AmanLink.URL.PHOTO_GALLERY.toRegex())) {
                // 查看大图
                val imageInfo = getJsonObject(url, AmanLink.URL.PHOTO_GALLERY, BrowserInfo::class.java)
                if (imageInfo != null && imageInfo.index >= 0) {
                    if (!url.contains("downloadable")) imageInfo.downloadable = true
                    Nav.toViewImages(context, imageInfo)
                }
            } else if (url.matches(AmanLink.URL.close_webview.toRegex()) && context is Activity) {
                context.finish()
            } else if (url.matches(AmanLink.URL.CANCELFOLLOWMENTOR_FROMWEB.toRegex())) { //web中取消关注通知主页刷新
                val mentorID = getOneID(url, AmanLink.URL.CANCELFOLLOWMENTOR_FROMWEB, Int::class.java, 0)
                if (mentorID != 0) {
                    "NavLinkHandler".postAction(CANCEL_FOLLOW, mentorID)
                }
            } else if (url.matches(AmanLink.URL.FOLLOWMENTOR_FROMWEB.toRegex())) { //web中加关注通知主页刷新
                "NavLinkHandler".postAction(FOLLOW_NEW_MENTOR, true)
//            } else if (url.matches(AmanLink.URL.WECHAT_ADD.toRegex())) { //加微信
//                val threeID = getRegexId2(url, AmanLink.URL.WECHAT_ADD)
//                // 先取到微信信息
//                if (activity != null && threeID.size >= 3) {
//                    var mainAppGNData: MainAppGNData? = null
//                    if (threeID[1] == "broadcast" && threeID.size == 5) {
//                        mainAppGNData = MainAppGNData(threeID[3], "", "直播", threeID[2], threeID[4], threeID[0])
//                    }
//                    activity.addWechatNumber(threeID[0].getInt(0), threeID[1], threeID[2], mainAppGNData)
//                }
            } else if (url.matches(AmanLink.URL.COPY_WORDS.toRegex())) {
                val content = getOneID(url, AmanLink.URL.COPY_WORDS)
                if (activity != null && !TextUtils.isEmpty(content)) {
                    BottomSelectFragment().addItem("复制").setItemClickListener(Callback {
                        if (activity.copyToClipboard(content)) {
                            UIToast.toast(activity, "复制成功")
                        }
                    }).show(activity.supportFragmentManager)
                }
            } else if (url.matches(AmanLink.URL.goto_mini_program.toRegex())) {
                TdSdk.openMiniProgram(context,
                        url.parseJsonValue("mini_app_name", ""),
                        url.parseJsonValue("mini_path", ""))
            } else if (url.matches(AmanLink.URL.SHARE_FROM_WEB.toRegex())) { // 分享
                activity?.shareFromWeb(url)
            } else if (url.matches(AmanLink.URL.SHARE_TARGET_WEB.toRegex())) { // 直接分享到第三方

                activity?.shareTargetWeb(url)
            } else if (url.matches(AmanLink.URL.OPEN_SYSTEM_NET_CLIENT.toRegex())) { // 打开系统浏览器
                intentToSystem(context, url)
            } else if (url.matches(AmanLink.URL.tab_select.toRegex())) { // 跳转到主页的tab
                val index = url.parseValue("tabbar", "")
                context.postAction(Constants.RxBusTag.TabbarIndexSelected, index)
                Nav.toMain(context)
            } else if (url.startsWith(AmanLink.URL.room_blacklist_delete)) {
                if (context is FragmentActivity) {
                    //取消拉黑
                    val userId = url.parseValue("userId", "")
                    context.postAction(DELETE_BLACKLIST, userId)
                }
            } else if (url.startsWith(AmanLink.URL.report_user)) {
                //举报
                val userId = url.parseValue("userId", 0)
                activity?.showReportDialog(userId, "")
            } else if (url.startsWith(AmanLink.URL.jump_to_video_live)) {
                if (activity != null) {
                    if (JohnUser.getSharedUser().hasLogin()) {
                        val liveEnterType = parseLiveEnterType(url.parseValue("liveEnterType"))
                        val roomId = url.parseValue("roomId")
                        VideoLiveRoomActivityLauncher.startActivity(activity, roomId, liveEnterType)
                    } else {
                        //没有登陆直接到首页
                        context.startActivity(Intent(context, TabMainActivity::class.java))
                    }
                }
            } else if (url.startsWith(AmanLink.URL.show_pay_dialog)) {
                if (activity != null) {
                    val fromType = url.parseValue("from")
                    activity.showPayDialog(fromType.parsePayFrom())
                }
            } else if (url.startsWith(AmanLink.URL.jump_personal_page)) {
                //跳转个人主页
                if (activity != null) {
                    val userId = url.parseValue("userId", 0)
                    val tabSelection = url.parseValue("tab", 0)
                    if (userId > 0) {
                        if (AppManager.getInstance().hasActivitys(MentorHomePageActivity::class.java, VideoLiveRoomActivity::class.java).size == 2) {
                            AppManager.getInstance().finishActivity(MentorHomePageActivity::class.java)
                        }
                        MentorHomePageActivityLauncher.startActivity(activity, userId, tabSelection)
                    }
                }
            } else if (url.startsWith(AmanLink.URL.jump_short_video_list_page)) {
                val tagId = url.parseValue("tag_id").getInt(0)
                ShortVideoListActivityLauncher.startActivity(activity, tagId)
            } else if (url.startsWith(AmanLink.URL.jump_short_video_square_page)) {
                val tagId = url.parseValue("tag_id").getInt(0)

                if (activity is TabMainActivity) {
                    activity.setCurrentTabItem(TAB_ITEM.SHORT_VIDEO)
                } else {
                    if (activity !is SquareOrRecommendActivity) {
                        activity?.startActivity(Intent(activity, TabMainActivity::class.java))
                    }
                }
                postAction(SHORT_VIDEO_TAG_CLICK, tagId)
            } else if (url.startsWith(AmanLink.URL.AUDIO_PLAYER)) {
                if (activity != null) {
                    //跳转到音频播放器
                    UIToast.toast(activity, when (getCurrLiveState()) {
                        1 -> "当前正在直播中，无法播放"
                        2 -> "当前正在连线中，无法播放"
                        else -> {
                            val audioId = url.parseValue("audioId", 0)
                            PlayerHandleActivityLauncher.startActivity(activity, audioId)
                            ""
                        }
                    })
                }
            } else if (url.startsWith(AmanLink.URL.IM_CHAT_C2C)) {
                val userId = url.parseValue("user_id")
                activity?.toImChat(userId)
            } else if (url.startsWith(AmanLink.URL.COURSE_PAY_DIALOG)) {
                val payUrl = url.parseValue("url")
                activity?.showExclusiveServiceDialog(payUrl)
            } else if (url.startsWith(AmanLink.URL.COURSE_SERVICE_WEBVIEW)) {
                val allValue = url.parseAllValues()
                var resultUrl = url.parseValue("url")
                allValue.filter { it.key != "url" }.forEach {
                    resultUrl = MeiUtil.appendParamsUrl(resultUrl, it.key to it.value)
                }
                MeiWebCourseServiceActivityLauncher.startActivity(context, MeiWebData(resultUrl, 0))
            } else if (url.startsWith(AmanLink.URL.HALF_SCREEN_COURSE_SERVICE_WEBVIEW)) {
                /**直播间半屏课程详情弹框*/
                val allValue = url.parseAllValues()
                var resultUrl = url.parseValue("url")
                val roomId = url.parseValue("room_id")
                allValue.filter { it.key != "url" }.forEach {
                    resultUrl = MeiUtil.appendParamsUrl(resultUrl, it.key to it.value)
                }
                activity?.showExclusiveServiceDialog(activity.appendRoomHalfScreenParamsUrl(resultUrl), roomId)
            } else if (url.startsWith(AmanLink.URL.start_common_live)) {
                if (activity.checkLogin()) {
                    activity?.requestMulPermissionZip(Manifest.permission.RECORD_AUDIO, Manifest.permission.CAMERA) {
                        // 两个权限都需要才能连线
                        if (it) {
                            StartLiveRoomActivityLauncher.startActivity(activity, MeiUser.getSharedUser().info?.groupInfo)
                        } else {
                            NormalDialogLauncher.newInstance().showDialog(activity, "用户连线需要摄像头和录音权限，请打开相应权限", back = { result ->
                                if (result == DISSMISS_DO_OK) {
                                    PermissionCheck.gotoPermissionSetting(activity)
                                }
                            })
                        }
                    }
                }
            } else if (url.startsWith(AmanLink.URL.USER_CHAT_SEND_MESSAGE)) {
                //发送自定义消息
                val values = url.parseAllValues()
                val data: String? = values["custom_message"]
                if (data?.isNotEmpty() == true) {
                    val result = data.json2Obj<ChickCustomData.Result>()
                    if (result != null) {
                        var publishUserId = 0
                        result.data?.serviceInfo?.apply {
                            publishUserId = publisherId
                        }
                        result.data?.courseInfo?.apply {
                            publishUserId = userId
                        }
                        val roomId = result.data?.roomId
                        if (roomId?.isNotEmpty() == true) {
                            //直播中
                            VideoLiveRoomActivityLauncher.startActivity(context, roomId, LiveEnterType.course_introduce_page, data)
                        } else {
                            context.toImChat(publishUserId.toString(), false, result)
                        }
                    }
                }
            } else if (url.startsWith(AmanLink.URL.SHORT_VIDEO_PLAYER)) {
                //短视频详情内链
                if (url.parseValue("seqId").isNotEmpty()) {
                    ShortVideoPlayerNewActivityLauncher.startActivity(context, url.parseValue("seqId"), url.parseValue("tagId"), url.parseValue("isMyLike", false), url.parseValue("isAllTag", false), url.parseValue("videoCoverUrl"), url.parseValue("fromType"), url.parseValue("publisherId", 0).toString())
                }
            } else if (url.startsWith(AmanLink.URL.SQUARE_RECOMMEND_PAGE)) {
                SquareOrRecommendActivityLauncher.startActivity(context, url.parseValue("tabId"), url.parseValue("tagId", 0))
            } else if (url.startsWith(AmanLink.URL.jump_radio)) {
                activity?.startActivity(Intent(activity, MeiRadioPlayerActivity::class.java))
            } else if (url.matches(AmanLink.URL.gio_track_web.toRegex())) {
                val gioTrackWeb = getJsonObject<Payload.GioReport>(url, AmanLink.URL.gio_track_web)
                gioTrackWeb?.let {
                    GrowingUtil.track(it.gioEventKey, it.gioValue)
                }
            } else if (url.startsWith(AmanLink.URL.open_live_room_chat)) {
                /**拉起直播间半屏聊天页面*/
                activity?.showChatDialog(url.parseValue("chatId"), url.parseValue("from"))
            } else if (url.startsWith(AmanLink.URL.to_receive_course)) {
                /**领取课程操作*/
                (activity as? MeiCustomActivity)?.toReceiveCourse(url.parseValue("userId")
                        , url.parseValue("publisherId")
                        , url.parseValue("referType")
                        , url.parseValue("referId"))
            } else if (url.startsWith(AmanLink.URL.new_people_gift_bag_dialog)) {
                (activity as? MeiCustomBarActivity)?.showPeopleGiftBagDialog()
            } else {
                return false
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return true
    }

    /**
     * 调起系统浏览器
     */
    private fun intentToSystem(context: Context, url: String) {
        try {
            val wapUrl = getOneID(url, AmanLink.URL.OPEN_SYSTEM_NET_CLIENT)
            val intent = Intent()
            intent.action = "android.intent.action.VIEW"
            val contentUrl = Uri.parse(wapUrl)
            intent.data = contentUrl
            context.startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}