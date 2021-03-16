package com.net.model.newhome;

import com.mei.orc.http.response.BaseResponse;

import java.util.List;

/**
 * Created by hang on 2018/10/31.
 */
public class Broadcast_v2_home {

    public static class Response extends BaseResponse<Broadcast_v2_home> {
    }

    public List<Tab_content_test.BroadcastListBean> public_list;
    public List<Tab_content_test.BroadcastListBean> broadcast_list;
    public List<Tab_content_test.EliteCate> elite_cate;
    public String h5_private_list;
    public String h5_public_list;
}
