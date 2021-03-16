package com.net.model.broadcast;


import android.text.TextUtils;

import java.util.List;
import java.util.Locale;

public class Room {
    public int begin_time_left_sec;
    public String begin_time;
    public String subject;
    public String permission_issue;
    public String last_broadcast_user_name;
    public int is_occupy;//当前用户是否超级管理员
    public String downstream_url;//收听输出流
    public String playbill_url;//节目单URL
    public String upstream;
    public String upstream_url;
    public int broadcast_user_id;
    public String share_url;//分享URL
    public int mentor_user_id;
    public String mentor_user_name;
    public String room_id;
    public int chat_enable;//是否被禁言，0是被禁言，
    public String mentor_avatar;
    public int last_broadcast_user_id;
    public int teach_enable;//是否是导师
    public int active_user_count;
    public String occupy_admin_user_name;
    public int compare_qiniu_system_time;
    public String broadcast_user_name;
    public String webview_url;//直播间、节目单 ab测老界面url地址
    public String webview_url2;//直播间、节目单 ab测新界面url地址
    public String webview_url3;//直播间ab测结案新地址
    public int broadcast_state;
    public String end_time;
    public int occupy_admin_user_id;
    public int broadcast_id;
    public int is_video;
    public int end_time_left_sec;
    public int is_require_pay;
    public int teach_type;
    public int followed;
    public String room_type;//直播类型，现分normal、tree_hole

    public int is_code;
    public String broadcast_code;

    public boolean has_weixin;
    public String weixin_url;
    public String pro_cate_name;

    public String channel_intro;
    public String chat_room_bg;

    public Broadcast_user_info broadcast_user_info;
    public int total_participation;


    public boolean isStartLiving() {//直播是否已开始
        return broadcast_state > 1;
    }

    public boolean isTeacher() {//是否是该课程导师
        return teach_enable == 1;
    }

    public boolean isNeedCode() {
        return is_code > 0;
    }

    public boolean isVideo() {
        return is_video == 1;
    }

    public boolean isrelease() {
        return broadcast_state == 4;
    }

    public boolean hasFollowdCurrentTeacher() {
        return followed == 1;
    }

    public enum PermissionIssueType {

        /**
         * 正常情况，可以连线
         */
        none("正常情况，可以连线"),

        /**
         * 普通用户或者没有登录态，没有连线按钮
         */
        normal_user("普通用户或者没有登录态，不能连线"),

        /**
         * 在七牛没有创建房间
         */
        no_room_in_qiniu("在七牛没有创建房间"),

        /**
         * 非团队成员&非超管
         */
        not_team_member("非团队成员,非超管"),

        /**
         * 不在可连线时间
         */
        timing("不在连线时间");

        public String toast = "";

        PermissionIssueType(String toast) {
            this.toast = toast;
        }

        public static PermissionIssueType parse(String name) {
            if (TextUtils.isEmpty(name)) {
                return none;
            }
            switch (name.toLowerCase(Locale.getDefault())) {
                case "normal_user":
                    return normal_user;
                case "no_room_in_qiniu":
                    return no_room_in_qiniu;
                case "not_team_member":
                    return not_team_member;
                case "timing":
                    return timing;
                default:
                    return none;
            }
        }
    }

    public static class Broadcast_user_info {
        public int user_id;
        public String intro;
        public List<String> image;
        public List<String> tag;
    }
}