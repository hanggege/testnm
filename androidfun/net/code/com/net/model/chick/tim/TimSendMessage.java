package com.net.model.chick.tim;

import com.mei.orc.http.response.BaseResponse;

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2019-12-09
 */
public class TimSendMessage {
    public static class Response extends BaseResponse<TimSendMessage> {

    }

    public String msgId;
    public String msgContent;
}
