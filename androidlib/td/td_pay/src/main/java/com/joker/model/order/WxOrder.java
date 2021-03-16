package com.joker.model.order;

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2017/5/2.
 */

public class WxOrder extends Order {
    public String wx_app_id;//微信需要支付ID
    public String partnerId;
    public String prepayId;
    public String packageValue;
    public String nonceStr;
    public String timeStamp;
    public String sign;

    public WxOrder(String orderSn, String wx_app_id, String partnerId, String prepayId, String packageValue, String nonceStr, String timeStamp, String sign) {
        super(orderSn);
        this.wx_app_id = wx_app_id;
        this.partnerId = partnerId;
        this.prepayId = prepayId;
        this.packageValue = packageValue;
        this.nonceStr = nonceStr;
        this.timeStamp = timeStamp;
        this.sign = sign;
    }
}
