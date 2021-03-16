package com.net.network.chat;

import com.mei.orc.http.retrofit.RxRequest;
import com.net.ApiInterface;
import com.net.model.chat.ChatAuth;

import io.reactivex.Observable;

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2020/4/21
 */
public class ChatAuthRequest extends RxRequest<ChatAuth.Response, ApiInterface> {
    private String userId;

    public ChatAuthRequest(String userId) {
        super(ChatAuth.Response.class, ApiInterface.class);
        this.userId = userId;
    }

    @Override
    protected String createCacheKey() {
        return toString();
    }

    @Override
    protected Observable<ChatAuth.Response> loadDataFromNetwork() throws Exception {
        return getService().ChatAuth(userId);
    }
}
