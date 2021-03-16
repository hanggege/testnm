package com.mei.orc.john.model;

import com.mei.orc.http.response.BaseResponse;

/**
 * @author caowei
 * @email 646030315@qq.com
 * Created on 17/6/27.
 */

public class CheckAccountResult {

    public static final class Response extends BaseResponse<CheckAccountResult> {

    }

    public String token;
    public String url;
}
