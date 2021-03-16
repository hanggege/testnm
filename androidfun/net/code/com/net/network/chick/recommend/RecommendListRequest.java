package com.net.network.chick.recommend;


import com.mei.orc.http.retrofit.RxRequest;
import com.net.ApiInterface;
import com.net.model.chick.recommend.RecommendList;

import io.reactivex.Observable;

/**
 * author : Song Jian
 * date   : 2020/1/8
 * desc   :
 */
public class RecommendListRequest extends RxRequest<RecommendList.Response, ApiInterface> {
    private String pageNo;
    private Boolean mIsMatch = true;
    private int pageSize = 10;

    public RecommendListRequest(String pageNo, Boolean isMatch) {
        super(RecommendList.Response.class, ApiInterface.class);
        this.pageNo = pageNo;
        mIsMatch = isMatch;
    }

    public RecommendListRequest(String pageNo, int paseSize) {
        super(RecommendList.Response.class, ApiInterface.class);
        this.pageNo = pageNo;
        this.pageSize = paseSize;
    }

    @Override
    protected String createCacheKey() {
        return toString();
    }

    @Override
    protected Observable<RecommendList.Response> loadDataFromNetwork() throws Exception {
        if (mIsMatch) {

            return getService().recommendList(pageNo);
        } else {
            return getService().recommendList(pageNo, mIsMatch);

        }

    }
}