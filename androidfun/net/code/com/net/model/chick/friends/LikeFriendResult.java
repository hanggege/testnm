package com.net.model.chick.friends;

import com.joker.im.Conversation;
import com.mei.orc.http.response.BaseResponse;
import com.tencent.imsdk.TIMConversationType;
import com.tencent.imsdk.TIMManager;

import java.util.List;

/**
 * Created by zzw on 2019-12-11
 * Des:
 */
public class LikeFriendResult implements Comparable<LikeFriendResult> {


    /**
     * age : int 年龄
     * avatar : string 头像
     * gender : byte 性别 0-女 1-男 -1未知
     * location : string 所在地
     * nickname : string 昵称
     * online : Boolean 是否在线 true-在线
     * userId : int 用户id
     */

    public int age;
    public String avatar;
    public int gender;
    public String location;
    public String nickname;
    public Boolean online;
    public int userId;

    @Override
    public int compareTo(LikeFriendResult other) {
        long temp = other.getConversation().getLastMessageTime() - getConversation().getLastMessageTime();

        return (temp > 0) ? 1 : -1;
    }

    public Conversation getConversation() {
        return new Conversation(TIMManager.getInstance().getConversation(TIMConversationType.C2C, String.valueOf(userId)));
    }

    public static class Response extends BaseResponse<List<LikeFriendResult>> {

    }


}
