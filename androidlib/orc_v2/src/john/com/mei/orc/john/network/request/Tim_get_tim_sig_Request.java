package com.mei.orc.john.network.request;

import com.mei.orc.http.retrofit.RxRequest;
import com.mei.orc.john.model.Tim_get_tim_sig;
import com.mei.orc.john.network.JohnInterface;

import io.reactivex.Observable;

/**
 * 佛祖保佑         永无BUG
 * <p>
 * Created by joker on 2017/2/21.
 */

public class Tim_get_tim_sig_Request extends RxRequest<Tim_get_tim_sig.Response, JohnInterface> {

    private String project_name;

    public Tim_get_tim_sig_Request(String project_name) {
        super(Tim_get_tim_sig.Response.class, JohnInterface.class);
        this.project_name = project_name;
    }

    @Override
    protected String createCacheKey() {
        return toString();
    }

    @Override
    public String toString() {
        return "Tim_get_tim_sig_Request{" +
                "project_name='" + project_name + '\'' +
                '}';
    }

    @Override
    protected Observable<Tim_get_tim_sig.Response> loadDataFromNetwork() throws Exception {
        return getService().Tim_get_tim_sig(project_name);
    }
}
