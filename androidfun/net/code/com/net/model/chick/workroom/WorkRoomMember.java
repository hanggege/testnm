package com.net.model.chick.workroom;

import java.util.List;

import androidx.annotation.Nullable;

/**
 * @author Created by lenna on 2020/7/30
 */
public class WorkRoomMember {


    /**
     * avatar : https://img.meidongli.net/22/2020/07/29/aWNfbGF1bmNoZXIucG5n
     * gender : 1
     * groupId : 30542255
     * introduction : 坎坎坷坷叫姐姐看空间咔咔咔急急急嘎达是是是对对对
     * isTest : true
     * medalList : []
     * medalMap : {}
     * nickname : joker测试
     * roleId : 1
     * roomInvisible : 0
     * status : 0
     * userId : 30542255
     * userLevel : 0
     */

    @Nullable
    public String avatar;
    public int gender;
    public int groupId;
    @Nullable
    public String introduction;
    public boolean isTest;
    @Nullable
    public MedalMapBean medalMap;
    @Nullable
    public String nickname;
    @Nullable
    public String roleId;
    public int roomInvisible;
    public int status;
    public int userId;
    public int userLevel;
    @Nullable
    public List<?> medalList;

    public boolean isChecked;

    @Nullable
    public String getAvatar() {
        return avatar;
    }


    public static class MedalMapBean {
    }
}
