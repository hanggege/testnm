package com.net.model.chick.room;

import com.mei.orc.http.response.BaseResponse;

import java.util.List;

/**
 * Created by hang on 2018/10/29.
 */
public class HandPickResult {

    public static class Response extends BaseResponse<HandPickResult> {
    }

    public List<Elite_cate_overview> elite_cate_overview;


    public static class Elite_cate_overview {
        public int cate_id;
        public String name;
        public String cate_image;
        public List<Elite_cate_item> list;
    }

    public static class Elite_cate_item {
        public String feed_id;
        public int cate_id;
        public String cate_name;
        public String create_user_name;
        public String sdate;
        public String title;
        public int is_video;
        public String image;
        public String create_time;
        public String keywords;
    }

}
