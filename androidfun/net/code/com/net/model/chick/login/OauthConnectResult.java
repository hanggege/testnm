package com.net.model.chick.login;

import com.mei.orc.http.response.BaseResponse;
import com.mei.orc.john.model.Oauth_Connect;

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2017/7/13.
 */

public class OauthConnectResult extends Oauth_Connect {
    public static class Response extends BaseResponse<OauthConnectResult> {

    }

    public boolean ifPerfect;
    public int isReg;

}
