package com.net.network.chat;

import com.mei.orc.http.retrofit.RxRequest;
import com.net.ApiInterface;
import com.net.model.chat.MessageUnread;

import io.reactivex.Observable;

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2020/4/21
 */
public class MessageUnreadRequest extends RxRequest<MessageUnread.Response, ApiInterface> {

    public MessageUnreadRequest() {
        super(MessageUnread.Response.class, ApiInterface.class);
    }

    @Override
    protected String createCacheKey() {
        return toString();
    }

    @Override
    protected Observable<MessageUnread.Response> loadDataFromNetwork() throws Exception {
        return getService().messageUnread();
    }
}
