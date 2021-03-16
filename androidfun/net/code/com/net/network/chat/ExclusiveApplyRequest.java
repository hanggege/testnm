package com.net.network.chat;

import com.mei.orc.http.retrofit.RxRequest;
import com.net.ApiInterface;
import com.net.model.chat.ExclusiveApply;

import io.reactivex.Observable;

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2020/4/21
 */
public class ExclusiveApplyRequest extends RxRequest<ExclusiveApply.Response, ApiInterface> {
    public int targetUserId;
    public int ignorePreviousApply;//是否忽略自己的排麦申请
    public int forceApply;//是否直接申请排麦
    public int justCheckBalance;//是否只请求余额是否充足
    public String specialServiceOrderId;//专属服务id
    public String from = "";
    public int videoMode;
    public long couponNum; // 使用优惠券 但是不指定使用那一张则传-1, 具体使用那一张则传具体优惠券的编号. 0 表示不使用优惠券
    public int categoryId; //咨询方向，品类id

    public ExclusiveApplyRequest() {
        super(ExclusiveApply.Response.class, ApiInterface.class);
    }

    @Override
    protected String createCacheKey() {
        return toString();
    }

    @Override
    protected Observable<ExclusiveApply.Response> loadDataFromNetwork() throws Exception {
        return getService().exclusiveApply(targetUserId, justCheckBalance, ignorePreviousApply, forceApply, specialServiceOrderId, from, videoMode, couponNum, categoryId);
    }
}
