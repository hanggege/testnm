package com.net.model.newhome;

import com.mei.orc.http.response.BaseResponse;

import java.util.List;

/**
 * Created by hang on 2018/10/30.
 */
public class List_cate {

    public static class Response extends BaseResponse<List_cate> {
    }

    public BroadcastItem broadcast;
    public Elite_item elite;

    public static class BroadcastItem {
        public String tips;
        public List<Tab_content_test.BroadcastListBean> list;
        public List<PrivateItem> private_list;
    }

    public static class PrivateItem {
        public int broadcast_id;
        public String subject;
        public String url;
    }

    public static class Elite_item {
        public String next_start_key;
        public boolean has_more;
        public List<Tab_content_v4.Elite_cate_item> list;
    }
}
