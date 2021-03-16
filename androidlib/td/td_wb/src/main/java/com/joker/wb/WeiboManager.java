package com.joker.wb;

import android.content.Context;

import com.joker.support.TdSdk;
import com.sina.weibo.sdk.WbSdk;
import com.sina.weibo.sdk.auth.AuthInfo;

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2018/11/26
 */
public class WeiboManager {

    public static void weiboInit(Context context) {
        WbSdk.install(context, new AuthInfo(context,
                TdSdk.WbAppKey(context), TdSdk.WbRedUrl(context), TdSdk.WbScope(context)));
    }
}
