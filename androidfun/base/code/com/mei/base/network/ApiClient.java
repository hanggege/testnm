package com.mei.base.network;

import com.mei.orc.channel.PkgChannelKt;
import com.mei.orc.common.CommonConstant;
import com.mei.orc.http.retrofit.ClientHelper;
import com.mei.orc.http.retrofit.RetrofitClient;
import com.mei.provider.AppBuildConfig;
import com.mei.provider.ProjectExt;
import com.mei.wood.util.MeiUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;

/**
 * Created by joker on 16/9/8.
 */
public class ApiClient extends RetrofitClient {

    private static final String TEST_API = "neptune-app.zhixinli.vip";
    private static String BASE_API = "neptune-app.zhixinli.vip";

    public static String getCustomUserAgent() {
        return ProjectExt.FullUserAgent;
    }


    @Override
    public String getBaseUrl() {
        String api = isTestClient ? TEST_API : BASE_API;
        String scheme = isHttpScheme ? "http://" : "https://";
        return scheme + api;
    }


    @Override
    protected Interceptor getInterceptor() {
        Interceptor interceptor = chain -> {

            Request original = chain.request();
            HttpUrl originalHttpUrl = original.url();

            if (AppBuildConfig.IS_TEST) {
                String req = new SimpleDateFormat("MM/dd HH:mm:ss", Locale.getDefault()).format(new Date()) + " :  " + originalHttpUrl.uri().toString();
                requestUrlList.add(req);
            }

            HttpUrl.Builder httpBuilder = originalHttpUrl.newBuilder();
            ClientHelper.addGeneralParameter(httpBuilder, PkgChannelKt.getChannelId());
            httpBuilder.setQueryParameter("user_agent", getCustomUserAgent());
            httpBuilder.setQueryParameter("ssid", MeiUtil.INSTANCE.getSSID());

            //chick不需要这个参数
            httpBuilder.removeAllQueryParameters(CommonConstant.Key.COOKIE);

            HttpUrl url = httpBuilder.build();
            Request.Builder builder = original.newBuilder().url(url);
            Request request = ClientHelper.addGeneralHeader(builder, getCustomUserAgent());
            return chain.proceed(request);
        };
        return interceptor;
    }


}