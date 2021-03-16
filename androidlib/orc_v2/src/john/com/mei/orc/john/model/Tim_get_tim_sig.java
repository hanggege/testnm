package com.mei.orc.john.model;

import com.mei.orc.http.response.BaseResponse;

/**
 * 佛祖保佑         永无BUG
 * <p>
 * Created by joker on 2017/2/21.
 */

public class Tim_get_tim_sig {
    public static class Response extends BaseResponse<Tim_get_tim_sig> {

    }

    public UserSig user_sig;

    public static class UserSig {
        public String sig;
        public String update;
    }
}
