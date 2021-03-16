package com.net.model.room;

import android.text.TextUtils;

/**
 * Created by hang on 2020/8/17.
 */
public enum RoomUserTypeEnum {
    ROOM_APPLY_USER("roomApplyUser", "申请连线"),
    ROOM_USER("roomUser", "房间内用户"),
    ROOM_OLD_USER("roomOldUser", "连线用户"),
    ROOM_SPECIAL_USER("roomSpecialUser", "专属用户");

    public String type;
    public String text;

    RoomUserTypeEnum(String type, String text) {
        this.type = type;
        this.text = text;
    }

    public static RoomUserTypeEnum parseValue(String type) {

        if (TextUtils.isEmpty(type)) {
            return RoomUserTypeEnum.ROOM_USER;
        }

        for (RoomUserTypeEnum value : values()) {
            if (value.type.toUpperCase().equals(type.toUpperCase())) {
                return value;
            }
        }
        return RoomUserTypeEnum.ROOM_USER;
    }
}
