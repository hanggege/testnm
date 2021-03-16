package com.net.network.chick.pay;

import com.joker.PayFlags;
import com.mei.base.util.td.TdHelperKt;
import com.mei.dialog.PayFromType;
import com.mei.orc.http.retrofit.RxRequest;
import com.mei.provider.ProjectExt;
import com.net.ApiInterface;
import com.net.model.chick.pay.OrderCreateResult;

import io.reactivex.Observable;

/**
 * @author Created by lenna on 2020/11/17
 */
public class NewPeopleGiftBagOrderCreateRequest extends RxRequest<OrderCreateResult.Response, ApiInterface> {
    private String bagId;
    private String payType;
    private String userAgent;
    private PayFromType fromType;

    public NewPeopleGiftBagOrderCreateRequest(String bagId,
                                              PayFromType fromType,
                                              @PayFlags int payType) {
        super(OrderCreateResult.Response.class, ApiInterface.class);
        this.bagId = bagId;
        this.fromType = fromType;
        this.payType = TdHelperKt.parsePayType(payType);
        this.userAgent = ProjectExt.FullUserAgent;
    }

    @Override
    protected String createCacheKey() {
        return toString();
    }

    @Override
    protected Observable<OrderCreateResult.Response> loadDataFromNetwork() throws Exception {
        return getService().createNewPeopleGiftBagOrder(bagId, fromType.name(), payType, userAgent);
    }
}
