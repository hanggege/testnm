package com.net.model.room;

import android.text.TextUtils;

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2019-12-12
 */
public enum RoomType {
    COMMON(0, "普通相亲房"),
    EXCLUSIVE(1, "专属相亲房"),
    COMMON_BANNER(2, "首页banner"),
    SPECIAL(4, "专属服务");
    public int code;
    public String text;

    RoomType(int code, String text) {
        this.code = code;
        this.text = text;
    }

    public static RoomType parseValue(String type) {

        if (TextUtils.isEmpty(type)) {
            return RoomType.COMMON;
        }

        for (RoomType value : values()) {
            if (value.name().toUpperCase().equals(type.toUpperCase())) {
                return value;
            }
        }
        return RoomType.COMMON;
    }
}
