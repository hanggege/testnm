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
                TdType.multi_share,
                TdType.image_share,
                TdType.mini_program_share  // 暂时只支持分享到对话，不支持分享到朋友圈
        })
@Retention(RetentionPolicy.SOURCE)
public @interface ShareFlags {
}