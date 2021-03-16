package com.net.network.newhome;

import androidx.annotation.NonNull;

import com.mei.orc.http.retrofit.RxRequest;
import com.mei.provider.ProjectExt;
import com.net.ApiInterface;
import com.net.model.newhome.Tab_content_v4;

import io.reactivex.Observable;

/**
 * Created by hang on 2018/10/29.
 * 安卓首页推荐A版网络请求
 * 首页数据统一改为V5接口
 */
public class Tab_content_v4_request extends RxRequest<Tab_content_v4.Response, ApiInterface> {

    private String module_id;
    private int pro_cate_id;


    public Tab_content_v4_request(int pro_cate_id, String module_id) {
        super(Tab_content_v4.Response.class, ApiInterface.class);
        this.pro_cate_id = pro_cate_id;
        this.module_id = module_id;
    }

    @Override
    protected String createCacheKey() {
        return toString();
    }

    @Override
    protected Observable<Tab_content_v4.Response> loadDataFromNetwork() throws Exception {
        return getService().Front_Page_V6_List_b(pro_cate_id, module_id, ProjectExt.FullUserAgent);
    }

    @NonNull
    @Override
    public String toString() {
        return "Tab_content_v4_request={module_id" + module_id + "}";
    }
}
