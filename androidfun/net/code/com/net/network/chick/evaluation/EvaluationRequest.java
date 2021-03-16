package com.net.network.chick.evaluation;


import com.mei.orc.http.retrofit.RxRequest;
import com.net.ApiInterface;
import com.net.model.chick.evaluation.EvaluationAfterBean;
import com.net.model.chick.evaluation.EvaluationBean;

import io.reactivex.Observable;


public class EvaluationRequest extends RxRequest<EvaluationAfterBean.Response, ApiInterface> {

    private EvaluationBean evaluationBean = new EvaluationBean();

    public EvaluationRequest(EvaluationBean evaluationBean) {
        super(EvaluationAfterBean.Response.class, ApiInterface.class);
        this.evaluationBean = evaluationBean;
    }

    @Override
    protected String createCacheKey() {
        return toString();
    }

    @Override
    protected Observable<EvaluationAfterBean.Response> loadDataFromNetwork() throws Exception {
        return getService().evaluation(evaluationBean);

    }
}