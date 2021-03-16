package com.net.model.chick.report;


import com.mei.orc.http.response.BaseResponse;

import java.util.List;

/**
 * Created by zzw on 2019-12-14
 * Des:
 */
public class ReasonBean {
    public static class Response extends BaseResponse<List<ReasonBean>> {

    }

    public int id;
    public String name;

    public ReasonBean(int id, String name) {
        this.id = id;
        this.name = name;
    }
}
