package com.net.model.room;

import com.joker.im.custom.chick.ServiceInfo;
import com.mei.orc.http.response.BaseResponse;

import java.util.List;

/**
 * Created by hang on 2020/6/3.
 */
public class SpecialServicePageList {

    public List<ServiceInfo> service;

    public static class Response extends BaseResponse<SpecialServicePageList> {
    }
}
