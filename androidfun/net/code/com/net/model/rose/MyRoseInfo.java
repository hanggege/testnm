package com.net.model.rose;

import com.mei.orc.http.response.BaseResponse;

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2019-12-13
 */

public class MyRoseInfo {
    public static class Response extends BaseResponse<MyRoseInfo> {

    }

    public WalletBean coin;

    public static class WalletBean {

        public double costMoney;
        public int hasDeposit;
        public double coinBalance;
        public int userId;

    }
}
