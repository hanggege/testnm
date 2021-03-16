package com.mei.orc.john.network.request;

import com.mei.orc.http.retrofit.RxRequest;
import com.mei.orc.john.model.UPLOAD_avatar;
import com.mei.orc.john.network.JohnInterface;

import io.reactivex.Observable;

/**
 * Created by steven on 15/4/27.
 */
public class UPLOAD_avatar_Request extends RxRequest<UPLOAD_avatar.Response, JohnInterface> {
    @Override
    public String toString() {
        return "UPLOAD_avatar_Request{" +
                '}';
    }

    public UPLOAD_avatar_Request() {
        super(UPLOAD_avatar.Response.class, JohnInterface.class);
    }

    @Override
    protected String createCacheKey() {
        return toString();
    }

    @Override
    public Observable<UPLOAD_avatar.Response> loadDataFromNetwork() throws Exception {
        return getService().UPLOAD_avatar();
    }
}
