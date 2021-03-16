package com.net.model.elite;

import android.text.TextUtils;

import java.util.List;

/**
 * Created by joker on 16/8/24. PHP接口返回数据
 */
public class Elite_New_Info {

    //页面展示需要的
    public int comment_num;
    public String feed_id;
    public int product_id;
    public String remark;
    public int fav_count;
    public int is_fav;
    public String video_url;
    public CreateUser create_user;
    //分享干货需要的
    public String create_time;
    public int feed_type;
    public String title;
    public int is_like;
    public String feed_image;
    public String summary;
    public String mini_program_path;
    //以下为暂时不用的
    public String css_url;
    public int css_version;
    public String js_url;
    public int js_version;
    public String body;
    public String play_js;
    public int elite_feed_id;
    public String voice_url;
    public int voice_id;
    public int view_count;
    public int like_count;
    public int is_pre_audit;
    public boolean is_self_video;
    public int total_time;
    public int is_anonymous;
    public String feed_url;
    public String feed_video;
    public List<String> feed_image_more;
    public ExtraInfo extra_info;

    public static class ExtraInfo {
        public boolean is_ab_test;
        public String ab_version;
    }

    public boolean _has_video() {
        return !TextUtils.isEmpty(video_url);
    }
}
