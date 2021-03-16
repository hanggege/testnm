package com.net.network.room;

import com.mei.orc.http.retrofit.RxRequest;
import com.net.ApiInterface;
import com.net.model.room.RoomApplyLaunch;
import com.net.model.room.RoomApplyType;
import com.net.model.room.RoomType;

import io.reactivex.Observable;

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2019-12-13
 */
public class RoomApplyLaunchRequest extends RxRequest<RoomApplyLaunch.Response, ApiInterface> {

    public int userId;
    public String roomId;
    public RoomApplyType type = RoomApplyType.UPSTREAM;
    public RoomType room_type = RoomType.COMMON;
    public String from = "";
    public int couponNum;
    public int videoMode;//视频模式 0: 兼容旧版,无效值, 1: 仅音频, 2: 音频 + 视频

    public RoomApplyLaunchRequest() {
        super(RoomApplyLaunch.Response.class, ApiInterface.class);
    }

    @Override
    protected String createCacheKey() {
        return toString();
    }

    @Override
    protected Observable<RoomApplyLaunch.Response> loadDataFromNetwork() throws Exception {
        return getService().roomApplyLaunch(userId, roomId, type.name(), room_type.name(), from, videoMode,couponNum);
    }
}
