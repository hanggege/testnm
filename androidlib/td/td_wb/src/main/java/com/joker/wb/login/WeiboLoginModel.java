package com.joker.wb.login;

import android.app.Activity;
import android.content.Intent;

import androidx.annotation.NonNull;

import com.joker.connect.TdCallBack;
import com.joker.constant.TdErrorCode;
import com.joker.flag.TdFlags;
import com.joker.login.LoginModel;
import com.joker.model.BackResult;
import com.joker.model.ErrResult;
import com.joker.support.listener.TdPerFormSuper;
import com.joker.wb.WeiboManager;
import com.sina.weibo.sdk.auth.AccessTokenKeeper;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WbAuthListener;
import com.sina.weibo.sdk.auth.WbConnectErrorMessage;
import com.sina.weibo.sdk.auth.sso.SsoHandler;

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2017/4/27.
 */

@SuppressWarnings("unused")
class WeiboLoginModel extends LoginModel implements TdPerFormSuper, WbAuthListener {

    private SsoHandler mSsoHandler;

    private SsoHandler getSsoHandler(Activity activity) {
        if (mSsoHandler == null) {
            mSsoHandler = new SsoHandler(activity);
        }
        return mSsoHandler;
    }

    public WeiboLoginModel(@TdFlags Integer type, Activity activity, @NonNull TdCallBack callBack) {
        super(type, activity, callBack);
        WeiboManager.weiboInit(activity);
    }

    @Override
    public void perform() {
        getSsoHandler(activity).authorize(this);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (mSsoHandler != null) mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
    }

    @Override
    public void onSuccess(Oauth2AccessToken mAccessToken) {
        if (mAccessToken.isSessionValid()) {
            AccessTokenKeeper.writeAccessToken(activity, mAccessToken);
            callBack.onSuccess(new BackResult(type, mAccessToken.getUid(), mAccessToken.getToken()));
        } else {
            callBack.onFailure(new ErrResult(type, TdErrorCode.DATA_IS_ERR, "登录失败，数据出错"));
        }
        unregisterLifecycle();
    }

    @Override
    public void cancel() {
        callBack.onFailure(new ErrResult(type, TdErrorCode.USER_CANCEL_ERR, "登录失败，取消登录"));
        unregisterLifecycle();
    }

    @Override
    public void onFailure(WbConnectErrorMessage err) {
        if (err == null) {
            callBack.onFailure(new ErrResult(type, TdErrorCode.DATA_IS_ERR, "登录失败，微博授权失败"));
        } else {
            int code;
            try {
                code = Integer.parseInt(err.getErrorCode());
            } catch (Exception e) {
                code = TdErrorCode.NONE_CODE_ERR;
                e.printStackTrace();
            }
            callBack.onFailure(new ErrResult(type, code, err.getErrorMessage()));
        }
        unregisterLifecycle();
    }

}
