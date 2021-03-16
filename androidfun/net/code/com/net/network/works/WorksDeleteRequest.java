package com.net.network.works;

import com.mei.orc.http.retrofit.RxRequest;
import com.net.ApiInterface;
import com.net.model.works.DeleteWorksModel;

import io.reactivex.Observable;

/**
 * Created by steven on 15/4/25.
 * 用户作品置顶
 */
public class WorksDeleteRequest extends RxRequest<DeleteWorksModel.Response, ApiInterface> {
    private int nextPageNo;
    private String worksID;

    public WorksDeleteRequest(String worksID, int nextPageNo) {
        super(DeleteWorksModel.Response.class, ApiInterface.class);
        this.worksID = worksID;
        this.nextPageNo = nextPageNo;
    }

    @Override
    protected String createCacheKey() {
        return toString();
    }

    @Override
    protected Observable<DeleteWorksModel.Response> loadDataFromNetwork() throws Exception {
        return getService().worksDelete(worksID, nextPageNo);
    }
}
