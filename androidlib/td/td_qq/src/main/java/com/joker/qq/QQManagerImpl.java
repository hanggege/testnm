//package com.joker.qq;
//
//import android.content.Context;
//import androidx.annotation.NonNull;
//
//import com.joker.connect.QQManager;
//import com.joker.model.BackResult;
//import com.joker.model.TdUserInfo;
//import com.joker.support.TdAction;
//import com.joker.utils.TdUserApi;
//import com.tencent.connect.UserInfo;
//import com.tencent.connect.auth.QQToken;
//import com.tencent.tauth.IUiListener;
//import com.tencent.tauth.UiError;
//
///**
// * 佛祖保佑         永无BUG
// *
// * @author Created by joker on 2017/4/28.
// */
//
//public class QQManagerImpl implements QQManager {
//
//    /**
//     * 获取第三方用户信息
//     */
//    @Override
//    public void getUserInfo(Context context, @NonNull BackResult result, final TdAction<TdUserInfo> callback) {
//        if (result.qqToken instanceof QQToken) {
//            requestQQInfo(context, result, callback);
//        } else {
//            callback.onCallback(null);
//        }
//    }
//
//    private static void requestQQInfo(Context context, @NonNull BackResult result, final TdAction<TdUserInfo> callback) {
//        UserInfo info = new UserInfo(context, (QQToken) result.qqToken);
//        info.getUserInfo(new IUiListener() {
//            @Override
//            public void onComplete(Object o) {
//                callback.onCallback(TdUserApi.parseQQ(o != null ? String.valueOf(o) : ""));
//            }
//
//            @Override
//            public void onError(UiError uiError) {
//                callback.onCallback(null);
//            }
//
//            @Override
//            public void onCancel() {
//                callback.onCallback(null);
//            }
//        });
//    }
//}
