package com.net.network.radio;

import com.mei.orc.http.retrofit.RxRequest;
import com.mei.orc.john.model.Empty_data;
import com.net.ApiInterface;

import io.reactivex.Observable;

/**
 * @author Created by lenna on 2020/9/24
 * 电台音频播放上报
 */
public class RadioReportRequest extends RxRequest<Empty_data.Response, ApiInterface> {
    private int channelId;
    private int audioId;
    private long playTime;
    private boolean completed;

    public RadioReportRequest(int channelId, int audioId, long playTime, boolean completed) {
        super(Empty_data.Response.class, ApiInterface.class);
        this.channelId = channelId;
        this.audioId = audioId;
        this.playTime = playTime;
        this.completed = completed;
    }

    @Override
    protected String createCacheKey() {
        return toString();
    }

    @Override
    protected Observable<Empty_data.Response> loadDataFromNetwork() throws Exception {
        return getService().reportRadio(channelId, audioId, playTime, completed);
    }
}
