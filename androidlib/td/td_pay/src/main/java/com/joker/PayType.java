package com.joker;

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2017/5/2.
 */

public interface PayType {

    int weixin = 1 << 10;
    int alipay = 1 << 11;
    int paypal = 1 << 12;
}
