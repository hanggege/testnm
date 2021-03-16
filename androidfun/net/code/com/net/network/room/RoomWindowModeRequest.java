package com.net.network.room;

import com.mei.orc.http.retrofit.RxRequest;
import com.mei.orc.john.model.Empty_data;
import com.net.ApiInterface;

import io.reactivex.Observable;

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2019-12-26
 * 房间窗口mode  0:对等大小 1:知心达人大用户小 2:用户小知心达人大
 */
public class RoomWindowModeRequest extends RxRequest<Empty_data.Response, ApiInterface> {

    private String roomId;
    private int mode;//111:用户将角色切换为知心达人。112:用户将角色切换为观众。

    public RoomWindowModeRequest(String roomId, int mode) {
        super(Empty_data.Response.class, ApiInterface.class);
        this.roomId = roomId;
        this.mode = mode;
    }

    @Override
    protected String createCacheKey() {
        return toString();
    }

    @Override
    protected Observable<Empty_data.Response> loadDataFromNetwork() throws Exception {
        return getService().roomMode(roomId, mode);
    }
}
