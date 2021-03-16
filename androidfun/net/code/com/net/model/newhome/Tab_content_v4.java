package com.net.model.newhome;

import com.mei.orc.http.response.BaseResponse;

import java.util.List;

/**
 * Created by hang on 2018/10/29.
 */
public class Tab_content_v4 {

    public static class Response extends BaseResponse<Tab_content_v4> {
    }

    public List<Pro_cate_item> pro_cate;
    public List<Tab_content_test.EliteCate> elite_cate;
    public List<Tab_content_test.BannerBean> banner2;
    public List<Tab_content_test.EntranceBean> entrance;
    public BroadcastItem broadcast;
    public Daily_elite daily_elite;
    public List<Elite_cate_overview> elite_cate_overview;
    public Intro intro;
    public Intro_list intro_list;
    public Platform_insurance platform_insurance;
    public EBookCollection ebook;
    public boolean review_version;


    public static class Pro_cate_item {
        public int pro_cate_id;
        public String pro_cate_name;
    }

    public static class BroadcastItem {
        public String tips;
        public List<Tab_content_test.BroadcastListBean> list;
    }

    public static class Daily_elite {
        public String cate_image;
        public List<Elite_cate_item> list;
    }

    public static class Elite_cate_overview {
        public int cate_id;
        public String name;
        public String cate_image;
        public List<Elite_cate_item> list;
    }

    public static class Intro {
        public String module_title;
        public String right_title;
        public String card_title;
        public String card_description;
        public String photo;
        public String url;
        public String link_url;
        public int position;
    }

    public static class Elite_cate_item {
        public String feed_id;
        public int cate_id;
        public String cate_name;
        public String create_user_name;
        public String create_user_id;
        public String sdate;
        public String title;
        public int is_video;
        public String image;
        public String create_time;
        public String keywords;
    }

    public static class Intro_list {
        public int position;
        public List<Intro> list;
    }

    public static class Platform_insurance {
        public String image;
        public String link_url;
    }

    /**
     * 秘籍
     */
    public static class EBookCollection {
        public String category_url;
        public String category_image;
        public List<EBookList> ebook;
        public PopUpData pop_up;
    }

    public static class PopUpData {
        public int pop_up;
        public String toast;
    }

    public static class EBookList {
        public EBook ebook;
        public User user;
    }

    public static class EBook {
        public int ebook_id;//17,
        public String name;//"测试测试测试测试-3",
        public String cover;
        public String intro;//"测试测试测试-1-1-1-1",
        public double ebook_price;//9.90,
        public int ebook_cate_id;//2,
        public int mentor_user_id;//3452342,
        public int total_section_amount;//4,
        public boolean free;//false,
        public String read_tag;//"LOCKED",
        public String applet_link;//"//goto_mini_program/{\"mini_app_name\":gh_6c35527f96a5,\"mini_path\":pages/cheats/cheats-details?ebook_id=17}"
    }

    public static class User {
        public int user_id;
        public String user_name;//文明用语",
        public String avatar;
    }
}
