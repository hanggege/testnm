package com.net.model.chick.upload;


import com.mei.orc.http.response.BaseResponse;

/**
 * Created by steven on 15/4/27.
 */

public class UploadToken {
    public static class Response extends BaseResponse<UploadToken> {

    }

    public String upToken = "";
    public String key = "";
    public String url = "";
}
