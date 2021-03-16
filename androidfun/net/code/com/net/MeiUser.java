package com.net;

import androidx.annotation.NonNull;

import com.mei.init.BaseAppKt;
import com.mei.orc.callback.Callback;
import com.mei.orc.http.exception.RxThrowable;
import com.mei.orc.http.listener.RequestListener;
import com.mei.orc.http.retrofit.RetrofitClient;
import com.mei.orc.john.model.JohnUser;
import com.net.model.chick.user.ChickUserInfo;
import com.net.network.chick.user.MyInfoRequest;

/**
 * Created by guoyufeng on 15/5/15.
 */
public class MeiUser {

    private static ChickUserInfo INSTANCE = new ChickUserInfo();

    public static ChickUserInfo getSharedUser() {
        if (INSTANCE == null) {
            INSTANCE = new ChickUserInfo();
        }
        return INSTANCE;
    }

    public static void resetUser(Callback<ChickUserInfo.Response> back) {
        resetUser(BaseAppKt.spiceHolder().getApiSpiceMgr(), new RequestListener<ChickUserInfo.Response>() {
            @Override
            public void onRequestFailure(RxThrowable retrofitThrowable) {
                back.onCallback(null);
            }

            @Override
            public void onRequestSuccess(@NonNull ChickUserInfo.Response response) {
                back.onCallback(response);
            }
        });
    }

    public static void resetUser(RetrofitClient melkorClient, final RequestListener<ChickUserInfo.Response> listener) {
        if (JohnUser.getSharedUser().hasLogin()) {
            resetUser(melkorClient, new MyInfoRequest(), listener);
        }
    }


    private static void resetUser(RetrofitClient melkorClient, MyInfoRequest request, final RequestListener<ChickUserInfo.Response> listener) {
        melkorClient.execute(request, new RequestListener<ChickUserInfo.Response>() {
            @Override
            public void onRequestFailure(RxThrowable retrofitThrowable) {
                if (listener != null) listener.onRequestFailure(retrofitThrowable);
            }

            @Override
            public void onRequestSuccess(@NonNull ChickUserInfo.Response response) {
                if (response.getData() != null && response.isSuccess()) {
                    INSTANCE = response.getData();
                }
                if (listener != null) {
                    listener.onRequestSuccess(response);
                }
            }
        });
    }

    public static void reset() {
        INSTANCE = new ChickUserInfo();
    }

    public static void setUserInfo(ChickUserInfo userInfo) {
        INSTANCE = userInfo;
    }
}
