package com.net.network.tim;


import com.mei.orc.http.retrofit.RxRequest;
import com.net.ApiInterface;
import com.net.model.chick.tim.TimSendMessage;

import io.reactivex.Observable;

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2019-12-09
 */
public class TimSendMessageRequest extends RxRequest<TimSendMessage.Response, ApiInterface> {

    public int fromUser;
    public int toUser;
    public String groupId;
    public String msgContent;

    public TimSendMessageRequest() {
        super(TimSendMessage.Response.class, ApiInterface.class);
    }

    @Override
    protected String createCacheKey() {
        return toString();
    }

    @Override
    protected Observable<TimSendMessage.Response> loadDataFromNetwork() throws Exception {
        return getService().TimSendMessage(fromUser, toUser == 0 ? "" : String.valueOf(toUser), groupId, msgContent);
    }
}
