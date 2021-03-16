package com.net.model.chat;

import com.joker.im.custom.chick.ChickCustomData;
import com.mei.orc.http.response.BaseResponse;

import java.util.List;

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2020/4/21
 */
public class MessageUnread {
    public static class Response extends BaseResponse<MessageUnread> {

    }

    public List<ApplyExclusiveItem> exclusiveApply;

    public static class ApplyExclusiveItem {
        public ChickCustomData data;
        public String type;
    }
}
