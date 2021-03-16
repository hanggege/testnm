package com.net.model.room;

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2019-12-13
 */
public enum RoomApplyState {

    NONE(0, "邀请连线"),
    APPLY(1, "同意连线"),
    WAITE_USER(2, "等待回复..."),//等待用户回复
    WAITE_USER_REPLY(3, "等待回复...");//待用户再次确认

    public int code;

    public String text;

    RoomApplyState(int code, String text) {
        this.code = code;
        this.text = text;
    }

    public static RoomApplyState parseValue(String state) {
        RoomApplyState result = RoomApplyState.NONE;
        for (int i = 0; i < RoomApplyState.values().length; i++) {
            if (state != null && state.equalsIgnoreCase(RoomApplyState.values()[i].name())) {
                result = RoomApplyState.values()[i];
            }
        }
        return result;
    }
}
