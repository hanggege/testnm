package com.mei.wood.rx;

import android.text.TextUtils;

import com.mei.orc.Cxt;
import com.mei.orc.http.exception.ExceptionHandle;
import com.mei.orc.ui.toast.UIToast;
import com.mei.wood.BuildConfig;

import io.reactivex.observers.ResourceObserver;

/**
 * Created by guoyufeng on 31/8/15.
 */
public abstract class ToastErrorObserver<T> extends ResourceObserver<T> {

    @Override
    public void onComplete() {
        //do nothing by default
    }

    @Override
    public void onError(Throwable e) {
        e.printStackTrace();
        String msg = ExceptionHandle.Companion.handleException(e).getMessage();
        if (!TextUtils.isEmpty(msg)) {
            UIToast.toast(Cxt.get(), msg);
        } else {
            if (BuildConfig.IS_TEST) {
                UIToast.toast(Cxt.get(), "error:" + e.getMessage());
            }
        }
    }

}
