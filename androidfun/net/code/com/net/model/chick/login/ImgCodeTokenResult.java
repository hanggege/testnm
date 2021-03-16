package com.net.model.chick.login;

import com.mei.orc.http.response.BaseResponse;

/**
 * Created by zzw on 2019-12-26
 * Des:
 */
public class ImgCodeTokenResult {

    public static class Response extends BaseResponse<ImgCodeTokenResult> {

    }

    /**
     * url : string,图片URL
     * token : MTM0MTA3Nzg0Nzk
     */

    public String url;
    public String token;

}
