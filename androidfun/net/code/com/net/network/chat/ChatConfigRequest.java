package com.net.network.chat;

import com.mei.orc.http.retrofit.RxRequest;
import com.net.ApiInterface;
import com.net.model.chat.ChatConfig;

import io.reactivex.Observable;

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2020/4/21
 */
public class ChatConfigRequest extends RxRequest<ChatConfig.Response, ApiInterface> {

    private String userId;
    private String fromTag;

    public ChatConfigRequest(String userId, String fromTag) {
        super(ChatConfig.Response.class, ApiInterface.class);
        this.userId = userId;
        this.fromTag = fromTag;
    }

    @Override
    protected String createCacheKey() {
        return toString();
    }

    @Override
    protected Observable<ChatConfig.Response> loadDataFromNetwork() throws Exception {

        return getService().ChatConfig(userId, fromTag);
    }
}
