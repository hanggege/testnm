package com.net.network.chick.upload;

import com.mei.orc.http.retrofit.RxRequest;
import com.net.ApiInterface;
import com.net.model.chick.upload.CheckAvatarViolation;

import io.reactivex.Observable;

/**
 * Created by steven on 15/4/27.
 */
public class CheckAvatarViolationRequest extends RxRequest<CheckAvatarViolation.Response, ApiInterface> {
    private String imageUrl;

    public CheckAvatarViolationRequest(String imageUrl) {
        super(CheckAvatarViolation.Response.class, ApiInterface.class);
        this.imageUrl = imageUrl;
    }

    @Override
    public String toString() {
        return "CheckAvatarViolationRequest{" +
                "imageUrl=" + imageUrl +
                '}';
    }

    @Override
    protected String createCacheKey() {
        return toString();
    }

    @Override
    public Observable<CheckAvatarViolation.Response> loadDataFromNetwork() throws Exception {
        return getService().CheckAvatarViolation(imageUrl);
    }
}
