package com.net.model.user;

import com.joker.im.custom.chick.ServiceInfo;
import com.joker.im.custom.chick.SplitText;
import com.mei.orc.http.response.BaseResponse;
import com.net.model.chick.friends.MyFollowListBean;

import java.util.List;

import androidx.annotation.Nullable;

/**
 * author : Song Jian
 * date   : 2020/2/25
 * desc   : 我的tab页
 */
public class MyPageInfo {


    @Nullable
    public Info info;

    public Finance finance;

    public boolean isReviewingVersion;

    public List<MeTabItem> list;
    public List<MeTabItem> listA; // 编辑工作室页面
    public List<MeTabItem> listB; // 我的关注，我赞过的
    public List<MeTabItem> listC; // 推荐【知心】给朋友，我要入驻
    public List<MeTabItem> listTools; //电台，助眠工具
    public List<MeTabItem> listD; // 设置
    @Nullable
    public List<MeTabItem> listUser; //我的余额，我的课程，优惠券

    public List<MyFollowListBean> myFollowList;

    @Nullable
    public List<ServiceInfo> mySpecialServices;

    public MeTabItem startLiveButton;

    public String mySpecialServiceAction;

    public String specialServiceAction;

    @Nullable
    public NoServiceLabel noServiceLabel;

    public static class Response extends BaseResponse<MyPageInfo> {

    }

    public static class Info extends UserInfoBase {
        public boolean adminUser;
        public boolean commonUser;
        public String interestIds;
        public boolean testUser;
        //    public List<?> interests;
        public boolean isTrainer;
        public String title;
        public GroupInfo groupInfo;
        public String groupRoleName;
        public boolean canEditGroupPage;
        @Nullable
        public String enterLevelPage; //等级内链
        /**
         * 形象照
         */
        @Nullable
        public String personalImage;

        /**
         * 形象照底色
         */
        public String backgroundColor;
    }

    public static class NoServiceLabel {
        public String toAllServiceAction;
        public String toWebServiceListAction;
        public String noServiceLabel;
        public boolean showButton = true;
    }

    public static class Finance {
        public Coin coin;
        public Wallet wallet;
    }

    public static class Coin {
        public int coinBalance;
        public double costMoney;
        public int hasDeposit;
    }

    public static class Wallet {
        public double incomeCount;
        public double moneyBalance;
    }

    public enum MeTabItemType {
        HONOR_MEDAL //荣誉勋章
    }

    /**
     * 为了解决「设置」的阴影切边问题
     */
    public enum MeTabItemPosition {
        AREA_FIRST,     // 一个区域的第一个item
        AREA_MID,       // 一个区域的中间item
        AREA_LAST,    // 一个区域的最后一个item
        SINGLE,         // 处于中间的item
        SINGLE_TOP,     // 处于最上方的item
        SINGLE_BOTTOM,   // 处于最下方的item
        UNDEFINED             // 空位置
    }

    public static class MeTabItem {
        public String action;
        public String icon;
        public String tipsIcon;
        public SplitText name;
        public SplitText tips;
        public boolean needLogin;
        public String type;

        public MeTabItemPosition position = MeTabItemPosition.UNDEFINED;

        public MeTabItem(String action, String icon, String tipsIcon, SplitText name, SplitText tips, boolean needLogin, String type) {
            this.action = action;
            this.icon = icon;
            this.tipsIcon = tipsIcon;
            this.name = name;
            this.tips = tips;
            this.needLogin = needLogin;
            this.type = type;
        }
    }
}
