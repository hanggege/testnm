package com.net.network.chick.video;

import com.mei.orc.http.retrofit.RxRequest;
import com.net.ApiInterface;
import com.net.model.chick.video.LikeVideoList;

import io.reactivex.Observable;

/**
 * LikeVideoListRequest
 *
 * @author kun
 * @version 1.0.0
 * @since 2020-08-18
 */
public class LikeVideoListRequest extends RxRequest<LikeVideoList.Response, ApiInterface> {

    private int pageNo;
    private int pageSize;

    public LikeVideoListRequest(int pageNo, int pageSize) {
        super(LikeVideoList.Response.class, ApiInterface.class);
        this.pageNo = pageNo;
        this.pageSize = pageSize;
    }

    @Override
    protected String createCacheKey() {
        return toString();
    }

    @Override
    protected Observable<LikeVideoList.Response> loadDataFromNetwork() throws Exception {
        return getService().shortLikeVideoList(pageNo, pageSize);
    }
}
