package com.net.network.chick.friends;

import com.mei.orc.http.retrofit.RxRequest;
import com.net.ApiInterface;
import com.net.model.chick.friends.LikeNexusListResult;

import io.reactivex.Observable;

/**
 * Created by zzw on 2020-01-11
 * Des:
 */
public class LikeNexusListRequest extends RxRequest<LikeNexusListResult.Response, ApiInterface> {

    public LikeNexusListRequest() {
        super(LikeNexusListResult.Response.class, ApiInterface.class);
    }

    @Override
    protected String createCacheKey() {
        return toString();
    }

    @Override
    protected Observable<LikeNexusListResult.Response> loadDataFromNetwork() throws Exception {
        return getService().getLikeNexusList();

    }
}