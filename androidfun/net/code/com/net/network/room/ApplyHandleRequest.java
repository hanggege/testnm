package com.net.network.room;

import com.mei.orc.http.retrofit.RxRequest;
import com.net.ApiInterface;
import com.net.model.room.QueueMy;
import com.net.model.room.RoomApplyType;

import io.reactivex.Observable;

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2019-12-13
 */
public class ApplyHandleRequest extends RxRequest<QueueMy.Response, ApiInterface> {

    public int userId;
    public String roomId;
    public RoomApplyType type = RoomApplyType.UPSTREAM;
    public int result;//0:拒绝 1:同意 2: 超时 3: 取消充值 4:充值失败
    public int videoMode;

    public ApplyHandleRequest() {
        super(QueueMy.Response.class, ApiInterface.class);
    }

    @Override
    protected String createCacheKey() {
        return toString();
    }

    @Override
    protected Observable<QueueMy.Response> loadDataFromNetwork() throws Exception {
        return getService().ApplyHandle(userId, roomId, type.name(), result, videoMode);
    }
}
