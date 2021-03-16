package com.net.network.chick.course;

import com.mei.orc.http.retrofit.RxRequest;
import com.net.ApiInterface;
import com.net.model.chick.course.ToReceiveCourseInfo;

import io.reactivex.Observable;

/**
 * @author Created by lenna on 2020/11/9
 */
public class ToReceiveCourseRequest extends RxRequest<ToReceiveCourseInfo.Response, ApiInterface> {
    private int userId;
    private int publisherId;
    private String referType;
    private String referId;

    public ToReceiveCourseRequest(int userId, int publisherId, String referType, String referId) {
        super(ToReceiveCourseInfo.Response.class, ApiInterface.class);
        this.userId = userId;
        this.publisherId = publisherId;
        this.referType = referType;
        this.referId = referId;
    }

    @Override
    protected String createCacheKey() {
        return toString();
    }

    @Override
    protected Observable<ToReceiveCourseInfo.Response> loadDataFromNetwork() throws Exception {
        return getService().toReceiveCourse(userId, publisherId, referType, referId);
    }
}
