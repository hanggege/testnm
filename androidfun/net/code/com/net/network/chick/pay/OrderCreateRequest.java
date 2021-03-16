package com.net.network.chick.pay;

import com.joker.PayFlags;
import com.mei.base.util.td.TdHelperKt;
import com.mei.dialog.PayFromType;
import com.mei.orc.http.retrofit.RxRequest;
import com.mei.provider.ProjectExt;
import com.net.ApiInterface;
import com.net.model.chick.pay.OrderCreateResult;

import io.reactivex.Observable;


public class OrderCreateRequest extends RxRequest<OrderCreateResult.Response, ApiInterface> {

    private String productId;
    private String payType;
    private String userAgent;
    private PayFromType fromType;
    private String roomId;


    public OrderCreateRequest(
            String productId,
            PayFromType fromType,
            @PayFlags int payType, String roomId) {
        super(OrderCreateResult.Response.class, ApiInterface.class);
        this.productId = productId;
        this.fromType = fromType;
        this.payType = TdHelperKt.parsePayType(payType);
        this.userAgent = ProjectExt.FullUserAgent;
        this.roomId = roomId;
    }


    @Override
    protected String createCacheKey() {
        return toString();
    }

    @Override
    protected Observable<OrderCreateResult.Response> loadDataFromNetwork() throws Exception {
        return getService().orderCreate(productId, fromType.name(), payType, userAgent, roomId);
    }


}
