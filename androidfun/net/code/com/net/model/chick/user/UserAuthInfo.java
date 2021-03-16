package com.net.model.chick.user;

import com.mei.orc.http.response.BaseResponse;

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2019-12-25
 */

public class UserAuthInfo {

    public static class Response extends BaseResponse<UserAuthInfo> {

    }

    public int userId;
    public String roleId = "";
    public int status;// 0正常，1禁言发言，2，禁止登录
}
