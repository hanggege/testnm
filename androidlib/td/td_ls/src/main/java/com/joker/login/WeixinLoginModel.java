package com.joker.login;

import android.app.Activity;
import android.content.Intent;

import com.joker.connect.TdCallBack;
import com.joker.constant.TdErrorCode;
import com.joker.flag.TdFlags;
import com.joker.model.BackResult;
import com.joker.model.ErrResult;
import com.joker.support.TdSdk;
import com.joker.support.WeixinEvent;
import com.joker.support.listener.TdNetAdapter;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;

import java.util.Observable;
import java.util.Observer;

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2017/4/28.
 */

class WeixinLoginModel extends LoginModel implements Observer {
    private IWXAPI api;
    private TdNetAdapter netAdapter;

    private IWXAPI getApi(Activity activity) {
        if (api == null) {
            api = TdSdk.getWeixinAPI(activity, TdSdk.WxAppid(activity));
        }
        return api;
    }

    WeixinLoginModel(@TdFlags Integer type, Activity activity, TdCallBack callBack, TdNetAdapter adapter) {
        super(type, activity, callBack);
        this.netAdapter = adapter;
        WeixinEvent.getInstance().addObserver(this);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

    }

    @Override
    public void perform() {
        if (!getApi(activity).isWXAppInstalled()) {
            callBack.onFailure(new ErrResult(type, TdErrorCode.NOT_INSTANLLED_ERR, "没有安装微信，请先安装微信"));
        } else {
            SendAuth.Req req = new SendAuth.Req();
            req.scope = "snsapi_userinfo";
            req.state = "weixin_login_model";//
            api.sendReq(req);
        }
    }

    @Override
    public void update(Observable o, Object result) {//回调
        if (o != null && o instanceof WeixinEvent && result != null && result instanceof SendAuth.Resp) {
            SendAuth.Resp info = (SendAuth.Resp) result;
            if (info.errCode == BaseResp.ErrCode.ERR_OK) {
                callBack.onSuccess(new BackResult(type, info.code));
//                TdManager.getWeixinToken(activity, info.code,netAdapter, new TdAction<WeixinToken>() {
//                    @Override
//                    public void onCallback(WeixinToken token) {
//                        if (token != null) {
//                            callBack.onSuccess(new BackResult(type, token.openid, token.refresh_token,token.access_token, token.unionid));
//                        } else {
//                            callBack.onFailure(new ErrResult(type, TdErrorCode.DATA_IS_ERR, "登录失败，数据出错"));
//                        }
//                    }
//                });
            } else {
                int code = info.errCode;
                String msg;
                switch (info.errCode) {
                    case BaseResp.ErrCode.ERR_USER_CANCEL:
                        msg = "登录失败，取消登录";
                        code = TdErrorCode.USER_CANCEL_ERR;
                        break;
                    case BaseResp.ErrCode.ERR_AUTH_DENIED:
                        msg = "发送被拒绝";
                        break;
                    case BaseResp.ErrCode.ERR_UNSUPPORT:
                        msg = "不支持错误";
                        break;
                    default:
                        msg = "未知错误";
                        code = TdErrorCode.NONE_CODE_ERR;
                        break;
                }
                callBack.onFailure(new ErrResult(type, code, msg));
            }
        } else {
            callBack.onFailure(new ErrResult(type, TdErrorCode.NONE_CODE_ERR, "登录失败"));
        }
        unregisterLifecycle();
        WeixinEvent.getInstance().deleteObserver(this);
    }
}
