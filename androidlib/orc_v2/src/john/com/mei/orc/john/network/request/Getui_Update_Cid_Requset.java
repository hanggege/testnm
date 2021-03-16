package com.mei.orc.john.network.request;


import com.mei.orc.http.retrofit.RxRequest;
import com.mei.orc.john.model.Empty_data;
import com.mei.orc.john.network.JohnInterface;

import io.reactivex.Observable;

/**
 * Created by 杨强彪 on 2016/5/30.
 *
 * @描述：
 */
public class Getui_Update_Cid_Requset extends RxRequest<Empty_data.Response, JohnInterface> {


    public Getui_Update_Cid_Requset() {
        super(Empty_data.Response.class, JohnInterface.class);
    }

    @Override
    protected String createCacheKey() {
        return toString();
    }

    @Override
    public Observable<Empty_data.Response> loadDataFromNetwork() throws Exception {
        return getService().Update_Cid();
    }
}