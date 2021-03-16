package com.joker.model;


import com.joker.PayFlags;

/**
 * Created by joker on 16/8/10.
 */
public class PaySuccess {
    @PayFlags
    public int type;
    public String orderSn;
    //paypal
    public String paymentId;
    public String state;
    public String payIntent;

    public PaySuccess() {
    }

    public PaySuccess(@PayFlags int type, String orderSn) {
        this.type = type;
        this.orderSn = orderSn;
        this.paymentId = "";
        this.state = "";
        this.payIntent = "";
    }

    public PaySuccess(@PayFlags int type, String orderSn, String paymentId, String state, String payIntent) {
        this.type = type;
        this.orderSn = orderSn;
        this.paymentId = paymentId;
        this.state = state;
        this.payIntent = payIntent;
    }
}
