package com.net.model.share;

import com.mei.orc.http.response.BaseResponse;

/**
 * Created by hang on 2019-12-17.
 */
public class LivingShareResult {

    public static class Response extends BaseResponse<LivingShareResult> {
    }

    public String cashBack;
    public String shareTitle;
    public String shareSubject;
    public String shareImage;
    public boolean activity;
    public String activityUrl; //活动详情链接
    public boolean isPublisher;
    //分享后端可配文案
    public String firstText; //第一段文案
    public String theUnit;   //分享可获得的价值单位如：心币，元，美元。。。

    //主播端分享后端可配文案
    public String publisherText;
    public String publisherUnit;  //如 ：返现。。。

    //按钮文案
    public String activityPageButton;  //查看详情
    //房间信息
//    public RoomInfo roomInfo;
    public InviteUserParam inviteUserParam;

    public static class InviteUserParam {

        /**
         * userReg : 20
         * userCount : 100
         * userCountPublisher : 20
         * userRegGet : 5
         * userDepositDayLimit : 7
         * userDeposit : 80
         */

        public int userReg;
        public int userCount;
        public int userCountPublisher;
        public int userRegGet;
        public int userDepositDayLimit;
        public int userDeposit;

    }
}
