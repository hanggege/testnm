package com.joker.im.custom.chick;

import androidx.annotation.Nullable;

/**
 * @author Created by lenna on 2020/10/13
 * 荣誉勋章
 */
public class HonorMedal {
    @Nullable
    public String innerImg;
    @Nullable
    public String shellImg;
    @Nullable
    public String title;
    @Nullable
    public String intro;
    @Nullable
    public String medalBtnStr;
    @Nullable
    public String action;
    @Nullable
    public String type;

    public enum HonorMedalType {
        USER_BROADCAST_SPEECH, //敞开心扉
        USER_LOGIN_DAYS,//伴我同行
        USER_SPECIAL_SERVICE_AMOUNT, //专属服务
        USER_UPSTREAM_SAME_PUBLISHER_DURATION, //耳濡目染
        USER_UPSTREAM_PUBLISHERS, //兼听则明

        //达人
        PUBLISHER_BROADCAST_DURATION,//兢兢业业
        PUBLISHER_FANS, //万众瞩目
        PUBLISHER_UPSTREAM_TIMES,//有求必应
        PUBLISHER_UPSTREAM_DURATION, //言传身教
        PUBLISHER_UPSTREAM_PEOPLE, // 良师益友
        TROPHY,//荣誉奖杯
    }
}

