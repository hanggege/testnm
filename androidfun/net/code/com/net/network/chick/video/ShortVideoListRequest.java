package com.net.network.chick.video;

import com.mei.orc.http.retrofit.RxRequest;
import com.net.ApiInterface;
import com.net.model.chick.video.ShortVideoList;

import io.reactivex.Observable;

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2020/7/11
 */
public class ShortVideoListRequest extends RxRequest<ShortVideoList.Response, ApiInterface> {

    private int tagId;
    private int pageNo;

    public ShortVideoListRequest(int tagId, int pageNo) {
        super(ShortVideoList.Response.class, ApiInterface.class);
        this.tagId = tagId;
        this.pageNo = pageNo;
    }

    @Override
    protected String createCacheKey() {
        return toString();
    }

    @Override
    protected Observable<ShortVideoList.Response> loadDataFromNetwork() throws Exception {
        return getService().ShortVideoList(tagId, pageNo);
    }
}
