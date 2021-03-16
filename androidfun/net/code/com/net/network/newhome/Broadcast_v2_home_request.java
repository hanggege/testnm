package com.net.network.newhome;

import androidx.annotation.NonNull;

import com.mei.orc.http.retrofit.RxRequest;
import com.net.ApiInterface;
import com.net.model.newhome.Broadcast_v2_home;

import io.reactivex.Observable;

/**
 * Created by hang on 2018/10/31.
 */
public class Broadcast_v2_home_request extends RxRequest<Broadcast_v2_home.Response, ApiInterface> {
    private int cate_id;
    private int pro_cate_id;

    public Broadcast_v2_home_request(int cate_id, int pro_cate_id) {
        super(Broadcast_v2_home.Response.class, ApiInterface.class);
        this.cate_id = cate_id;
        this.pro_cate_id = pro_cate_id;
    }

    @Override
    protected String createCacheKey() {
        return toString();
    }

    @Override
    protected Observable<Broadcast_v2_home.Response> loadDataFromNetwork() throws Exception {
        return getService().Broadcast_v2_home(cate_id, pro_cate_id);
    }

    @NonNull
    @Override
    public String toString() {
        return "Broadcast_v2_home_request={cate_id" + cate_id + "}";
    }
}
