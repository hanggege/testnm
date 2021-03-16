package com.net.network.room;

import com.mei.orc.http.retrofit.RxRequest;
import com.net.ApiInterface;
import com.net.model.room.ContributionsInfo;

import io.reactivex.Observable;

/**
 * Created by zhanyinghui on 2020/4/13
 * Des:
 */
public class ContributeListRequest extends RxRequest<ContributionsInfo.Response, ApiInterface> {
    public String publisherId;
    public boolean isDaily;
    public int pageNo;

    public ContributeListRequest() {
        super(ContributionsInfo.Response.class, ApiInterface.class);
    }


    /****
     * 切换是单日榜单数据还是总榜单数据
     * @param isDaily
     * @return
     */
    public ContributeListRequest updateParam(boolean isDaily, int pageNo) {
        this.isDaily = isDaily;
        this.pageNo = pageNo;
        return this;
    }


    @Override
    protected String createCacheKey() {
        return toString();
    }

    @Override
    protected Observable<ContributionsInfo.Response> loadDataFromNetwork() throws Exception {
        return getService().getContributionList(publisherId, isDaily, pageNo);
    }


}
