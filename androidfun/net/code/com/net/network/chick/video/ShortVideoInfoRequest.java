package com.net.network.chick.video;

import com.mei.orc.http.retrofit.RxRequest;
import com.net.ApiInterface;
import com.net.model.chick.video.ShortVideoInfo;

import io.reactivex.Observable;

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2020/7/11
 */
public class ShortVideoInfoRequest extends RxRequest<ShortVideoInfo.Response, ApiInterface> {

    private String seqId;
    private String publisherId;
    private String tagId;
    private boolean isMyLike;
    private boolean isAllTag;


    public ShortVideoInfoRequest(String seqId, String publisherId, String tagId, boolean isMyLike, boolean isAllTag) {
        super(ShortVideoInfo.Response.class, ApiInterface.class);
        this.seqId = seqId;
        this.publisherId = publisherId;
        this.tagId = tagId;
        this.isMyLike = isMyLike;
        this.isAllTag = isAllTag;
    }

    @Override
    protected String createCacheKey() {
        return toString();
    }

    @Override
    protected Observable<ShortVideoInfo.Response> loadDataFromNetwork() throws Exception {
        return getService().ShortVideoInfo(seqId, publisherId, tagId, isMyLike, isAllTag);
    }
}
