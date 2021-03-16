package com.mei.orc.john.network;

import com.mei.orc.channel.PkgChannelKt;
import com.mei.orc.http.retrofit.ClientHelper;
import com.mei.orc.http.retrofit.RetrofitClient;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by joker on 16/9/8.
 */
public class JohnClient extends RetrofitClient {

    private static String userAgent = "";
    private static String BASE_API = "john.yeshouxiansheng.com";


    public static void setUserAgent(String userAgent) {
        JohnClient.userAgent = userAgent;
    }

    @Override
    public String getBaseUrl() {
        String scheme = isHttpScheme ? "http://" : "https://";
        return scheme + BASE_API;
    }

    @Override
    protected Interceptor getInterceptor() {
        Interceptor interceptor = new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {

                Request original = chain.request();
                HttpUrl originalHttpUrl = original.url();

                HttpUrl.Builder httpBuilder = originalHttpUrl.newBuilder();
                ClientHelper.addGeneralParameter(httpBuilder, PkgChannelKt.getChannelId());
                httpBuilder.setQueryParameter("user_agent", userAgent);

                HttpUrl url = httpBuilder.build();
                Request.Builder builder = original.newBuilder().url(url);
                Request request = ClientHelper.addGeneralHeader(builder, userAgent);

                return chain.proceed(request);
            }
        };
        return interceptor;
    }


}
