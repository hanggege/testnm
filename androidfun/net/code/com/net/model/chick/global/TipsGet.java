package com.net.model.chick.global;

import com.joker.im.custom.chick.SplitText;
import com.mei.orc.http.response.BaseResponse;
import com.net.model.chick.tim.SystemInviteResult;

import java.util.List;

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2020/8/4
 */

public class TipsGet {
    public String nextStartKey;
    public List<ActionInfo> tipsList;
    public SystemDialogInfo message;
    public String tabId;

    public static class Response extends BaseResponse<TipsGet> {

    }

    public static class ActionInfo {
        public int userId;
        public int gender;
        public String msgId;
        public long lastMs;//毫秒值
        public String avatar;
        public String roomId;
        public List<SplitText> text;
    }

    public static class SystemDialogInfo {
        public String type;
        public SystemInviteResult.SystemInviteJoin content;
    }

}
