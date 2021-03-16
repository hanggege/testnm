package com.net.network.chick.evaluation;

import com.mei.orc.http.retrofit.RxRequest;
import com.net.ApiInterface;
import com.net.model.chick.evaluation.TagBean;

import io.reactivex.Observable;


/**
 * Created by zzw on 2019-12-16
 * Des:
 */
public class EvaluationTagsRequest extends RxRequest<TagBean.Response, ApiInterface> {


    public EvaluationTagsRequest() {
        super(TagBean.Response.class, ApiInterface.class);
    }

    @Override
    protected String createCacheKey() {
        return toString();
    }

    @Override
    protected Observable<TagBean.Response> loadDataFromNetwork() throws Exception {
        return getService().evaluationTags();

    }
}