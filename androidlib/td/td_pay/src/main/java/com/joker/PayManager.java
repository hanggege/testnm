package com.joker;

import android.app.Activity;

import androidx.annotation.NonNull;

import com.joker.connect.PayCallBack;
import com.joker.model.order.Order;
import com.joker.pay.PayHelper;

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2017/5/2.
 */

public class PayManager {
    /**
     * 调起支付
     */
    public static boolean performPay(Activity activity, @PayFlags int type, @NonNull Order info, @NonNull PayCallBack callBack) {
        PayHelper holder = new PayHelper(activity);
        return holder.performPay(type, info, callBack);

    }
}
