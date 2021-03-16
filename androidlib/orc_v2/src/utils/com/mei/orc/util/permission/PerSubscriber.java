package com.mei.orc.util.permission;


import com.mei.orc.Cxt;
import com.mei.orc.rxpermission.Permission;
import com.mei.orc.ui.toast.UIToast;

import io.reactivex.observers.ResourceObserver;


/**
 * 佛祖保佑         永无BUG
 * <p>
 * Created by joker on 2017/1/5.
 */

public abstract class PerSubscriber<T> extends ResourceObserver<T> {

    @Override
    public void onComplete() {
        dispose();
    }

    @Override
    public void onError(Throwable e) {
        onFinish();
        UIToast.toast(Cxt.get(), "权限获取失败");
        dispose();
    }

    @Override
    public void onNext(T t) {
        onFinish();
        if (t instanceof Boolean) {
            if ((Boolean) t) {
                onSuccess(t);
            } else {
                onFail(t);
            }
        } else if (t instanceof Permission) {
            if (((Permission) t).granted) {
                onSuccess(t);
            } else {
                onFail(t);
            }
        }
        onComplete();
    }

    public abstract void onSuccess(T permissionGranted);

    public void onFail(T permission) {

    }

    public void onFinish() {

    }


}
