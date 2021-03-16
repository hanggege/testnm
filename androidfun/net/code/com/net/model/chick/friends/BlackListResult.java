package com.net.model.chick.friends;

import com.mei.orc.http.response.BaseResponse;

import java.util.List;

/**
 * Created by zzw on 2019-12-11
 * Des:
 */
public class BlackListResult {

    /**
     * age : int 年龄
     * avatar : string 头像
     * gender : byte 性别 0-女 1-男 -1未知
     * nickname : string 昵称
     * blackTime : Date拉黑时间
     * userId : int 用户id
     */

    public String age;
    public String avatar;
    public int gender;
    public String nickname;
    public String blackTime;
    public int userId;

    public static class Response extends BaseResponse<List<BlackListResult>> {

    }


}
