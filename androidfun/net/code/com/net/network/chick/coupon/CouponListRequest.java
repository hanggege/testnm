package com.net.network.chick.coupon;

import com.mei.orc.http.retrofit.RxRequest;
import com.net.ApiInterface;
import com.net.model.chick.coupon.CouponListInfo;

import io.reactivex.Observable;

/**
 * CouponListRequest
 *
 * @author kun
 * @version 1.0.0
 * @since 2020-09-11
 */
public class CouponListRequest extends RxRequest<CouponListInfo.Response, ApiInterface> {

    private int couponId;
    private String couponNum;
    private int publisherId;

    public CouponListRequest(int couponId, String couponNum, int publisherId) {
        super(CouponListInfo.Response.class, ApiInterface.class);
        this.couponId = couponId;
        this.couponNum = couponNum;
        this.publisherId = publisherId;
    }

    @Override
    protected String createCacheKey() {
        return toString();
    }

    @Override
    protected Observable<CouponListInfo.Response> loadDataFromNetwork() throws Exception {
        return getService().getCouponList(couponId, couponNum, publisherId);
    }
}
