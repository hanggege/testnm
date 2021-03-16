package com.net.network.radio;

import com.mei.orc.http.retrofit.RxRequest;
import com.mei.orc.john.model.Empty_data;
import com.net.ApiInterface;

import io.reactivex.Observable;

/**
 * RadioScheduleReportRequest
 *
 * @author kun
 * @version 1.0.0
 * @since 2020-09-24
 */
public class RadioScheduleReportRequest extends RxRequest<Empty_data.Response, ApiInterface> {
    private String id;

    public RadioScheduleReportRequest(String id) {
        super(Empty_data.Response.class, ApiInterface.class);
        this.id = id;
    }

    @Override
    protected String createCacheKey() {
        return toString();
    }

    @Override
    protected Observable<Empty_data.Response> loadDataFromNetwork() throws Exception {
        return getService().reportRadioSchedule(id);
    }
}
