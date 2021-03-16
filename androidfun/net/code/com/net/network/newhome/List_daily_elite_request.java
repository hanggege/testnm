package com.net.network.newhome;

import androidx.annotation.NonNull;

import com.mei.orc.http.retrofit.RxRequest;
import com.net.ApiInterface;
import com.net.model.newhome.List_daily_elite;

import io.reactivex.Observable;

/**
 * Created by hang on 2018/10/30.
 */
public class List_daily_elite_request extends RxRequest<List_daily_elite.Response, ApiInterface> {
    private String start;
    private int pro_cate_id;

    public List_daily_elite_request(String start, int pro_cate_id) {
        super(List_daily_elite.Response.class, ApiInterface.class);
        this.start = start;
        this.pro_cate_id = pro_cate_id;
    }

    @Override
    protected String createCacheKey() {
        return toString();
    }

    @Override
    protected Observable<List_daily_elite.Response> loadDataFromNetwork() throws Exception {
        return getService().List_daily_elite(start, pro_cate_id);
    }

    @NonNull
    @Override
    public String toString() {
        return "List_daily_elite_request={" +
                "start = " + start
                + "}";
    }
}
