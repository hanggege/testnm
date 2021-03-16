package com.net.model.chick.evaluation;

import com.mei.orc.http.response.BaseResponse;

import java.util.List;

/**
 * Created by zzw on 2019-12-15
 * Des:
 */
public class TagBean {
    public List<TagBean> subTags;

    public static class Response extends BaseResponse<List<TagBean>> {

    }


    public TagBean(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int id;
    public String name;
}
