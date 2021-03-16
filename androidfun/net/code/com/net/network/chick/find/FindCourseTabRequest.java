package com.net.network.chick.find;

import com.mei.orc.http.retrofit.RxRequest;
import com.net.ApiInterface;
import com.net.model.chick.find.FindCourseTab;

import io.reactivex.Observable;

/**
 * FindCourseTabRequest
 *
 * @author kun
 * @version 1.0.0
 * @since 2020-07-23
 */
public class FindCourseTabRequest extends RxRequest<FindCourseTab.Response, ApiInterface> {

    private String type;

    public FindCourseTabRequest(String type) {
        super(FindCourseTab.Response.class, ApiInterface.class);
        this.type = type;
    }

    @Override
    protected String createCacheKey() {
        return toString();
    }

    @Override
    protected Observable<FindCourseTab.Response> loadDataFromNetwork() throws Exception {
        return getService().getFindCourseTab(type);
    }
}
