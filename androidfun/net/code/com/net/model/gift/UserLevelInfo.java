package com.net.model.gift;

import com.mei.orc.http.response.BaseResponse;

import androidx.annotation.Nullable;

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2020/4/17
 */
public class UserLevelInfo {
    public int level;
    public int needSend;
    public int nextLevel;
    public int levelUpNeed;
    @Nullable
    public String enterLevelPage; //等级介绍页面

    public static class Response extends BaseResponse<UserLevelInfo> {

    }

}
