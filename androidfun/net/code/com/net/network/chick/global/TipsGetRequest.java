package com.net.network.chick.global;

import com.mei.orc.http.retrofit.RxRequest;
import com.net.ApiInterface;
import com.net.model.chick.global.TipsGet;

import io.reactivex.Observable;

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2020/8/4
 */
public class TipsGetRequest extends RxRequest<TipsGet.Response, ApiInterface> {

    private String nextStartKey;
    private boolean unable;
    private int count;
    private String tabId;
    private boolean showSystemInvite;

    public TipsGetRequest(String nextStartKey, boolean unable, int count, String tabId, boolean showSystemInvite) {
        super(TipsGet.Response.class, ApiInterface.class);
        this.nextStartKey = nextStartKey;
        this.unable = unable;
        this.count = count;
        this.tabId = tabId;
        this.showSystemInvite = showSystemInvite;
    }

    @Override
    protected String createCacheKey() {
        return toString();
    }

    @Override
    protected Observable<TipsGet.Response> loadDataFromNetwork() throws Exception {
        return getService().TipsGet(nextStartKey, unable, count, tabId, showSystemInvite);
    }
}
