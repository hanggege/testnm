package com.net.network.room;

import com.mei.orc.http.retrofit.RxRequest;
import com.net.ApiInterface;
import com.net.model.room.DataStatistics;

import io.reactivex.Observable;

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2019-12-26
 */
public class DataStatisticsRequest extends RxRequest<DataStatistics.Response, ApiInterface> {

    private String roomId;
    private String broadcastId;

    public DataStatisticsRequest(String roomId, String broadcastId) {
        super(DataStatistics.Response.class, ApiInterface.class);
        this.roomId = roomId;
        this.broadcastId = broadcastId;
    }

    @Override
    protected String createCacheKey() {
        return toString();
    }

    @Override
    protected Observable<DataStatistics.Response> loadDataFromNetwork() throws Exception {
        return getService().dataStatistics(roomId, broadcastId);
    }
}
