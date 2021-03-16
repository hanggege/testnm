package com.net.network.chat;

import com.mei.orc.http.retrofit.RxRequest;
import com.net.ApiInterface;
import com.net.model.chat.CommandPhrase;

import io.reactivex.Observable;

/**
 * @author Created by zyh on 2020/4/21
 */
public class CommandPhraseAddRequest extends RxRequest<CommandPhrase.Base, ApiInterface> {
    private final String content;

    public CommandPhraseAddRequest(String content) {
        super(CommandPhrase.Base.class, ApiInterface.class);
        this.content = content;
    }

    @Override
    protected String createCacheKey() {
        return toString();
    }

    @Override
    protected Observable<CommandPhrase.Base> loadDataFromNetwork() throws Exception {
        return getService().addCommandPhrase(content);
    }
}
