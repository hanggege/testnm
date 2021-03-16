package com.net.network.chick.report;

import androidx.annotation.NonNull;

import com.mei.orc.http.retrofit.RxRequest;
import com.mei.orc.john.model.Empty_data;
import com.mei.orc.util.json.JsonExtKt;
import com.net.ApiInterface;

import java.util.Map;

import io.reactivex.Observable;

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2020/7/13
 */
public class GIOUploadRequest extends RxRequest<Empty_data.Response, ApiInterface> {

    public Map<String, String> json;

    public GIOUploadRequest(@NonNull Map<String, String> json) {
        super(Empty_data.Response.class, ApiInterface.class);
        this.json = json;
    }

    @Override
    protected String createCacheKey() {
        return toString();
    }

    @Override
    protected Observable<Empty_data.Response> loadDataFromNetwork() throws Exception {
        String jsonData = JsonExtKt.toJson(json);
        return getService().GIOUpload(jsonData == null ? "" : jsonData);
    }
}
