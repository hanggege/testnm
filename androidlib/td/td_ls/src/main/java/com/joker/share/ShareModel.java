package com.joker.share;

import android.app.Activity;

import com.joker.connect.TdCallBack;
import com.joker.flag.TdFlags;
import com.joker.model.ShareInfo;
import com.joker.support.listener.TdHandlerListener;
import com.joker.support.listener.TdLifecycleListener;
import com.joker.support.listener.TdPerFormSuper;


/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2017/4/27.
 */

public abstract class ShareModel implements TdLifecycleListener, TdPerFormSuper {
    @TdFlags
    protected int type;
    protected Activity activity;
    protected TdCallBack callBack;
    protected ShareInfo shareInfo;

    public ShareModel(@TdFlags Integer type, Activity activity, ShareInfo info, TdCallBack callBack) {
        this.type = type;
        this.activity = activity;
        this.shareInfo = info;
        this.callBack = callBack;
        if (activity instanceof TdHandlerListener)
            ((TdHandlerListener) activity).registerLifecycle(this);
    }

    protected void unregisterLifecycle() {
        if (activity instanceof TdHandlerListener)
            ((TdHandlerListener) activity).unregisterLifecycle(this);
    }


}