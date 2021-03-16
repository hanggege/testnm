package com.mei.orc.john.model;

import com.mei.orc.http.response.BaseResponse;

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2017/7/13.
 */

public class Oauth_Connect extends AccountLoginResult {
    public static class Response extends BaseResponse<Oauth_Connect> {

    }

//    如果rtn=566说明传参有问题，请直接将返回的msg提示给用户


    //如果rtn=20201说明用户是第一次登录（服务器没有对应的user_id记录） → 【当前流程结束】此时的返回结构为
    public String access_token;
    public String openid;
    public long seq_id;  // 临时身份标识ID，和access_token捆绑，注册时需用到
    public String user_name;// 第三方的用户昵称，注册时需用到
    public String avatar;

}
