package com.joker.flag;

import androidx.annotation.IntDef;

import com.joker.TdType;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @hide
 */
@IntDef(flag = true,
        value = {
                TdType.weixin,
                TdType.weibo,
                TdType.qq
        })
@Retention(RetentionPolicy.SOURCE)
public @interface TdFlags {
}