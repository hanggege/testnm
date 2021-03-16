package com.mei.base.network.holder;

import com.mei.orc.http.retrofit.RetrofitClient;

/**
 * Created by guoyufeng on 14/5/15.
 */
public interface SpiceHolder {

    RetrofitClient getApiSpiceMgr();

    void recycleManager();
}
