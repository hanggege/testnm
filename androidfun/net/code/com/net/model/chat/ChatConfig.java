package com.net.model.chat;

import androidx.annotation.Nullable;

import com.joker.im.custom.chick.CouponInfo;
import com.mei.im.ui.view.menu.ChatMenu;
import com.mei.orc.http.response.BaseResponse;

import java.util.ArrayList;
import java.util.List;

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2020/4/21
 */
public class ChatConfig {
    public static class Response extends BaseResponse<ChatConfig> {

    }

    public int myRoleId;
    public int targetRoleId;
    public int targetUserId;
    public String targetNickname;
    public String tips = "私聊1心币/每条";
    @Nullable
    public String specialServiceTips;
    public ArrayList<String> chatPhrase;
    @Nullable
    public CouponInfo couponTips;

    public List<ChatMenu> moreMenu;
}
