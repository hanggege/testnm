package com.net.model.rose;

public enum RoseFromSceneEnum {
    COMMON_ROOM(0, "公开相亲"),
    EXCLUSIVE_ROOM(1, "专属相亲"),
    PERSONAL_PAGE(3, "个人主页"),
    EXCLUSIVE_CHAT(4, "IM送礼");

    private Integer code;

    private String text;

    RoseFromSceneEnum(Integer code, String text) {
        this.code = code;
        this.text = text;
    }

    public Integer getCode() {
        return code;
    }

    public String getText() {
        return text;
    }
}
