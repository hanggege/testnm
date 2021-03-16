package com.net.model.ali;

import com.mei.orc.http.response.BaseResponse;

/**
 * Created by hang on 2019-11-29.
 */
public class AliRpToken {

    public static class Response extends BaseResponse<AliRpToken> {
    }

    public TokenResponse tokenResponse;
    public String requestId;

    public class TokenResponse {
        public String verifyToken;
    }
}
