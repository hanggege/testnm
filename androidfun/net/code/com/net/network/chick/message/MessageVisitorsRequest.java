package com.net.network.chick.message;

import com.mei.orc.http.retrofit.RxRequest;
import com.net.ApiInterface;
import com.net.model.chick.message.VisitorsMessage;

import io.reactivex.Observable;


/**
 * @author Created by lenna on 2020/6/15
 */
public class MessageVisitorsRequest extends RxRequest<VisitorsMessage.Response, ApiInterface> {
    private int mPageNo;
    private int mPageSize;

    public MessageVisitorsRequest(int pageNo, int pageSize) {
        super(VisitorsMessage.Response.class, ApiInterface.class);
        mPageNo = pageNo;
        mPageSize = pageSize;
    }


    @Override
    protected String createCacheKey() {
        return toString();
    }

    @Override
    protected Observable<VisitorsMessage.Response> loadDataFromNetwork() throws Exception {
        return getService().getVisitorsList(mPageNo, mPageSize);
    }
}
