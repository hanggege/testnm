package com.net.network.chick.friends;

import com.mei.orc.http.retrofit.RxRequest;
import com.net.ApiInterface;
import com.net.model.chick.friends.ServiceListResult;

import io.reactivex.Observable;

/**
 * Song Jian
 */
public class ServiceListRequest extends RxRequest<ServiceListResult.Response, ApiInterface> {

    private int mTargetUserId;
    private int pageNo;
    private int pageSize = 6;

    public ServiceListRequest(int targetUserId, int pageNo) {
        super(ServiceListResult.Response.class, ApiInterface.class);
        mTargetUserId = targetUserId;
        this.pageNo = pageNo;
    }


    @Override
    protected String createCacheKey() {
        return toString();
    }

    @Override
    public Observable<ServiceListResult.Response> loadDataFromNetwork() throws Exception {
        return getService().getServiceList(mTargetUserId, pageNo, pageSize);
    }

}
