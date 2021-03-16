package com.joker.connect;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.joker.support.TdSdk;
import com.tencent.mm.opensdk.openapi.IWXAPI;

public class AppRegister extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        final IWXAPI api = TdSdk.getWeixinAPI(context, TdSdk.WxAppid(context));
    }
}
