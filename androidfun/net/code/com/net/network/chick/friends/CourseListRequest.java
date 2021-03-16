package com.net.network.chick.friends;

import com.mei.orc.http.retrofit.RxRequest;
import com.net.ApiInterface;
import com.net.model.chick.friends.CourseListResult;

import io.reactivex.Observable;

/**
 * @author Created by lenna on 2020/7/7
 */
public class CourseListRequest extends RxRequest<CourseListResult.Response, ApiInterface> {
    private int userId;
    private int coursePageNo;

    public CourseListRequest(int userId, int pageNo) {
        super(CourseListResult.Response.class, ApiInterface.class);
        this.userId = userId;
        this.coursePageNo = pageNo;
    }

    @Override
    protected String createCacheKey() {
        return toString();
    }

    @Override
    protected Observable<CourseListResult.Response> loadDataFromNetwork() throws Exception {
        return getService().getCourseList(userId, coursePageNo);
    }
}
