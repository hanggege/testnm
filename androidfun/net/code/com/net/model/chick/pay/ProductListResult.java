package com.net.model.chick.pay;

import com.mei.orc.http.response.BaseResponse;

import java.util.List;

/**
 * Created by zzw on 2019-12-13
 * Des:
 */
public class ProductListResult {


    public static class Response extends BaseResponse<ProductListResult> {

    }

    /**
     * userRose : {"costMoney":0,"hasDeposit":0,"roseBalance":0,"userId":22313310}
     * products : [{"cost":1,"depositRose":10,"firstDeposit":1,"phoneSystem":"ANDROID","priority":100,"productId":1},{"cost":10,"depositRose":100,"firstDeposit":0,"phoneSystem":"ANDROID","priority":90,"productId":2}]
     */

    public UserCoinBean userCoin;
    public List<ProductBean> firstProducts;
    public List<ProductBean> products;
    public List<NewbieTaskBean> newbieTask;
    public String coinUseText;

    public static class UserCoinBean {
        /**
         * costMoney : 0.0
         * hasDeposit : 0
         * roseBalance : 0.0
         * userId : 22313310
         */

        public double costMoney;
        public int hasDeposit;
        public int coinBalance;
        public int userId;
    }

    public static class NewbieTaskBean {
        /**
         * coinText : 5
         * isFinish : false
         * taskText : 首次观看直播10分钟
         * taskType : FIRST_WATCH_LIVE
         */

        public String coinText;
        public boolean isFinish;
        public String taskText;
        public String taskType;

    }


}
