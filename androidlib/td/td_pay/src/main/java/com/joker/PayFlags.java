package com.joker;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @hide
 */
@IntDef(flag = true,
        value = {
                PayType.weixin,
                PayType.alipay,
                PayType.paypal
        })
@Retention(RetentionPolicy.SOURCE)
public @interface PayFlags {
}