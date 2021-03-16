package com.joker.login;

import android.app.Activity;

import com.joker.connect.TdCallBack;
import com.joker.flag.TdFlags;
import com.joker.support.listener.TdHandlerListener;
import com.joker.support.listener.TdLifecycleListener;
import com.joker.support.listener.TdPerFormSuper;

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2017/4/27.
 */

public abstract class LoginModel implements TdPerFormSuper, TdLifecycleListener {
    @TdFlags
    protected int type;
    protected Activity activity;
    protected TdCallBack callBack;

    public LoginModel(@TdFlags int type, Activity activity, TdCallBack callBack) {
        this.type = type;
        this.activity = activity;
        this.callBack = callBack;
        if (activity instanceof TdHandlerListener) {
            ((TdHandlerListener) activity).registerLifecycle(this);
        }
    }


    protected void unregisterLifecycle() {
        if (activity instanceof TdHandlerListener)
            ((TdHandlerListener) activity).unregisterLifecycle(this);
    }

}
