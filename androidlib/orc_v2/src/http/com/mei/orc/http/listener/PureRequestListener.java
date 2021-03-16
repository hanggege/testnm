package com.mei.orc.http.listener;

import android.annotation.SuppressLint;
import android.text.TextUtils;

import com.mei.orc.http.exception.RxThrowable;
import com.mei.orc.http.response.DataResponse;


/**
 * Just resolve response .
 *
 * @author caowei
 * @email 646030315@qq.com
 * @time Created on 2016/10/20 0020
 */
public abstract class PureRequestListener<Result extends DataResponse> implements RequestListener<Result> {

    @SuppressLint("LongLogTag")
    @Override
    public void onRequestFailure(RxThrowable e) {
        e.printStackTrace();
        onRequestFail(e);
        onRequestFinished();
    }

    public void onRequestFail(RxThrowable e) {

    }

    @Override
    public void onRequestSuccess(Result result) {
        onResponseStart();
        if (result != null && result.isSuccess()) {
            onResponseCorrect(result);
        } else {
            onResponseError(result);
        }
        onRequestFinished();
    }

    public void onRequestFinished() {
    }

    public void onResponseStart() {
    }

    protected abstract void onResponseCorrect(Result result);

    public void onResponseError(Result result) {
        if (result == null) {
            return;
        }
        int errorRtn = result.getRtn();

        onToast(TextUtils.isEmpty(result.getErrMsg()) ? "返回数据异常" : errorRtn == 1110 ? "验证码错误，请重新获取验证码" : result.getErrMsg());
    }

    public void onToast(String message) {

    }

}
