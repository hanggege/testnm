package com.net.network.tim;


import android.app.Activity;

import com.mei.orc.http.retrofit.RxRequest;
import com.mei.wood.ui.TabMainActivity;
import com.mei.wood.util.AppManager;
import com.net.ApiInterface;
import com.net.model.chick.tim.SystemInviteResult;

import io.reactivex.Observable;


public class SystemInviteRequest extends RxRequest<SystemInviteResult.Response, ApiInterface> {

    private long count;

    public SystemInviteRequest(long count) {
        super(SystemInviteResult.Response.class, ApiInterface.class);
        this.count = count;
    }

    @Override
    protected String createCacheKey() {
        return toString();
    }

    @Override
    protected Observable<SystemInviteResult.Response> loadDataFromNetwork() throws Exception {
        Activity activity = AppManager.getInstance().currentActivity();
        String currActivityName = "";
        if (activity instanceof TabMainActivity) {
            int index = ((TabMainActivity) activity).getSelectedIndex();
            currActivityName = TabMainActivity.class.getSimpleName() + index;
        } else {
            currActivityName = activity.getClass().getSimpleName();
        }
        return getService().invite(count, currActivityName);
    }
}
