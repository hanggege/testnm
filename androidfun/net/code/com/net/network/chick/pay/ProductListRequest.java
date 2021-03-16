package com.net.network.chick.pay;


import com.mei.orc.http.retrofit.RxRequest;
import com.net.ApiInterface;
import com.net.model.chick.pay.ProductListResult;

import io.reactivex.Observable;


public class ProductListRequest extends RxRequest<ProductListResult.Response, ApiInterface> {

    public int showAll;
    public int showTask = 0;
    public int textType = 0;

    public ProductListRequest() {
        super(ProductListResult.Response.class, ApiInterface.class);
    }

    @Override
    protected String createCacheKey() {
        return toString();
    }

    @Override
    protected Observable<ProductListResult.Response> loadDataFromNetwork() throws Exception {
        return getService().productList(showAll, showTask, textType);

    }
}