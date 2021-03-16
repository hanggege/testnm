package com.joker.pay;

import android.app.Activity;
import android.util.Log;

import androidx.annotation.NonNull;

import com.joker.PayFlags;
import com.joker.PayType;
import com.joker.connect.PalManager;
import com.joker.connect.PayCallBack;
import com.joker.model.order.AliOrder;
import com.joker.model.order.Order;
import com.joker.model.order.PalOrder;
import com.joker.model.order.WxOrder;
import com.joker.support.listener.TdPerFormSuper;
import com.joker.support.proxy.TransitionFactory;

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2017/5/2.
 */

public class PayHelper {
    private Activity activity;

    public PayHelper(Activity activity) {
        this.activity = activity;
    }

    public synchronized boolean performPay(@PayFlags int type, @NonNull Order info, @NonNull PayCallBack callBack) {
        if (activity == null) {//未初始化
            Log.e("TdLoginHelper", "TdLoginHelper未初始化");
            return false;
        }
        TdPerFormSuper payHelper = null;
        switch (type) {
            case PayType.weixin:
                if (info instanceof WxOrder) {
                    payHelper = new WeixinPayModel(callBack, activity, type, (WxOrder) info);
                }
                break;
            case PayType.alipay:
                if (info instanceof AliOrder) {
                    payHelper = new AliPayModel(callBack, activity, type, (AliOrder) info);
                }
                break;
            case PayType.paypal:
                if (info instanceof PalOrder) {
                    TransitionFactory.RfParams params = new TransitionFactory.RfParams();
                    params.append(Activity.class, activity);
                    params.append(Integer.class, type);
                    params.append(PalOrder.class, info);
                    params.append(PayCallBack.class, callBack);
                    payHelper = TransitionFactory.create(PalManager.PAYPAL_PAY_MODEL, params);
                }
                break;
            default:
                break;
        }
        if (payHelper == null) {//未支持到
            Log.e("TdLoginHelper", "TdLoginHelper未支持到");
            return false;
        }
        payHelper.perform();
        return true;
    }
}
