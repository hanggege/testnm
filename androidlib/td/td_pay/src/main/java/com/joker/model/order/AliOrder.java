package com.joker.model.order;

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2017/5/2.
 */

public class AliOrder extends Order {

    public String pay_info;

    public AliOrder(String orderSN, String pay_info) {
        super(orderSN);
        this.pay_info = pay_info;
    }
}
