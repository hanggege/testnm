package com.mei.base.common

/**
 *  Created by zzw on 2019-07-30
 *  Des:
 */

const val CANCEL_FOLLOW = "cancelFollow"
const val FOLLOW_NEW_MENTOR = "followNewMentor"
const val CHANG_LOGIN = "changLogin"//登录态变化
const val CHANG_GENDER = "changGender"//修改性别
const val CHANG_NICKNAME = "changeNickname"//昵称修改
const val SELECT_HOME = "select_home"//切换至首页
const val EMOTION_TEST_SUCCESS = "emotionTestSuccess"
const val WEB_REFRESH = "webRefresh"//web刷新
const val WEBVIEW_MESSAGE_CHANGE = "webviewMessageChange"//web自己进行通信
const val NOTIFY_ME_TAB = "notify_me_tab"
const val PAY_SUCCESS = "PAY_SUCCESS" //支付成功
const val FOLLOW_USER_STATE = "follow_user_action"//是否关注用户的状态
const val CLOSE_LOADING = "close_loading"
const val SHARE_WEB_GIO = "share_web_gio"//通知web分享埋点
const val RESET_WEB_VERSION = "reset_web_version"//重置web version

const val UPSTREAM_ACTION = "upstream_action"//上下麦通知
const val SHOW_RECOMMENT = "SHOW_RECOMMENT"//首次弹窗
const val CREATOR_UPSTREAM = "creator_upstream"//主播上麦了

const val SYSTEM_INVITE_OPEN_OFF = "SYSTEM_INVITE_OPEN_OFF" //系统邀请弹窗关闭与开启通知
const val DELETE_BLACKLIST = "DELETE_BLACKLIST" //移除黑名单
const val HAS_BLACKLIST = "HAS_BLACKLIST" //当前进入黑名单

const val MUTE_REMOTE_AUDIO_STREAMS = "muteAllRemoteAudioStreams" //关闭远端音频
const val WINDOW_MODE_CHANGE = "window_mode_change" //视频窗口模式变化
const val AUDIO_MODE_CHANGE = "audio_mode_change" //开闭麦
const val VIDEO_MODE_CHANGE = "video_mode_change" //开关视频流
const val ROOM_TYPE_CHANGE = "room_type_change" //房间类型改变
const val COURSE_PAY_SUCCESS = "COURSE_PAY_SUCCESS"// 课程充值成功通知
const val BIND_PHONE_ACTION = "BIND_PHONE_ACTION" //绑定手机号状态变化

const val MENTOR_PAGE_CHANGED = "mentor_page_changed"//导师主页信息发生改变
const val WORK_ROOM_CHANGED = "work_room_changed"//工作室信息改变

const val RECYCLE_SCROLL_STATE = "RECYCLE_SCROLL_STATE" //监听首页rv滑动状态

const val SHORT_VIDEO_LIST_TAB_CHANGE = "short_video_list_tab_changed" //短视频列表tab需要变化
const val SHORT_VIDEO_LIST_REFRESH = "short_video_list_refresh" //短视频列表刷新

const val SHORT_VIDEO_FIRST_EXIT_DETAIL = "SHORT_VIDEO_FIRST_EXIT_DETAIL" //首次观看短视频时推出详情
const val SHORT_VIDEO_COMPLETION = "SHORT_VIDEO_COMPLETION" //短视频播放完毕通知
const val SHORT_SWITCH_CHANGED = "SHORT_SWITCH_CHANGED" //短视频切换
const val SHORT_VIDEO_TAG_CLICK = "SHORT_VIDEO_TAG_CLICK" //短视频标签点击跳转广场通知
const val VIDEO_RENDERING_START = "VIDEO_RENDERING_START" //短视频加载完成


const val DRAWER_LAYOUT_STATE = "drawer_layout_state" //抽屉状态

const val COUPON_TO_SERVICE = "COUPON_TO_SERVICE" // 在优惠券弹窗点击使用由web端处理
const val COUPON_TO_LIVE = "COUPON_TO_LIVE" // 在直播间的知心小助手点击优惠券

const val PRAISE_STATE = "praise_state" //点赞状态

const val HONOR_MEDAL_CHANGED = "HONOR_MEDAL_CHANGED" //勋章数发生改变

const val SHIELD_USER = "SHIELD_USER" //拉黑用户
const val MESSAGE_DIALOG_SHIELD_USER = "MESSAGE_DIALOG_SHIELD_USER" // 直播间半屏消息拉黑刷新会话通知

const val CANCEL_SHIELD_USER = "CANCEL_SHIELD_USER" //取消拉黑
const val REFRESH_HOME_DATA = "REFRESH_HOME_DATA" //刷新HOME数据 --- 测评刷新问题

const val APPLY_UPSTREAM_ENABLE = "applyUpstreamEnable" //申请上麦按钮开关
const val APPLY_UPSTREAM_STATE = "apply_upstream_state"//申请上麦状态 true 申请 false 未申请

const val NEW_PEOPLE_GIFT_BAG_PAY_SUCCESS = "NEW_PEOPLE_GIFT_BAG_PAY_SUCCESS" //新人礼包支付成功

