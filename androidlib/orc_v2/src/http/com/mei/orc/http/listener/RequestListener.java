package com.mei.orc.http.listener;

import androidx.annotation.NonNull;

import com.mei.orc.http.exception.RxThrowable;

/**
 * user for ui {@link UIRequestListener}
 */
public interface RequestListener<RESULT> {

    void onRequestFailure(RxThrowable retrofitThrowable);

    void onRequestSuccess(@NonNull RESULT result);
}