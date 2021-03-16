package com.net.network.chick.video;

import com.mei.orc.http.retrofit.RxRequest;
import com.mei.orc.john.model.Empty_data;
import com.net.ApiInterface;

import io.reactivex.Observable;

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2020/7/20
 */
public class ShortVideoReportRequest extends RxRequest<Empty_data.Response, ApiInterface> {

    private long time;
    private long duration;
    private String seqId;
    private String fromType;

    public ShortVideoReportRequest(long time, long duration, String seqId, String fromType) {
        super(Empty_data.Response.class, ApiInterface.class);
        this.time = time;
        this.duration = duration;
        this.seqId = seqId;
        this.fromType = fromType;
    }

    @Override
    protected String createCacheKey() {
        return toString();
    }

    @Override
    protected Observable<Empty_data.Response> loadDataFromNetwork() throws Exception {
        return getService().ShortVideoReport(time, duration, seqId, fromType);
    }
}
