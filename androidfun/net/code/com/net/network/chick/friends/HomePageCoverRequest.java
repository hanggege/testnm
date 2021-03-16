package com.net.network.chick.friends;

import com.mei.orc.http.retrofit.RxRequest;
import com.mei.orc.john.model.Empty_data;
import com.net.ApiInterface;

import io.reactivex.Observable;

/**
 * Created by steven on 15/4/25.
 * 封面上传
 */
public class HomePageCoverRequest extends RxRequest<Empty_data.Response, ApiInterface> {

    private String cover;

    public HomePageCoverRequest(String cover) {
        super(Empty_data.Response.class, ApiInterface.class);
        this.cover = cover;
    }

    @Override
    protected String createCacheKey() {
        return toString();
    }

    @Override
    protected Observable<Empty_data.Response> loadDataFromNetwork() throws Exception {
        return getService().homepageCover(cover);
    }


}
