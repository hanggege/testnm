package com.mei.pay.webpay.data;


import com.google.gson.annotations.SerializedName;
import com.joker.PayFlags;
import com.joker.PayType;
import com.joker.model.order.AliOrder;
import com.joker.model.order.Order;
import com.joker.model.order.WxOrder;
import com.mei.orc.http.response.BaseResponse;
import com.mei.orc.net.URLEncoderKt;

/**
 * Created by hang on 2019/1/14.
 */

public class Order_Create_V2 {
    public static class Response extends BaseResponse<Order_Create_V2> {

    }

    /**
     * order_string : order_sn : 1506120906125119152 pay_param : {"sign":"1CB9EB94BEA59882B82DCA62EEBD7CBF","timestamp":"1434071172"}
     */

    public String order_sn;
    public Pay_paramEntity pay_param;

    @Override
    public String toString() {
        return "ORDER_create{" +
                " order_sn='" + order_sn + '\'' +
                ", pay_param=" + pay_param +
                '}';
    }

    public Order getOrder(@PayFlags int payType) {
        Order order = null;
        switch (payType) {
            case PayType.weixin:
                if (pay_param != null) {
                    order = new WxOrder(order_sn, pay_param.appid,
                            pay_param.partnerid, pay_param.prepayid, pay_param.packageName,
                            pay_param.noncestr, pay_param.timestamp, pay_param.sign);
                }
                break;
            case PayType.alipay:
                order = new AliOrder(order_sn, pay_param.getOrder_string());
                break;
            case PayType.paypal:
            default:
                break;
        }
        return order;
    }

    public static class Pay_paramEntity {
        /**
         * 通过web获取order_string，会编码一次，本地会进行解码，但是传入支付宝的sign值必须要进行编码
         */
        private String getOrder_string() {
            int startIndex = order_string.indexOf("sign=\"") + 6;
            int endIndex = order_string.indexOf("\"", startIndex);
            String old_sign = "";
            if (startIndex < endIndex && endIndex < order_string.length()) {
                old_sign = order_string.substring(startIndex, endIndex);
            }
            String encode_sign = URLEncoderKt.encode(old_sign);
            try {
                return order_string.replace(old_sign, encode_sign);
            } catch (Exception e) {
                return order_string;
            }
        }

        public String order_string;
        public String subject;
        public String _input_charset;
        public String sign;
        public String it_b_pay;
        public String body;
        public String notify_url;
        public String noncestr;
        public String payment_type;
        public String out_trade_no;
        public String partner;
        public String service;
        public String appid;
        public String total_fee;
        public String partnerid;
        public String prepayid;
        public String seller_id;
        public String show_url;
        public String timestamp;

        @SerializedName("package")
        public String packageName;

        ///////paypal//////////
        public String currency;
        public String transaction_desc;
        public String item_name;
        public String item_intro;
        public String item_price;
        public String item_quantity;

    }
}
