package com.net.network.newhome;

import androidx.annotation.NonNull;

import com.mei.orc.http.retrofit.RxRequest;
import com.net.ApiInterface;
import com.net.model.newhome.List_cate;

import io.reactivex.Observable;

/**
 * Created by hang on 2018/10/30.
 */
public class List_cate_request extends RxRequest<List_cate.Response, ApiInterface> {
    private String start;
    private int cate_id;

    public List_cate_request(String start, int cate_id) {
        super(List_cate.Response.class, ApiInterface.class);
        this.start = start;
        this.cate_id = cate_id;
    }

    @Override
    protected String createCacheKey() {
        return toString();
    }

    @Override
    protected Observable<List_cate.Response> loadDataFromNetwork() throws Exception {
        return getService().List_cate(start, cate_id);
    }

    @NonNull
    @Override
    public String toString() {
        return "List_cate_request={" +
                "start = " + start
                + ",cate_id = " + cate_id
                + "}";
    }
}
