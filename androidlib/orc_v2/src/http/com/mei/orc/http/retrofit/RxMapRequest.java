package com.mei.orc.http.retrofit;


import com.mei.orc.http.response.DataResponse;

import java.util.Map;

import io.reactivex.Observable;

/**
 * Created by joker on 16/11/16.
 */

public abstract class RxMapRequest<RESULT extends DataResponse, R> extends RxRequest<RESULT, R> {


    public RxMapRequest(Class<RESULT> responseClass, Class<R> retrofitedInterfaceClass) {
        super(responseClass, retrofitedInterfaceClass);
    }

    @Override
    public Observable<RESULT> loadDataFromNetwork() throws Exception {
        return definitionMethod(hashJavaBeanToMap(this));
    }

    protected abstract Observable<RESULT> definitionMethod(Map<String, Object> map);


}
