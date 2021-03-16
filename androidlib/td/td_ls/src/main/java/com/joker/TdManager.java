package com.joker;

import android.app.Activity;
import android.content.Context;

import androidx.annotation.NonNull;

import com.joker.connect.TdCallBack;
import com.joker.flag.TdFlags;
import com.joker.login.TdLoginHelper;
import com.joker.model.BackResult;
import com.joker.model.ShareInfo;
import com.joker.model.TdUserInfo;
import com.joker.model.WeixinToken;
import com.joker.share.TdShareHelper;
import com.joker.support.TdAction;
import com.joker.support.TdRequest;
import com.joker.support.TdSdk;
import com.joker.support.listener.TdImageAdapter;
import com.joker.support.listener.TdNetAdapter;

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2017/4/28.
 */

public class TdManager {

    /**
     * 调起登录
     */
    public static boolean performLogin(Activity activity, @TdFlags int type, @NonNull TdCallBack callBack) {
        TdLoginHelper holder = new TdLoginHelper(activity);
        return holder.performLogin(type, callBack);
    }

    /**
     * 调起分享,可自定义网络请求
     */
    public static boolean performLogin(Activity activity, @TdFlags int type, TdNetAdapter adapter, @NonNull TdCallBack callBack) {
        TdLoginHelper holder = new TdLoginHelper(activity, adapter);
        return holder.performLogin(type, callBack);
    }

    /**
     * 调起分享
     */
    public static boolean performShare(Activity activity, @TdFlags int type, @NonNull ShareInfo info, @NonNull TdCallBack callBack) {
        TdShareHelper holder = new TdShareHelper(activity);
        return holder.performShare(type, info, callBack);
    }

    /**
     * 调起分享,可自定义取网络图片
     */
    public static boolean performShare(Activity activity, @TdFlags int type, @NonNull ShareInfo info, TdImageAdapter adapter, @NonNull TdCallBack callBack) {
        TdShareHelper holder = new TdShareHelper(activity, adapter);
        return holder.performShare(type, info, callBack);
    }

    /**
     * 获取第三方用户信息
     * 如果需要QQ则使用{ com.joker.qq.TdManagerImpl}
     */
    public static void getUserInfo(Context context, @NonNull BackResult result, TdAction<TdUserInfo> callback) {
//        TdUserApi.getUserInfo(context, result, null, callback);
    }

    public static void getUserInfo(Context context, @NonNull BackResult result, TdNetAdapter netAdapter, TdAction<TdUserInfo> callback) {
//        TdUserApi.getUserInfo(context, result, netAdapter, callback);
    }

    /**
     * 获取weixinToken 用于库内部
     */
    public static void getWeixinToken(Context context, String wxCode, TdNetAdapter netAdapter, final TdAction<WeixinToken> callBack) {
        final String url = "https://api.weixin.qq.com/sns/oauth2/access_token?grant_type=authorization_code" +
                "&appid=" + TdSdk.WxAppid(context) + "&secret=" + TdSdk.WxSecret(context) +
                "&code=" + wxCode;
        TdAction<String> action = new TdAction<String>() {
            @Override
            public void onCallback(String json) {
                callBack.onCallback(WeixinToken.parseJsonObject(json));
            }
        };
        if (netAdapter != null) {
            netAdapter.requestNet(url, action);
        } else {
            TdRequest.requestNet(url, action);
        }
    }


}
