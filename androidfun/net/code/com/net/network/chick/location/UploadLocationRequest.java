package com.net.network.chick.location;

import com.mei.orc.http.retrofit.RxRequest;
import com.mei.orc.john.model.Empty_data;
import com.net.ApiInterface;

import io.reactivex.Observable;

/**
 * Created by zzw on 2019-12-11
 * Des:
 */
public class UploadLocationRequest extends RxRequest<Empty_data.Response, ApiInterface> {

    public String location;
    public String locationCity;
    public double longitude;
    public double latitude;

    public UploadLocationRequest(String location, String locationCity, double longitude, double latitude) {
        super(Empty_data.Response.class, ApiInterface.class);
        this.location = location;
        this.locationCity = locationCity;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    @Override
    protected String createCacheKey() {
        return toString();
    }

    @Override
    protected Observable<Empty_data.Response> loadDataFromNetwork() throws Exception {
        return getService().uploadLocation(location, locationCity, longitude, latitude);

    }
}