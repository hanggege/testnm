package com.joker.model.order;

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2017/5/3.
 */

public class PalOrder extends Order {
    public String total_fee;
    public String currency;
    public String item_name;
    public String paypal_clientID;

    public PalOrder(String orderSn, String total_fee, String currency, String item_name, String paypal_clientID) {
        super(orderSn);
        this.total_fee = total_fee;
        this.currency = currency;
        this.item_name = item_name;
        this.paypal_clientID = paypal_clientID;
    }
}
