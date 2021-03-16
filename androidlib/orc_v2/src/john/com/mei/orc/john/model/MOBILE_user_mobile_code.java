package com.mei.orc.john.model;

import com.mei.orc.http.response.BaseResponse;

/**
 * Created by joker on 16/1/7.
 */
public class MOBILE_user_mobile_code {
    /**
     * easemob_app_salt :
     * session_id : 194816010796019e4a0855c7aba75fd0
     * login_user_id : 4102478
     * login_user_name : Lucky_Joker_刘
     */

    public String easemob_app_salt;

    public static class Response extends BaseResponse<MOBILE_user_mobile_code> {

    }

    public String session_id;
    public int login_user_id;
    public String login_user_name;


}
