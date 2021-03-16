package com.mei.orc.john.network.request;

import com.mei.orc.http.retrofit.RxRequest;
import com.mei.orc.john.model.App_Version_Check_Info;
import com.mei.orc.john.network.JohnInterface;

import io.reactivex.Observable;

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2019-06-10
 */
public class App_Version_Check_Info_Request extends RxRequest<App_Version_Check_Info.Response, JohnInterface> {

    public App_Version_Check_Info_Request() {
        super(App_Version_Check_Info.Response.class, JohnInterface.class);
    }

    @Override
    protected String createCacheKey() {
        return toString();
    }

    @Override
    protected Observable<App_Version_Check_Info.Response> loadDataFromNetwork() throws Exception {
        return getService().appVersionCheck();
    }
}
