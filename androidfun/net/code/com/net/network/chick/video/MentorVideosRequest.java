package com.net.network.chick.video;

import com.mei.orc.http.retrofit.RxRequest;
import com.net.ApiInterface;
import com.net.model.chick.video.MentorShortVideo;

import io.reactivex.Observable;

/**
 * @author Created by lenna on 2020/8/20
 */
public class MentorVideosRequest extends RxRequest<MentorShortVideo.Response, ApiInterface> {
    private int userId;
    private int pageNo;
    private String excludeId;


    public MentorVideosRequest(int userId, int pageNo, String excludeId) {
        super(MentorShortVideo.Response.class, ApiInterface.class);
        this.userId = userId;
        this.pageNo = pageNo;
        this.excludeId = excludeId;
    }

    @Override
    protected String createCacheKey() {
        return toString();
    }

    @Override
    protected Observable<MentorShortVideo.Response> loadDataFromNetwork() throws Exception {
        return getService().getMentorVideos(userId, pageNo, excludeId);
    }
}
