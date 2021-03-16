package com.net.network.gift;

import com.mei.orc.http.retrofit.RxRequest;
import com.net.ApiInterface;
import com.net.model.gift.SendGiftBalance;

import io.reactivex.Observable;

/**
 * 直播间发送消息
 */
public class SendGiftRequest extends RxRequest<SendGiftBalance.Response, ApiInterface> {

    public int giftId;// 礼物id
    public int number;//礼物数量
    public int toUserId;//送礼对象
    public String fromScene;//来源场景
    public String referId;//房间号
    public String fromType;

    public SendGiftRequest() {
        super(SendGiftBalance.Response.class, ApiInterface.class);
    }

    @Override
    protected String createCacheKey() {
        return toString();
    }

    @Override
    protected Observable<SendGiftBalance.Response> loadDataFromNetwork() throws Exception {
        return getService().giftSend(giftId, number, toUserId, fromScene, referId, fromType);
    }
}