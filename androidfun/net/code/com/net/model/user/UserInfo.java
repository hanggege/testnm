package com.net.model.user;


import com.net.model.room.RoomInfo;

import java.util.List;
import java.util.Map;

import androidx.annotation.Nullable;

/**
 * Created by steven on 15/4/25.
 */

public class UserInfo extends UserInfoBase {


    /**
     * adminUser : false
     * birthYear : 90后
     * commonUser : true
     * interestIds : 3
     * interests : [{"id":3,"name":"星座占卜"}]
     * isPublisher : false
     * isTest : false
     * isTrainer : false
     * male : true
     * publisher : false
     * roleId : 0
     * testUser : false
     */

    public boolean adminUser;
    public String birthYear;
    public boolean commonUser;
    public String interestIds;
    public String phone;
    public int roomRole;//0:普通用户1: 知心达人/知心小姐姐://2 超管3房管
    public boolean isTrainer;
    public List<InterestsBean> interests;
    public List<UserInfo.InterestsBean> skills;
    public List<PublisherSkillsBean> publisherSkills;
    public List<String> tags;
    public String introduction = "";
    public Map<String, Medal> medalMap;
    public boolean specialFlag;
    public boolean upFlag;
    /**
     * 拉黑状态
     */
    private int status;

    /**
     * 对方被(自己)拉黑(或者互相拉黑同用一个状态)
     */
    public boolean isTheOtherToBlackList() {
        return status == 6;
    }

    /**
     * 自己被对方拉黑
     */
    public boolean isSelfToBlackList() {
        return status == 7;
    }


    /**
     * 是否有黑名单关系（true 存在黑名单关系 false 正常）
     */
    public boolean isBlackList() {
        return isTheOtherToBlackList() || isSelfToBlackList();
    }

    /**
     * AB版本
     */
    public String abVer;
    @Nullable
    public GroupInfo groupInfo;
    /**
     * 形象照
     */
    @Nullable
    public String personalImage;

    /**
     * 形象照底色
     */
    public String backgroundColor;

    public static class InterestsBean {
        /**
         * id : 3
         * name : 星座占卜
         */

        public int id;
        public String name;


    }

    public static class PublisherSkillsBean {
        /**
         * id : 3
         * name : 星座占卜
         */

        public int id;
        public String name;


    }

    /**
     * 超管
     */
    public boolean isManager() {
        return false;
    }


    //################以下是竹马旧数据 待删########################

    public int age;
    public int height;
    //手机认证状态 0未认证 1已认证
    public int phoneAuthStatus;


    //地区
    public String location;
    //真实姓名
    public String realName;
    //number,好友关系 0代表 不是好友 1代表处于待确认中 2代表是好友中
    //双方面的好友关系
    public int friendRelation;

    //true 表示发送申请  false 表示接收申请   这个字段必须是friendRelation==1时有效
    public boolean isFriendApply = true;

    //是否是黑名单 0不是黑名单  1把对方拉为黑名单  2对方把我拉为黑名单  3双方都是黑名单
    public int blackState = 0;
    //在线状态
    public boolean online = false;

    // 是否隐身
    public int roomInvisible = 0;


    //房间信息
    public RoomInfo roomInfo;


    public boolean isBlack() {
        return blackState == 1 || blackState == 3;
    }


}
