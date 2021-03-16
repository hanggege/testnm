package com.net.network.chat;

import com.mei.orc.http.retrofit.RxRequest;
import com.mei.orc.john.model.Empty_data;
import com.net.ApiInterface;

import io.reactivex.Observable;

/**
 * @ProjectName: dove
 * @Package: com.net.network.chat
 * @ClassName: MessagePushReport
 * @Description:
 * @Author: zxj
 * @CreateDate: 2020/6/17 14:46
 * @UpdateUser:
 * @UpdateDate: 2020/6/17 14:46
 * @UpdateRemark:
 * @Version:
 */

public class GiftPushReport extends RxRequest<Empty_data.Response, ApiInterface> {
    String id;
    String giftld;


    public GiftPushReport(String id, String giftld) {
        super(Empty_data.Response.class, ApiInterface.class);
        this.id = id;
        this.giftld = giftld;

    }

    @Override
    protected String createCacheKey() {
        return toString();
    }

    @Override
    protected Observable<Empty_data.Response> loadDataFromNetwork() throws Exception {
        return getService().giftEvaluation(id, giftld);
    }
}