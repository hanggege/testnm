package com.mei.orc.john.network.request;

import com.mei.orc.http.retrofit.RxRequest;
import com.mei.orc.john.model.Empty_data;
import com.mei.orc.john.network.JohnInterface;

import io.reactivex.Observable;

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2019/1/29
 */
public class Report_Openinstall_Request extends RxRequest<Empty_data.Response, JohnInterface> {

    private String data;

    public Report_Openinstall_Request(String data) {
        super(Empty_data.Response.class, JohnInterface.class);
        this.data = data;
    }

    @Override
    protected String createCacheKey() {
        return toString();
    }

    @Override
    protected Observable<Empty_data.Response> loadDataFromNetwork() throws Exception {
        return getService().Report_Openinstall(data);
    }
}
