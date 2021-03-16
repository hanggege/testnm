package com.net.model.config;

import com.mei.orc.http.response.BaseResponse;


/**
 * Created by steven on 15/4/25.
 */

public class UPLOAD_uptoken {
    public static class Response extends BaseResponse<UPLOAD_uptoken> {

    }

    public String dir;
    public String upToken;
    public String url;
    public String localFilePath;


}
