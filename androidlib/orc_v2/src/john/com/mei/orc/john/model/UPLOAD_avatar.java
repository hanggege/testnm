package com.mei.orc.john.model;


import com.mei.orc.http.response.BaseResponse;

/**
 * Created by steven on 15/4/27.
 */

public class UPLOAD_avatar {
    public static class Response extends BaseResponse<UPLOAD_avatar> {

    }

    public String uptoken = "";
    public String key = "";
    public String url = "";
}
