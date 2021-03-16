package com.mei.live

import android.util.Log

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2019-12-15
 */

//红娘自主开房间、自主进入普通房、自主申请专属房、红娘邀请、系统自动邀请弹窗、用户个人主页、消息列表、消息列表好友头像、好友列表、离线推送
enum class LiveEnterType(var enterText: String, var applyText: String) {
    none("未知", ""),
    match_maker_start_live("红娘自主开房间", ""),//红娘自主开房间
    myself_apply_vip("自主申请专属房", ""),//自主申请专属房后红娘同意连线进入
    match_maker_invite("红娘邀请", ""),//红娘邀请
    system_auto_invite("系统自动邀请弹窗", ""),//系统自动邀请弹窗

    myself_home_normal_enter("首页进入普通房", "首页申请"),//首页普通房进入
    tab_me_normal_enter("我的Tab进入普通房", "我的申请"),//首页普通房进入
    user_home_page("用户个人主页", "用户个人主页申请"),//用户个人主页
    recommend_page("推荐列表页", "推荐列表申请"),//推荐列表页
    message_list("消息列表", "消息列表申请"),//消息列表
    message_list_friend_header("消息列表好友头像", "消息列表好友头像申请"),//消息列表好友头像
    friend_list("好友列表", "好友列表申请"),//好友列表
    follow_list("关注列表", "关注列表"),//关注列表
    im_private_page("im私聊页面", "im私聊页面申请"),//好友列表

    offline_push("离线推送", ""),//离线推送

    im_c2c_private_page("im私聊页面", "头部信息头像点击"),
    course_introduce_page("课程介绍", "询问"),
    special_room_page("专属房","跳转大公开房"),
    room_data_card("资料卡",""),

    quick_consult_page_wait("快速咨询等待页面", "") //快速咨询等待页面
}

fun parseLiveEnterType(type: String?): LiveEnterType {

    if (type.isNullOrEmpty()) {
        Log.e("LiveEnterType", "type is null or empty")
        return LiveEnterType.none
    }
    LiveEnterType.values().forEach {
        if (type == it.name) {
            return it
        }
    }
    return LiveEnterType.none
}