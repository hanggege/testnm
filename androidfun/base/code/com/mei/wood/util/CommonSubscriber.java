package com.mei.wood.util;

import android.text.TextUtils;

import com.mei.orc.http.response.DataResponse;

import io.reactivex.observers.ResourceObserver;

/**
 * @author caowei
 * @email 646030315@qq.com
 * @time Created on 2016/11/7 0007
 */

public abstract class CommonSubscriber<Response extends DataResponse> extends ResourceObserver<Response> {


    public CommonSubscriber() {
    }

    @Override
    public void onError(Throwable e) {
        if (e != null) {
            onFail(e.getMessage());
        }
    }

    @Override
    public void onNext(Response response) {
        if (response.isSuccess()) {
            onSuccess(response);
        } else {
            int errorRtn = response.getRtn();
            String message = response.getErrMsg();
            if (TextUtils.isEmpty(message)) {
                message = "Unknown message with code: " + errorRtn;
            }
            onFail(message);
            onResponseError(response);
        }
    }

    @Override
    public void onComplete() {

    }

    public abstract void onSuccess(Response response);

    public abstract void onFail(String message);

    public void onResponseError(Response response) {

    }
}
