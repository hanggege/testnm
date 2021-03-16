package com.net.network.chick.friends;

import com.mei.orc.http.retrofit.RxRequest;
import com.net.ApiInterface;
import com.net.model.chick.friends.WorkListResult;

import io.reactivex.Observable;

/**
 * Song Jian
 */
public class WorkListRequest extends RxRequest<WorkListResult.Response, ApiInterface> {

    private int mTargetUserId;
    private int pageNo;
    private int workPageSize = 6;

    public WorkListRequest(int targetUserId, int pageNo) {
        super(WorkListResult.Response.class, ApiInterface.class);
        this.mTargetUserId = targetUserId;
        this.pageNo = pageNo;

    }

    public WorkListRequest(int targetUserId, int pageNo, int workPageSize) {
        super(WorkListResult.Response.class, ApiInterface.class);
        this.mTargetUserId = targetUserId;
        this.pageNo = pageNo;
        this.workPageSize = workPageSize;
    }


    @Override
    protected String createCacheKey() {
        return toString();
    }

    @Override
    public Observable<WorkListResult.Response> loadDataFromNetwork() throws Exception {
        return getService().getProductList(mTargetUserId, pageNo, workPageSize);
    }

}
