package com.joker.login;

import android.app.Activity;
import android.util.Log;

import androidx.annotation.NonNull;

import com.joker.TdType;
import com.joker.connect.TdCallBack;
import com.joker.connect.TdConstants;
import com.joker.flag.TdFlags;
import com.joker.support.listener.TdNetAdapter;
import com.joker.support.listener.TdPerFormSuper;
import com.joker.support.proxy.TransitionFactory;

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2017/4/27.
 */

public class TdLoginHelper {
    private Activity activity;
    private TdNetAdapter netAdapter;


    public TdLoginHelper(@NonNull Activity activity) {
        this.activity = activity;
    }

    public TdLoginHelper(Activity activity, TdNetAdapter netAdapter) {
        this.activity = activity;
        this.netAdapter = netAdapter;
    }

    public synchronized boolean performLogin(@TdFlags int type, @NonNull TdCallBack callBack) {
        if (activity == null) {//未初始化
            Log.e("TdLoginHelper", "TdLoginHelper未初始化");
            return false;
        }
        TdPerFormSuper loginSuper = null;
        switch (type) {
            case TdType.weibo:
                TransitionFactory.RfParams wbParams = new TransitionFactory.RfParams();
                wbParams.append(Integer.class, type);
                wbParams.append(Activity.class, activity);
                wbParams.append(TdCallBack.class, callBack);
                loginSuper = TransitionFactory.create(TdConstants.WEIBO_LOGIN_MODEL, wbParams);
//                loginSuper = new WeiboLoginModel(type, activity, callBack);
                break;
            case TdType.weixin:
                loginSuper = new WeixinLoginModel(type, activity, callBack, netAdapter);
                break;
            case TdType.qq:
                TransitionFactory.RfParams qqParams = new TransitionFactory.RfParams();
                qqParams.append(Integer.class, type);
                qqParams.append(Activity.class, activity);
                qqParams.append(TdCallBack.class, callBack);
                loginSuper = TransitionFactory.create(TdConstants.QQ_LOGIN_MODEL, qqParams);
                break;
            default:
                break;
        }
        if (loginSuper == null) {//未支持到
            Log.e("TdLoginHelper", "TdLoginHelper未支持到");
            return false;
        }
        loginSuper.perform();
        return true;
    }

}
