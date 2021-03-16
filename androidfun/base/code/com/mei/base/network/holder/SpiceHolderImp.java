package com.mei.base.network.holder;

import com.mei.base.network.ApiClient;
import com.mei.orc.http.retrofit.RetrofitClient;
import com.mei.orc.http.retrofit.RxRetrofitManager;
import com.mei.wood.BuildConfig;

/**
 * Created by guoyufeng on 14/5/15.
 */
public class SpiceHolderImp implements SpiceHolder {
    private RxRetrofitManager retrofitManager;

    public SpiceHolderImp() {
        retrofitManager = new RxRetrofitManager();
    }


    @Override
    public RetrofitClient getApiSpiceMgr() {
        return retrofitManager.getRetrofitClient(ApiClient.class).setLogVisible(BuildConfig.IS_TEST).create();
    }

    @Override
    public void recycleManager() {
        try {
            retrofitManager.recycleManager();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
