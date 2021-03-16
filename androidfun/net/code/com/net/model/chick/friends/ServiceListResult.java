package com.net.model.chick.friends;

import com.mei.orc.http.response.BaseResponse;
import com.net.model.room.SpecialServiceBean;

/**
 * Created by Song Jian
 * Des: 他人主页
 */
public class ServiceListResult {

    public static class Response extends BaseResponse<ServiceListResult> {

    }

    public SpecialServiceBean list;


}

