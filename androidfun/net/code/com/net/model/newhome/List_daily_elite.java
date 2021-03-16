package com.net.model.newhome;

import com.mei.orc.http.response.BaseResponse;

import java.util.List;

/**
 * Created by hang on 2018/10/30.
 */
public class List_daily_elite {

    public static class Response extends BaseResponse<List_daily_elite> {
    }

    public String next_start_key;
    public boolean has_more;
    public List<Tab_content_v4.Elite_cate_item> list;
}
