package com.net.network.room;

import com.mei.orc.http.retrofit.RxRequest;
import com.mei.orc.john.model.Empty_data;
import com.net.ApiInterface;

import io.reactivex.Observable;

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2019-12-26
 */
public class UserStreamReportRequest extends RxRequest<Empty_data.Response, ApiInterface> {

    public String roomId;
    public int mode;//0:音频流+视频流1:音频流2:视频流
    public int status;//0:关闭1:打开

    public UserStreamReportRequest() {
        super(Empty_data.Response.class, ApiInterface.class);
    }

    @Override
    protected String createCacheKey() {
        return toString();
    }

    @Override
    protected Observable<Empty_data.Response> loadDataFromNetwork() throws Exception {
        return getService().userStreamReport(roomId,mode,status);
    }
}
