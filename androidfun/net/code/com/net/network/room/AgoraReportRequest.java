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
public class AgoraReportRequest extends RxRequest<Empty_data.Response, ApiInterface> {

    private String roomId;
    private int status;//111:用户将角色切换为知心达人。112:用户将角色切换为观众。113:上麦中掉线。114:上麦连接时掉线
    private int userId;

    public AgoraReportRequest(String roomId, int status, int userId) {
        super(Empty_data.Response.class, ApiInterface.class);
        this.roomId = roomId;
        this.status = status;
        this.userId = userId;
    }

    @Override
    protected String createCacheKey() {
        return toString();
    }

    @Override
    protected Observable<Empty_data.Response> loadDataFromNetwork() throws Exception {
        return getService().AgoraReport(roomId, userId, status);
    }
}
