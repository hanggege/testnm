package com.net.network.works;

import com.mei.orc.http.retrofit.RxRequest;
import com.mei.orc.john.model.Empty_data;
import com.net.ApiInterface;

import java.util.List;

import io.reactivex.Observable;

/**
 * Created by steven on 15/4/25.
 */
public class AddUserResourcesListRequest extends RxRequest<Empty_data.Response, ApiInterface> {

    ResourcesRequest mResourcesReuqest;

    public AddUserResourcesListRequest(ResourcesRequest mResourcesReuqest) {
        super(Empty_data.Response.class, ApiInterface.class);
        this.mResourcesReuqest = mResourcesReuqest;
    }


    @Override
    protected String createCacheKey() {
        return toString();
    }

    @Override
    protected Observable<Empty_data.Response> loadDataFromNetwork() throws Exception {
        return getService().addResourcesList(mResourcesReuqest);
    }

    public static class ResourcesRequest {
        public List<ResourcesData> works;

        public ResourcesRequest(List<ResourcesData> works) {
            this.works = works;
        }

        public static class ResourcesData {
            public String url;
            public String coverUrl;
            public String worksType;

        }

    }
}
