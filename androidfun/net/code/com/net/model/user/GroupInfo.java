package com.net.model.user;

import androidx.annotation.Nullable;

import java.io.Serializable;
import java.util.List;

/**
 * GroupInfo
 *
 * @author kun
 * @version 1.0.0
 * @since 2020-07-31
 */
public class GroupInfo implements Serializable {

    /**
     * avatar : https://img.meidongli.net/22/2020/07/29/aWNfbGF1bmNoZXIucG5n
     * gender : 1
     * groupId : 30542255
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

    public String avatar;
    public int gender;
    public int groupId;
    public boolean isTest;
    public MedalMapBean medalMap;
    public String nickname;
    public String roleId;
    public int roomInvisible;
    public int status;
    public int userId;
    public int userLevel;
    public List<?> medalList;
    @Nullable
    public String groupName; //工作室名字
    @Nullable
    public String image; //工作室封面
    @Nullable
    public String memberCount; //工作室成员
    public String roomId;
    public boolean publicRoom;//是否是工作室大直播间


    public static class MedalMapBean {
    }
}