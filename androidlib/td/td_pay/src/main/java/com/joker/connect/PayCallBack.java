package com.joker.connect;


import com.joker.model.PayFailure;
import com.joker.model.PaySuccess;

/**
 * Created by joker on 16/8/10.
 */
public interface PayCallBack {
    void onSuccess(PaySuccess success);

    void onFailure(PayFailure failure);
}
