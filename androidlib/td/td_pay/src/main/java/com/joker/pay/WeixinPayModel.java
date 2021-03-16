package com.joker.pay;

import android.app.Activity;
import android.content.Intent;

import com.joker.PayFlags;
import com.joker.connect.PayCallBack;
import com.joker.model.PayErrorCode;
import com.joker.model.PayFailure;
import com.joker.model.PaySuccess;
import com.joker.model.order.WxOrder;
import com.joker.support.TdSdk;
import com.joker.support.WeixinEvent;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.modelpay.PayResp;
import com.tencent.mm.opensdk.openapi.IWXAPI;

import java.util.Observable;
import java.util.Observer;

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2017/5/2.
 */

class WeixinPayModel extends PayModel implements Observer {
    private IWXAPI api;
    private WxOrder wxOrder;

    private IWXAPI getApi(Activity activity, String appID) {
        if (api == null) {
            api = TdSdk.getWeixinAPI(activity, appID);
        }
        return api;
    }

    WeixinPayModel(PayCallBack callBack, Activity activity, @PayFlags int type, WxOrder orderInfo) {
        super(callBack, activity, type);
        this.wxOrder = orderInfo;
        WeixinEvent.getInstance().addObserver(this);
    }

    @Override
    public void perform() {
        if (!getApi(activity, wxOrder.wx_app_id).isWXAppInstalled()) {
            callBack.onFailure(new PayFailure(type, wxOrder.orderSn, PayErrorCode.NOT_INSTANLLED_ERR, "支付失败", "没有安装微信，请先安装微信"));
        } else {
            PayReq req = new PayReq();
            req.appId = wxOrder.wx_app_id;
            req.partnerId = wxOrder.partnerId;
            req.prepayId = wxOrder.prepayId;
            req.packageValue = wxOrder.packageValue;
            req.nonceStr = wxOrder.nonceStr;
            req.timeStamp = wxOrder.timeStamp;
            req.sign = wxOrder.sign;
            api.sendReq(req);
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

    }

    @Override
    public void update(Observable o, Object result) {
        if (o != null && o instanceof WeixinEvent && result != null && result instanceof PayResp) {
            PayResp resp = (PayResp) result;
            if (resp.errCode == BaseResp.ErrCode.ERR_OK) {
                callBack.onSuccess(new PaySuccess(type, wxOrder.orderSn));
            } else {
                switch (resp.errCode) {
                    case BaseResp.ErrCode.ERR_USER_CANCEL:
                        callBack.onFailure(new PayFailure(type, wxOrder.orderSn, PayErrorCode.Pay_User_Cancel_code, "支付失败,请重试", "取消支付"));
                        break;
                    case BaseResp.ErrCode.ERR_BAN:
                    default:
                        callBack.onFailure(new PayFailure(type, wxOrder.orderSn, PayErrorCode.pay_failure_code, "支付失败,请重试", "支付失败"));
                        break;
                }
            }
        } else {
            callBack.onFailure(new PayFailure(type, wxOrder.orderSn, PayErrorCode.BACK_OF_ERR, "微信未返回", "回调未获得正确信息"));
        }
        unregisterLifecycle();
        WeixinEvent.getInstance().deleteObserver(this);
    }
}
