package com.mei.orc.callback;

import androidx.annotation.Nullable;

/**
 * Created by guoyufeng on 12/5/15.
 */
public abstract class TaskCallback<Result> {

    public abstract void onSuccess(Result result);

    public abstract void onFailed(@Nullable Exception exception, int errCode, Object... objects);

    public void onCanceled() {
        //do nothing by default
    }

    public static <T> TaskCallback<T> defaultCallback() {
        return new TaskCallback<T>() {
            @Override
            public void onSuccess(T t) {

            }

            @Override
            public void onFailed(Exception exception, int errCode, Object... objects) {

            }
        };
    }

}
