package com.net.network.report;

import android.app.Activity;

import com.mei.orc.http.retrofit.RxRequest;
import com.mei.orc.john.model.Empty_data;
import com.mei.wood.util.AppManager;
import com.net.ApiInterface;

import io.reactivex.Observable;

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2020/10/26
 */
public class MessageClickRequest extends RxRequest<Empty_data.Response, ApiInterface> {
    public String payload;


    public MessageClickRequest(String payload) {
        super(Empty_data.Response.class, ApiInterface.class);
        this.payload = payload;
    }

    @Override
    protected String createCacheKey() {
        return toString();
    }

    @Override
    protected Observable<Empty_data.Response> loadDataFromNetwork() throws Exception {
        Activity activity = AppManager.getInstance().currentActivity();
        return getService().messageClickReport(payload, activity == null ? "" : activity.getClass().getSimpleName());
    }
}