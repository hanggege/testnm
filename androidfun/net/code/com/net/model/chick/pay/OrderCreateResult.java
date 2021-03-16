package com.net.model.chick.pay;


import com.google.gson.annotations.SerializedName;
import com.joker.PayFlags;
import com.joker.PayType;
import com.joker.model.order.AliOrder;
import com.joker.model.order.Order;
import com.joker.model.order.WxOrder;
import com.mei.orc.http.response.BaseResponse;

/**
 * Created by Joker on 2015/6/12.
 */

public class OrderCreateResult {
    public static class Response extends BaseResponse<OrderCreateResult> {

    }

    public String orderSn;
    public String payType;

    public PayParamEntity payParam;


    public static class PayParamEntity {
        //微信----
        public String appid;
        public String noncestr;
        public String partnerid;
        public String prepayid;
        public String sign;
        public String timestamp;
        @SerializedName("package")
        public String packageName;
        //微信----

        //支付宝----
        public String order_string;
        //支付宝----


    }

    public Order getOrder(@PayFlags int payType) {
        Order order = null;
        switch (payType) {
            case PayType.weixin:
                if (payParam != null) {
                    order = new WxOrder(orderSn, payParam.appid,
                            payParam.partnerid, payParam.prepayid, payParam.packageName,
                            payParam.noncestr, payParam.timestamp, payParam.sign);
                }
                break;
            case PayType.alipay:
                order = new AliOrder(orderSn, payParam.order_string);
                break;
        }
        return order;
    }
}
