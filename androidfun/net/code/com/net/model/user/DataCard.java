package com.net.model.user;


import com.mei.orc.http.response.BaseResponse;
import com.net.model.chick.friends.WorkListResult;
import com.net.model.room.CouponLabelInfo;
import com.net.model.room.RoomUserQueue;

import java.util.List;
import java.util.Map;

import androidx.annotation.Nullable;

/**
 * Created by steven on 15/4/25.
 */

public class DataCard {


    public int userId;
    public String avatar;
    public String nickname;
    public int gender;
    public String introduction;
    public List<String> publisherTags;//标签
    public String birthYear;
    public List<UserInfo.InterestsBean> interests;
    public WorkListResult.WorksBean works;//作品
    public int worksCount;//作品数量
    public List<UserInfo.InterestsBean> skills;
    public boolean isPublisher;//是否是主播
    public boolean isRoomOwner;//是否是该房间主播
    public boolean isSelf;//是否看自己
    public boolean isFollow;
    public int receivedCoinCount;
    public int fansCount;
    public String broadcastCount;
    public int sendCoinCount;//送礼数
    public int sendMeCount;//送我礼数
    public int followCount;
    public int upstreamCount;//用户连线次数
    public int userLevel;//用户等级
    public RoomUserQueue.InviteOption applyOptionDtoItem;
    public Map<String, Medal> medalMap;
    public boolean specialFlag;
    public int roleId;
    public int roomRole;//0:普通用户1: 知心达人/知心小姐姐://2 超管3房管
    private int status;
    @Nullable
    public MemberInfo memberInfo;
    public int groupId;
    public String roomId;
    public int onlineStatus;//0-不在线 1-直播 2-在线

    public List<CouponLabelInfo> coupons;

    /**
     * 拉黑次数 整数
     */
    public int blacklistCount;

    /**
     * 注册时间 XX天或者XX消失
     */
    public String regTime;

    /**
     * 与我连线次数 整数
     */
    public int linkedMeTimes;


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

    public static class Response extends BaseResponse<DataCard> {
    }

    public static class MemberInfo {
        public String avatar;
        public String nickname;
        public String birthYear;
        public int gender;
        public int groupRoleId;
        public int groupRights;
        public int userId;
        public String groupRoleName;
    }

}
