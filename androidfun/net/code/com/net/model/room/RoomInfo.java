package com.net.model.room;

import androidx.annotation.Nullable;

import com.joker.im.custom.chick.CourseInfo;
import com.joker.im.custom.chick.ServiceInfo;
import com.joker.im.custom.chick.SplitText;
import com.mei.im.ui.view.menu.ChatMenu;
import com.mei.orc.john.model.JohnUser;
import com.net.model.gift.GiftInfo;
import com.net.model.user.GroupInfo;

import java.io.Serializable;
import java.util.List;

public class RoomInfo implements Serializable {

    @Nullable
    public GiftInfo gift;
    public String roomId = "";// 房间信息
    public String broadcastId = "";// 直播间信息
    private String roomType = "";// 房间类型
    public String channelId = "";//声网频道id
    @Nullable
    public GroupInfo groupInfo;
    @Nullable
    public CreateUser createUser;// 知心达人信息
    @Nullable
    public CreateUser guestUser;// 连线用户信息

    public boolean needUpstream;// 是否要连线
    public int publisherId;
    public String tag;
    public String title;
    public String token = "";
    @Nullable
    public ServiceInfo recommend;//主播第一个服务`
    @Nullable
    public CourseInfo recommendCourse;//主播第一个课程
    public int mode = 1;
    public int sendGuideMsgTime;// 发送引导消息需要的时间（秒）
    public int onlineCount;//在线人数
    public long receiveCount;
    public boolean applyUpstreamEnable;
    public int videoMode;// 视频模式 0: 无效值兼容旧版, 1: 仅音频, 2:视频
    @Nullable
    public ApplyOption applyOption;
    @Nullable
    public ServiceInfo specialServiceSampleDto;
    public boolean special;//是否是专属服务，仅限从im界面发起专属服务功能

    public int roomRole;//0:普通用户1: 知心达人/知心小姐姐://2 超管3房管

    public int groupRole;//工作室角色id 0:普通用户 1:房主 2:咨询师 3:分析师
    public int groupRights;//工作室权限 （1:开播 2:编辑资料 4:财务 8:督导）值做与操作判断是否有改权限

    public List<RoomWelcome> welcomeItems;
    public String defaultAudioBackground;//仅音频时背景
    public String roomStatusTips;//直播间空文案提示

    public List<SplitText> applyTips;
    public String applySuccessText;

    public boolean hasAvailableSpecialService;//是否购买了当前主播的服务
    public boolean hasSendMessage;//是否在当前直播间发过言
    public String applyPreCommunicationButtonText;//申请上麦按钮文案
    public String chatPlaceholderText;//输入框提示文案
    public String chatPlaceholderTextForNewBie;//未发送信息前输入框提示文案
    public String applyTipsText;//右上角申请上麦按钮文案
    // 默认美颜配置
    public String customBeautyConfigKey = "";

    public String alertTips;

    public boolean canSkipEvaluation = false;//是否可以跳过评价
    @Nullable
    public String weekRankText; //周排行榜

    public int scenario;//设置音频应用场景

    public int mirrorMode;//镜像mode

    public boolean openUpstreamControlOption = false;

    public boolean showApplyUpstreamPanel = false;

    public boolean showSpecialServiceAnimation;

    public int minWatchTimeOfShowFollowDialogSec;

    public List<ChatMenu> moreMenu;

    @Nullable
    public UpstreamCouponItem upstreamCouponItem; //当前房间是有用户在使用连线券连线

    public boolean isCreator() {
        return createUser != null && createUser.userId == JohnUser.getSharedUser().userID;
    }

    //是否是工作室大直播间
    public boolean isStudio() {
        return groupInfo != null && groupInfo.publicRoom;
    }

    //是否是工作室专属服务房间
    public boolean isSpecialStudio() {
        return groupInfo != null && !groupInfo.publicRoom;
    }

    public boolean isCommonRoom() {
        return groupInfo == null;
    }

    //当前房间是否是专属服务
    public boolean isSpecialService() {
        return special || specialServiceSampleDto != null;
    }

    public boolean isMatchRoomType(String type, int serviceId) {
        int specialServiceId;
        if (specialServiceSampleDto == null) {
            specialServiceId = 0;
        } else {
            specialServiceId = specialServiceSampleDto.getSpecialServiceId();
        }
        return type.equals(roomType) && serviceId == specialServiceId;
    }

    public RoomType getRoomType() {
        return RoomType.parseValue(roomType);
    }

    public void setRoomType(RoomType type) {
        roomType = type.name();
    }

    public static class CreateUser implements Serializable {
        public String avatar = "";
        public int gender;
        public boolean isFocus; //是否关注
        public String nickName = "";
        public int userId;
        public String personalImage;
        public String backgroundColor;
        public boolean isPublisher;
        public List<String> skills;
    }

    public static class ApplyOption implements Serializable {
        public int defaultSelect;
        @Nullable
        public List<Option> options;
    }

    public static class UpstreamCouponItem implements Serializable {
        /**
         * 券id
         */
        public Integer seqId;

        /**
         * 用户优惠券id
         */
        public Long couponNum;

        /**
         * 券名
         */
        public String couponName;

        /**
         * 说明
         */
        public String couponDesc;

        /**
         * 连线时长
         */
        public Integer linkLimit;

        /**
         * 咨询方向(品类)id
         */
        public Integer categoryId;

        /**
         * 上麦的品类
         */
        public String categoryName;
    }

    public static class Option implements Serializable {
        /**
         * 标题
         */
        public String title;

        /**
         * 消费提示
         */
        public String costTips;

        /**
         * 价格
         */
        public int price;

        /**
         * 房间类型
         */
        public String room_type;
    }

    public static class RoomWelcome {
        public int after;
        public List<SplitText> content;
    }


}
