package com.mei.orc.john.model;

import com.mei.orc.http.response.BaseResponse;

/**
 * Created by steven on 15/4/27.
 */

public class AccountLoginResult {

    public static class Response extends BaseResponse<AccountLoginResult> {

    }

    public int login_user_id;
    public String easemob_app_salt = "";
    public String login_user_name = "";
    public String session_id = "";

    public String token = "";
    //如果rtn=10402说明需要图片验证码，此时服务器的返回结构为：
    public String url = "";

    public AccountLoginResult() {
    }

    public AccountLoginResult(int login_user_id, String easemob_app_salt, String login_user_name, String session_id) {
        this.login_user_id = login_user_id;
        this.easemob_app_salt = easemob_app_salt;
        this.login_user_name = login_user_name;
        this.session_id = session_id;
    }

}
