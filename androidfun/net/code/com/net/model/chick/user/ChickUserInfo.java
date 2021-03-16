package com.net.model.chick.user;

import androidx.annotation.Nullable;

import com.mei.orc.http.response.BaseResponse;
import com.net.model.user.UserInfo;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zzw on 2019-12-09
 * Des:
 */
public class ChickUserInfo {

    @Nullable
    public UserInfo info;
    @Nullable
    public Extra extra;
    @NotNull
    public List<String> years = new ArrayList<>();

    public static class Response extends BaseResponse<ChickUserInfo> {

    }

    public static class Extra {
        //心币余额
        public double coinBalance;
        //基本资料是否完善
        public boolean ifPerfectBasicInfo;
        //是否为提审版本  控制我的钱包 条目是否隐藏  默认隐藏
        public boolean isReviewingVersion = true;

        //是否充值过
        public int hasDeposit;

        public boolean bindPhone = false;
        public boolean bindWechat = false;
        public boolean bindTask = false;
        // 是否有权限设置隐身功能
        public int canSetRoomInvisible = 0;
    }

}
