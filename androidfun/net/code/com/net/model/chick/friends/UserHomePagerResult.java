package com.net.model.chick.friends;

import com.mei.orc.http.response.BaseResponse;
import com.net.model.chick.recommend.BatRoomStatusResult;
import com.net.model.chick.workroom.WorkRoomMember;
import com.net.model.room.CourseListInfo;
import com.net.model.room.SpecialServiceBean;
import com.net.model.user.GroupInfo;
import com.net.model.user.UserInfo;

import java.util.List;

import androidx.annotation.Nullable;

/**
 * Created by Song Jian
 * Des: 他人主页
 */
public class UserHomePagerResult {

    public static class Response extends BaseResponse<UserHomePagerResult> {

    }


    @Nullable
    public InfoBean info; // 工作室简介
    public int fansCount;
    public boolean isGroup; // 是否是工作室成员
    public boolean canEditGroupInfo;
    @Nullable
    public WorkListResult.WorksBean works;
    public List<ReceiveGiftBean> receiveGift;
    @Nullable
    public SpecialServiceBean specialServices; //专属服务
    @Nullable
    public CourseListInfo courses; //详细课程
    public int receivedCoinCount;//收到心币数
    public int receiveGiftCount;//收到礼物数
    public String broadcastCount;//开直播数
    public int worksCount;
    public int courseCount;
    public int userGroupRights;//权限，大于0代表自己是当前工作室的角色
    public boolean hasLived; //是否直播过
    public List<ConsultInfo> consultList; //咨询师列表
    public boolean rpVerify; //是否经过身份验证
    public List<ConsultInfo> promoterList; //分析师列表
    public Promoter bindPromoter; // 捆绑的销售
    public int topWorksLimit; //个人置顶作品限制
    public int groupTopWorksLimit; //工作室置顶作品限制
    @Nullable
    public String hasBlacklistTips; //点击直播间入口提示

    @Nullable
    public BatRoomStatusResult.StatusResult roomStatus; // 正在直播的状态,为空就没有直播

    /**
     * 是否是当前工作室成员
     */
    public boolean isGroupMember() {
        return userGroupRights != 0;
    }

    public static class InfoBean {
        public UserInfo userInfo = new UserInfo();
        @Nullable
        public GroupInfo groupInfo = new GroupInfo();
        public boolean isFollow;
        public int groupId;
        public String roomId;
        public int userId;
        public String homepageCover;
        public String introduction;
        //        0离线 1开播 2-在线
        public int onlineStatus;
        public List<String> publisherTags;
        public List<SkillsBean> skills;

        @Nullable
        public String personalImage;

        public static class SkillsBean {

            public int id;
            public String name;


        }
    }

    public static class ReceiveGiftBean {

        public String giftId;
        public String giftName;
        public String giftImage;
        public String num;

    }

    public static class ConsultInfo {
        public String avatar;
        public int gender;
        public int groupId;
        public int groupRights;
        public int groupRoleId;
        public String backgroundColor;
        public boolean isTest;
        public MedalMapBean medalMap;
        public String nickname;
        public String roleId;
        public int roomInvisible;
        public int status;
        public int userId;
        public int userLevel;
        public String personalImage;
        public List<?> medalList;
        public int bindPromoterId;
        public boolean isGroupMember;
        public String personalColorImage;

        public static class MedalMapBean {
        }
    }

    public static class WorkInfo {
        public boolean hasMore;
        public List<?> list;
        public int nextPageNo;
    }

    public static class Promoter {
        public String avatar;
        public String birthYear;
        public String clientType;
        public int gender;
        public int groupId;
        public int groupRights;
        public int groupRoleId;
        public String groupRoleName;
        public boolean isTest;
        public WorkRoomMember.MedalMapBean medalMap;
        public String nickname;
        public String personalImage;
        public String roleId;
        public int roomInvisible;
        public int status;
        public int userId;
        public int userLevel;
        public List<?> medalList;
    }


}

