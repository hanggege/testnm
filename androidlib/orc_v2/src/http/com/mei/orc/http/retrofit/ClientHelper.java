package com.mei.orc.http.retrofit;

import android.text.TextUtils;

import com.mei.orc.Cxt;
import com.mei.orc.common.CommonConstant;
import com.mei.orc.http.interceptor.MeiInterceptor;
import com.mei.orc.john.model.CookieModel;
import com.mei.orc.john.model.JohnUser;
import com.mei.orc.util.json.JsonExtKt;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.TimeUnit;

import okhttp3.Dispatcher;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;

import static com.mei.orc.util.app.AppUtilKt.getAppVersion;

/**
 * 佛祖保佑         永无BUG
 * <p>
 * Created by joker on 2017/2/28.
 */

public class ClientHelper {

    private static RetrofitParamsFactory factory;

    public static void setParamsFactory(RetrofitParamsFactory factoryImpl) {
        factory = factoryImpl;
    }

    private static OkHttpClient.Builder builder;

    static OkHttpClient.Builder getClearOkHttpBuilder() {
        if (builder == null) {
            Dispatcher dispatcher = new Dispatcher();
            dispatcher.setMaxRequests(32);
            builder = new OkHttpClient.Builder()
                    .connectTimeout(15, TimeUnit.SECONDS)
                    .dispatcher(dispatcher);
        }
        builder.interceptors().clear();
        return builder;
    }

    /****************************监听全局网络请求结果************************************/
    public static CopyOnWriteArraySet<MeiInterceptor> meiInterceptors = new CopyOnWriteArraySet<>();

    public static void putMeiInterceptor(MeiInterceptor interceptor) {
        meiInterceptors.add(interceptor);
    }

    public static void removeMeiInterceptor(MeiInterceptor interceptor) {
        meiInterceptors.remove(interceptor);
    }


    /**
     * 添加统一头部
     *
     * @param builder
     * @param userAgent
     * @return
     * @throws UnsupportedEncodingException
     */
    public static Request addGeneralHeader(Request.Builder builder, String userAgent) throws UnsupportedEncodingException {

        String appVersion = getAppVersion(Cxt.get());
        // 设备存在中文的情况下会报异常，跟服务端协商将user-agent全部内容进行utf-8编码
        builder.addHeader(CommonConstant.Key.USER_AGENT, URLEncoder.encode(userAgent, "utf-8"))
                .addHeader(CommonConstant.Key.VERSION, appVersion);
        Request request = builder.build();
        return request;
    }

    /**
     * 添加统一参数
     *
     * @param httpBuilder
     * @param channel
     */
    public static void addGeneralParameter(HttpUrl.Builder httpBuilder, String channel) {
        JohnUser johnUser = JohnUser.getSharedUser();
        if (TextUtils.isEmpty(johnUser.getSessionID())) {
            johnUser.loadPersistent();
        }
        String phoneSN = johnUser.getPhoneSN();
        String deviceIMEI = johnUser.getDeviceIMEI();
        String sessionID = johnUser.getSessionID();
        int login_user_id = johnUser.getUserID();
        String appVersion = getAppVersion(Cxt.get());

        if (!TextUtils.isEmpty(phoneSN))
            httpBuilder.setQueryParameter(CommonConstant.Key.PHONE_SN, phoneSN);
        if (!TextUtils.isEmpty(deviceIMEI))
            httpBuilder.setQueryParameter(CommonConstant.Key.DEVICE_ID, deviceIMEI);
        if (!TextUtils.isEmpty(sessionID))
            httpBuilder.setQueryParameter(CommonConstant.Key.SESSION_ID, sessionID);
        if (login_user_id > 0)
            httpBuilder.setQueryParameter(CommonConstant.Key.LOGIN_USER_ID, String.valueOf(login_user_id));
        if (factory != null) {
            Map<String, String> params = factory.params();
            for (String key : params.keySet()) {
                httpBuilder.setQueryParameter(key, params.get(key));
            }
        }
        httpBuilder.setQueryParameter(CommonConstant.Key.VERSION, appVersion);
        httpBuilder.setQueryParameter(CommonConstant.Key.APP_CHANNEL, channel);
        httpBuilder.setQueryParameter(CommonConstant.Key.PUSH_CILENT_ID, johnUser.getGetuiPushClientId());

        httpBuilder.setQueryParameter(CommonConstant.Key.COOKIE, JsonExtKt.toJson(CookieModel.parseCookie()));
    }
}
