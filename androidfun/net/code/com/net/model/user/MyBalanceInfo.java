package com.net.model.user;

import com.mei.orc.http.response.BaseResponse;

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2019-12-13
 */

public class MyBalanceInfo {
    public static class Response extends BaseResponse<MyBalanceInfo> {

    }

    public double walletBalance;
    public double roseBalance;

}
