package com.net.network.room;

import com.mei.orc.http.retrofit.RxRequest;
import com.net.ApiInterface;
import com.net.model.room.GenericEffectConfig;

import io.reactivex.Observable;

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2020/4/16
 */
public class GenericEffectConfigRequest extends RxRequest<GenericEffectConfig.Response, ApiInterface> {
    public GenericEffectConfigRequest() {
        super(GenericEffectConfig.Response.class, ApiInterface.class);
    }

    @Override
    protected String createCacheKey() {
        return null;
    }

    @Override
    protected Observable<GenericEffectConfig.Response> loadDataFromNetwork() throws Exception {
        return getService().getGenericEffectConfig();
    }
}
