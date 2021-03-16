package com.joker.qq.login;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;

import com.joker.connect.TdCallBack;
import com.joker.constant.TdErrorCode;
import com.joker.flag.TdFlags;
import com.joker.login.LoginModel;
import com.joker.model.BackResult;
import com.joker.model.ErrResult;
import com.joker.support.TdSdk;
import com.joker.support.listener.TdPerFormSuper;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import org.json.JSONObject;

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2017/5/2.
 */
@SuppressLint("unused")
public class QQLoginModel extends LoginModel implements TdPerFormSuper, IUiListener {

    private Tencent tencent;

    public Tencent getTencent() {
        if (tencent == null) {
            tencent = Tencent.createInstance(TdSdk.QQAppid(activity), activity);
        }
        return tencent;
    }

    public QQLoginModel(@TdFlags Integer type, Activity activity, TdCallBack callBack) {
        super(type, activity, callBack);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Tencent.onActivityResultData(requestCode, resultCode, data, this);
    }

    @Override
    public void perform() {
        Tencent tencent = getTencent();
        if (tencent.isSessionValid()) {
            tencent.logout(activity);
        }
        tencent.login(activity, "all", this);
    }

    @Override
    public void onComplete(Object o) {
        String json = o != null ? String.valueOf(o) : "";
        String openID = getValue(json, "openid");
        String token = getValue(json, "access_token");
        String expires_in = getValue(json, "expires_in");
        if (TextUtils.isEmpty(json)
                || TextUtils.isEmpty(openID)
                || TextUtils.isEmpty(token)
                || TextUtils.isEmpty(expires_in)) {
            callBack.onFailure(new ErrResult(type, TdErrorCode.DATA_IS_ERR, "登录失败，数据出错"));
        } else {
            tencent.setAccessToken(token, expires_in);
            tencent.setOpenId(openID);
            callBack.onSuccess(new BackResult(type, openID, token, tencent.getQQToken()));
        }
        unregisterLifecycle();
    }

    @Override
    public void onError(UiError uiError) {
        callBack.onFailure(new ErrResult(type, uiError.errorCode, uiError.errorMessage));
        unregisterLifecycle();
    }

    @Override
    public void onCancel() {
        callBack.onFailure(new ErrResult(type, TdErrorCode.USER_CANCEL_ERR, "登录失败，取消登录"));
        unregisterLifecycle();
    }


    private static String getValue(String result, String key) {
        String value = "";
        try {
            JSONObject json = new JSONObject(result);
            value = json.getString(key);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return value;
    }
}
