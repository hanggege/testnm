package com.net.model.chick.tim;


import com.joker.im.custom.chick.InviteUp;
import com.mei.orc.http.response.BaseResponse;


public class SystemInviteResult {

    public static class Response extends BaseResponse<SystemInviteResult> {

    }

    public static class SystemInviteJoin {
        public InviteUp inviteInfo = new InviteUp();
        public String roomId = "";
        public int timeOut;
        public long userId;
        public String title;
        public boolean needCallHandle;
        public String action = "";
        public String image = "";
        public boolean showCloseBtn;// 是否展示关闭按钮
        public boolean showTimeOut;//显示倒计时，仅当countdown>0时有效
        public boolean canClose;// 能否点击空白区域关闭
    }
}
