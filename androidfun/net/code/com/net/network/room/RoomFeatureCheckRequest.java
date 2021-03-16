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
public class RoomFeatureCheckRequest extends RxRequest<Empty_data.Response, ApiInterface> {

    public String roomId;
    public String feature;//EXCLUSIVE_CHAT私聊 SPECIAL_SERVICE专属服务 ONLY_AUDIO_CHAT语音连线

    public RoomFeatureCheckRequest() {
        super(Empty_data.Response.class, ApiInterface.class);
    }

    @Override
    protected String createCacheKey() {
        return toString();
    }

    @Override
    protected Observable<Empty_data.Response> loadDataFromNetwork() throws Exception {
        return getService().roomFeatureCheck(roomId, feature);
    }
}
