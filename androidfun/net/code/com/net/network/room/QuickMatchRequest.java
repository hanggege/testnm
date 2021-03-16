package com.net.network.room;

import com.mei.orc.http.retrofit.RxRequest;
import com.net.ApiInterface;
import com.net.model.room.MatchInfo;

import io.reactivex.Observable;

/**
 * @author Created by lenna on 2020/11/16
 * 快速匹配请求
 */
public class QuickMatchRequest extends RxRequest<MatchInfo.Response, ApiInterface> {
    private int proCateId;

    public QuickMatchRequest(int proCateId) {
        super(MatchInfo.Response.class, ApiInterface.class);
        this.proCateId = proCateId;
    }

    @Override
    protected String createCacheKey() {
        return toString();
    }

    @Override
    protected Observable<MatchInfo.Response> loadDataFromNetwork() throws Exception {
        return getService().quickMatch(proCateId);
    }
}
