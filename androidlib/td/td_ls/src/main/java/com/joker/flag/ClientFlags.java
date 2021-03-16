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
                TdType.we_chat,
                TdType.friend_circle
        })
@Retention(RetentionPolicy.SOURCE)
public @interface ClientFlags {
}