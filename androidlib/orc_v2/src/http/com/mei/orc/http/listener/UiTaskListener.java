package com.mei.orc.http.listener;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.mei.orc.Cxt;
import com.mei.orc.common.CommonConstant;
import com.mei.orc.http.exception.CanceledException;
import com.mei.orc.http.exception.RxThrowable;
import com.mei.orc.http.response.DataResponse;
import com.mei.orc.ui.loading.LoadingToggle;
import com.mei.orc.ui.toast.UIToast;

/**
 * Created by guoyufeng on 8/5/15.
 */
public abstract class UiTaskListener<Result extends DataResponse> implements RequestListener<Result> {

    private Activity activity;
    private LoadingToggle loadingToggle;
    private SwipeRefreshLayout swipeRefresh;
    private StopLoadingStrategy stopLoadingStrategy;

    public UiTaskListener(Activity activity, LoadingToggle loadingToggle, StopLoadingStrategy stopLoadingStrategy) {
        this.activity = activity;
        this.loadingToggle = loadingToggle;
        this.stopLoadingStrategy = stopLoadingStrategy;
    }

    public UiTaskListener(Activity activity, LoadingToggle loadingToggle) {
        this(activity, loadingToggle, StopLoadingStrategy.StopOnFinish);
    }

    public UiTaskListener(SwipeRefreshLayout swipeRefresh, Activity activity) {
        this(activity, null, StopLoadingStrategy.StopOnFinish);
        this.swipeRefresh = swipeRefresh;
    }

    public UiTaskListener(Activity activity) {
        this(activity, null, null);
    }

    @SuppressLint("LongLogTag")
    @Override
    public void onRequestFailure(RxThrowable e) {
        onRequestFinished();
        onRequestFail(e);
        if (e instanceof CanceledException) {
            try {
                if (loadingToggle != null) {
                    loadingToggle.showLoading(false);
                }

                if (swipeRefresh != null) {
                    swipeRefresh.setRefreshing(false);
                }
            } catch (Exception err) {
                err.printStackTrace();
            }
            Log.e("UiTaskListener_Exception", e.getMessage());
            return;
        }

        if (handleNotSuccess()) {
//            return;
        }
        e.printStackTrace();
        String msg = e.getMessage();
        if (!TextUtils.isEmpty(msg)) {
            UIToast.toast(Cxt.get(), msg);
        } else {
            UIToast.toast(Cxt.get(), "spiceException:" + e.getMessage());// close when release
        }
    }

    public void onRequestFail(RxThrowable e) {

    }

    protected void onRequestFinished() {
        if (loadingToggle != null && stopLoadingStrategy == StopLoadingStrategy.StopOnFinish) {
            loadingToggle.showLoading(false);
        }
        if (swipeRefresh != null && stopLoadingStrategy == StopLoadingStrategy.StopOnFinish) {
            swipeRefresh.setRefreshing(false);
        }
    }

    @Override
    public void onRequestSuccess(Result result) {
        onRequestFinished();
        if (result != null && result.isSuccess()) {
//            if (!TextUtils.isEmpty(result.getErrMsg())) {
//                UIToast.toast(Cxt.get(), result.getErrMsg());
//            }
            onResponseCorrect(result);
        } else {
            onResponseError(result);
        }
    }

    protected abstract void onResponseCorrect(@NonNull Result result);

    public void onResponseError(Result result) {
        if (result == null) {
            return;
        }
        if (handleNotSuccess()) {
//            return;
        }

        int errorRtn = result.getRtn();
        String message = result.getErrMsg();
        if (TextUtils.isEmpty(message)) {
            message = "Unknown message with code: " + errorRtn;
        }
        if (errorRtn != CommonConstant.Config.NEED_PHONE_CODE && errorRtn != CommonConstant.Config.FIRST_LOGIN_NEED_REGIST) {//当是提示图像验证码为空和检测账号不存在时，不予提示
            UIToast.toast(Cxt.get(), message);
        }

    }

    /**
     * @return true if handle this, false otherwise
     */
    public boolean handleNotSuccess() {
        if (stopLoadingStrategy == StopLoadingStrategy.StopOnFail && loadingToggle != null) {
            loadingToggle.showLoading(false);
        }
        if (stopLoadingStrategy == StopLoadingStrategy.StopOnFail && swipeRefresh != null) {
            swipeRefresh.setRefreshing(false);
        }

        //do nothing by default
        return false;
    }


    public void setLoadingToggle(LoadingToggle loadingToggle) {
        this.loadingToggle = loadingToggle;
        if (stopLoadingStrategy == null) {
            stopLoadingStrategy = StopLoadingStrategy.StopOnFinish;
        }
    }

}
