package com.net.model.user;


/**
 * Created by steven on 15/4/25.
 */

public class UserInfoBase {
    public int userId;
    public String nickname;
    public String avatar;
    // 0女1男 -1未填写
    public int gender;
    public String birthday;
    public String wechat;
    public String createTime;
    public int userLevel;
    public String clientType;//Android,iOS,MP
    public int groupRole;
    public int groupRights;
    public int groupId;
    public boolean isPublisher;

    public boolean isStudioPublisher() {
        return isPublisher && groupId > 0;
    }

    public boolean isCommonPublisher() {
        return isPublisher && groupId == 0;
    }
}
