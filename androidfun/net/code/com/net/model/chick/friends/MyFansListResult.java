package com.net.model.chick.friends;

import com.mei.orc.http.response.BaseResponse;

import java.util.List;

/**
 * author : Song Jian
 * date   : 2020/2/21
 * desc   : 我的粉丝
 */
public class MyFansListResult {


    /**
     * list : {"hasMore":false,"list":[],"nextPageNo":2}
     */

    public ListBean list;

    public static class Response extends BaseResponse<MyFansListResult> {

    }


    public static class ListBean {
        /**
         * hasMore : false
         * list : []
         * nextPageNo : 2
         */

        public boolean hasMore;
        public int nextPageNo;
        public List<SubListBean> list;

    }


}
