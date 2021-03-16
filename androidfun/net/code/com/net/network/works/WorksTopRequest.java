package com.net.network.works;

import com.mei.orc.http.retrofit.RxRequest;
import com.mei.orc.john.model.Empty_data;
import com.net.ApiInterface;

import io.reactivex.Observable;

/**
 * Created by steven on 15/4/25.
 * 用户作品置顶
 */
public class WorksTopRequest extends RxRequest<Empty_data.Response, ApiInterface> {


    private String worksID;
    /**
     * 0 非置顶
     * 1 置顶
     */
    private int isTop;

    public WorksTopRequest(String worksID, int isTop) {
        super(Empty_data.Response.class, ApiInterface.class);
        this.worksID = worksID;
        this.isTop = isTop;
    }

    @Override
    protected String createCacheKey() {
        return toString();
    }

    @Override
    protected Observable<Empty_data.Response> loadDataFromNetwork() throws Exception {
        return getService().worksTop(worksID, isTop);
    }


}
