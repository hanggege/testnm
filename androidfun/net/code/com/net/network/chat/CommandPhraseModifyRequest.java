package com.net.network.chat;

import com.google.gson.Gson;
import com.mei.orc.http.retrofit.RxRequest;
import com.net.ApiInterface;
import com.net.model.chat.CommandPhraseModifyInfo;

import java.util.ArrayList;

import io.reactivex.Observable;

/**
 * @author Created by zyh on 2020/4/21
 */
public class CommandPhraseModifyRequest extends RxRequest<CommandPhraseModifyInfo.Base, ApiInterface> {
    public String list;

    public CommandPhraseModifyRequest(ArrayList<String> data) {
        super(CommandPhraseModifyInfo.Base.class, ApiInterface.class);
        /**
         * 常用语需要是jsonArray
         */
        this.list = new Gson().toJson(data);
    }

    @Override
    protected String createCacheKey() {
        return toString();
    }

    @Override
    protected Observable<CommandPhraseModifyInfo.Base> loadDataFromNetwork() throws Exception {
        return getService().updateCommandPhrase(list);
    }
}
