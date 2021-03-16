package com.net.model.room;

import android.text.TextUtils;

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2019-12-13
 */
public enum RoomApplyType {

    UPSTREAM(0, "申请连线"),
    INVITE_UPSTREAM(1, "邀请连线"),
    TRANSFORM(2, "房间转换"),
    INVITE_FREE_UPSTREAM(2, "邀请免费连线");

    public int code;

    public String text;

    RoomApplyType(int code, String text) {
        this.code = code;
        this.text = text;
    }

    public static RoomApplyType parseValue(String type) {

        if (TextUtils.isEmpty(type)) {
            return RoomApplyType.INVITE_UPSTREAM;
        }

        for (RoomApplyType value : values()) {
            if (value.name().toUpperCase().equals(type.toUpperCase())) {
                return value;
            }
        }

        return RoomApplyType.INVITE_UPSTREAM;
    }
}
