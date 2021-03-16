package com.mei.orc.http.listener;

import android.text.TextUtils;

import com.mei.orc.Cxt;
import com.mei.orc.http.exception.CanceledException;
import com.mei.orc.http.exception.RxThrowable;
import com.mei.orc.ui.toast.UIToast;


public abstract class UIRequestListener<RESULT> implements RequestListener<RESULT> {

    private boolean showToast;

    public UIRequestListener() {
        showToast = true;
    }

    public UIRequestListener(boolean showToast) {
        this.showToast = showToast;
    }

    @Override
    public void onRequestFailure(RxThrowable retrofitThrowable) {
        if (showToast) {
            if (retrofitThrowable instanceof CanceledException) {

            } else {
                if (!TextUtils.isEmpty(retrofitThrowable.getMessage())) {
                    UIToast.toast(Cxt.get(), retrofitThrowable.getMessage());
                }
            }
        }
    }


}