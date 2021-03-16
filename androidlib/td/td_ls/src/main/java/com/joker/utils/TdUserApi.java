//package com.joker.utils;
//
//import android.content.Context;
//import androidx.annotation.NonNull;
//import android.text.TextUtils;
//import android.util.Log;
//
//import com.joker.TdType;
//import com.joker.connect.TdConstants;
//import com.joker.model.BackResult;
//import com.joker.model.TdUserInfo;
//import com.joker.support.TdAction;
//import com.joker.support.TdRequest;
//import com.joker.support.listener.TdNetAdapter;
//import com.joker.support.proxy.TransitionFactory;
//
//import org.json.JSONObject;
//
///**
// * 佛祖保佑         永无BUG
// *
// * @author Created by joker on 2017/4/28.
// */
//
//public class TdUserApi {
//
//    public static void getUserInfo(Context context, @NonNull BackResult result, TdNetAdapter netAdapter, final TdAction<TdUserInfo> callback) {
//        switch (result.type) {
//            case TdType.weibo:
//                String wbUrl = "https://api.weibo.com/2/users/show.json?uid="
//                        + result.open_id + "&access_token=" + result.token;
//                TdAction<String> action = new TdAction<String>() {
//                    @Override
//                    public void onCallback(String result) {
//                        callback.onCallback(parseWeibo(result));
//                    }
//                };
//                if (netAdapter != null) {
//                    netAdapter.requestNet(wbUrl, action);
//                } else {
//                    TdRequest.requestNet(wbUrl, action);
//                }
//
//                break;
//            case TdType.weixin:
//                final String wxUrl = "https://api.weixin.qq.com/sns/userinfo?"
//                        + "openid=" + result.open_id + "&access_token=" + result.access_token;
//                TdAction<String> wxAction = new TdAction<String>() {
//                    @Override
//                    public void onCallback(String result) {
//                        callback.onCallback(parseWeixin(result));
//                    }
//                };
//                if (netAdapter != null) {
//                    netAdapter.requestNet(wxUrl, wxAction);
//                } else {
//                    TdRequest.requestNet(wxUrl, wxAction);
//                }
//                break;
//            case TdType.qq:
//                requestQQ(context, result, callback);
//                break;
//            default:
//                callback.onCallback(null);
//                break;
//        }
//    }
//
//    private static void requestQQ(Context context, @NonNull BackResult result, final TdAction<TdUserInfo> callback) {
//        TdConstants manager = TransitionFactory.newInstance(TdConstants.QQ_MANAGER_IMPL, null);
//        if (manager != null) {
//            manager.getUserInfo(context, result, callback);
//        } else {
//            callback.onCallback(null);
//        }
//    }
//
//
//    private static TdUserInfo parseWeibo(String result) {
//        if (TextUtils.isEmpty(result)) {
//            return null;
//        }
//        try {
//            TdUserInfo userInfo = new TdUserInfo();
//            JSONObject json = new JSONObject(result);
//            userInfo.avatar = getValue(json, "avatar_large");
//            userInfo.gender = TextUtils.equals(getValue(json, "gender"), "m") ? 1 : 2;
//            userInfo.nick_name = getValue(json, "name");
//            userInfo.location = getValue(json, "location");
//            if (TextUtils.isEmpty(userInfo.nick_name)) {
//                userInfo = null;
//            }
//            return userInfo;
//        } catch (Exception e) {
//            e.printStackTrace();
//            Log.e(TdUserApi.class.getSimpleName(), "result = " + result);
//        }
//        return null;
//
//    }
//
//    private static TdUserInfo parseWeixin(String result) {
//        if (TextUtils.isEmpty(result)) {
//            return null;
//        }
//        try {
//            TdUserInfo userInfo = new TdUserInfo();
//            JSONObject json = new JSONObject(result);
//            userInfo.avatar = getValue(json, "headimgurl");
//            userInfo.gender = getGender(json, "sex");
//            userInfo.nick_name = getValue(json, "nickname");
//            userInfo.location = getValue(json, "city");
//            if (TextUtils.isEmpty(userInfo.nick_name)) {
//                userInfo = null;
//            }
//            return userInfo;
//        } catch (Exception e) {
//            e.printStackTrace();
//            Log.e(TdUserApi.class.getSimpleName(), "result = " + result);
//        }
//        return null;
//    }
//
//    public static TdUserInfo parseQQ(String result) {
//        if (TextUtils.isEmpty(result)) {
//            return null;
//        }
//        try {
//            TdUserInfo userInfo = null;
//            JSONObject json = new JSONObject(result);
//            int ret = json.getInt("ret");
//            if (ret == 0) {
//                userInfo = new TdUserInfo();
//                userInfo.avatar = getValue(json, "figureurl_qq_1");
//                userInfo.gender = TextUtils.equals(getValue(json, "gender"), "女") ? 2 : 1;
//                userInfo.nick_name = getValue(json, "nickname");
//                userInfo.location = getValue(json, "city");
//            }
//            return userInfo;
//        } catch (Exception e) {
//            e.printStackTrace();
//            Log.e(TdUserApi.class.getSimpleName(), "result = " + result);
//        }
//        return null;
//    }
//
//    private static String getValue(JSONObject json, String key) {
//        String value = "";
//        try {
//            value = json.getString(key);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return value;
//    }
//
//    private static int getGender(JSONObject json, String key) {
//        int value = 1;
//        try {
//            value = json.getInt(key);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return value;
//    }
//}
