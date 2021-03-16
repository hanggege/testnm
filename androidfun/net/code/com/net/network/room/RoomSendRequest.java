package com.net.network.room;

import com.mei.orc.http.retrofit.RxRequest;
import com.mei.orc.john.model.Empty_data;
import com.net.ApiInterface;

import io.reactivex.Observable;

/**
 * 直播间发送消息
 */
public class RoomSendRequest extends RxRequest<Empty_data.Response, ApiInterface> {

    public String type;//SEND_TEXT: 消息,PUSH_WECHAT: 交换微信, SEND_GIFT: 礼物
    public String roomId;//房间id
    public int to;//目标id
    public String at;//聊天at的人的ids,逗号分割
    public int count;//礼物数量
    public String msg;//消息内容,礼物id

    public RoomSendRequest() {
        super(Empty_data.Response.class, ApiInterface.class);
    }

    @Override
    protected String createCacheKey() {
        return toString();
    }

    @Override
    protected Observable<Empty_data.Response> loadDataFromNetwork() throws Exception {
        return getService().roomSend(type, roomId, to, at, count, msg);
    }
}