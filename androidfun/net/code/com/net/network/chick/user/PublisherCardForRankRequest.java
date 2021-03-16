package com.net.network.chick.user;

import com.mei.orc.http.retrofit.RxRequest;
import com.net.ApiInterface;
import com.net.model.user.DataCard;

import io.reactivex.Observable;

/**
 * author : Song Jian
 * date   : 2020/2/25
 * desc   : 我的页面
 */
public class PublisherCardForRankRequest extends RxRequest<DataCard.Response, ApiInterface> {

    private int userId;

    public PublisherCardForRankRequest(int userId) {
        super(DataCard.Response.class, ApiInterface.class);
        this.userId = userId;
    }

    @Override
    protected String createCacheKey() {
        return toString();
    }

    @Override
    protected Observable<DataCard.Response> loadDataFromNetwork() throws Exception {
        return getService().publisherCardForRank(userId);
    }
}