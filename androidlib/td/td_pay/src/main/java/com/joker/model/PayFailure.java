package com.joker.model;


import com.joker.PayFlags;

/**
 * Created by joker on 16/8/10.
 */
public class PayFailure {
    @PayFlags
    public int type;
    public String orderSn;
    public String err_code;
    public String err_reason;
    public String err_msg;

    public PayFailure(@PayFlags int type, String orderSn, String err_code, String err_reason, String err_msg) {
        this.type = type;
        this.orderSn = orderSn;
        this.err_code = err_code;
        this.err_reason = err_reason;
        this.err_msg = err_msg;
    }
}
