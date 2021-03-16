package com.net.network.room;

import com.mei.orc.http.retrofit.RxRequest;
import com.net.ApiInterface;
import com.net.model.room.ProductCategory;

import io.reactivex.Observable;

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2019-12-26
 */
public class NewProductCategoryRequest extends RxRequest<ProductCategory.Response, ApiInterface> {


    public NewProductCategoryRequest() {
        super(ProductCategory.Response.class, ApiInterface.class);
    }

    @Override
    protected String createCacheKey() {
        return toString();
    }

    @Override
    protected Observable<ProductCategory.Response> loadDataFromNetwork() throws Exception {
        return getService().newProductCategory();
    }
}
