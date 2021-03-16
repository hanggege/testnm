package com.net.model.room;

import com.mei.orc.http.response.BaseResponse;

import java.util.List;

import androidx.annotation.Nullable;

/**
 * Created by hang on 2020-03-08.
 */
public class ProductCategory {

    /**
     * cover_color : #1111
     * cover_url : https://img.meidongli.net/2020/02/25/pro_cate/marriage.png
     * in_use : 1
     * is_delete : 0
     * is_test_data : 0
     * priority : 90
     * pro_cate_id : 2
     * pro_cate_name : 婚姻情感
     */

    public List<ProductCategoryBean> list;
    @Nullable
    public String img;


    public static class Response extends BaseResponse<ProductCategory> {
    }


    public static class ProductCategoryBean {
        public String cover_color;
        public String cover_url;
        public String cover_url_unselected;
        public int pro_cate_id;
        public String pro_cate_name;

        public int id;
        public String name;
        public String icon;
    }

}
