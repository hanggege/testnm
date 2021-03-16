package com.joker.pay;

import android.app.Activity;

import com.joker.PayFlags;
import com.joker.connect.PayCallBack;
import com.joker.support.listener.TdHandlerListener;
import com.joker.support.listener.TdLifecycleListener;
import com.joker.support.listener.TdPerFormSuper;

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2017/5/2.
 */

public abstract class PayModel implements TdLifecycleListener, TdPerFormSuper {
    protected PayCallBack callBack;
    protected Activity activity;
    @PayFlags
    protected int type;

    public PayModel(PayCallBack callBack, Activity activity, @PayFlags int type) {
        this.callBack = callBack;
        this.activity = activity;
        this.type = type;
        if (activity instanceof TdHandlerListener)
            ((TdHandlerListener) activity).registerLifecycle(this);
    }

    protected void unregisterLifecycle() {
        if (activity instanceof TdHandlerListener)
            ((TdHandlerListener) activity).unregisterLifecycle(this);
    }
}
