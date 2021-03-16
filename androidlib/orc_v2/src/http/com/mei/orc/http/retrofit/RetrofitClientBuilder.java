package com.mei.orc.http.retrofit;

/**
 * Created by joker on 16/9/2.
 */
public class RetrofitClientBuilder {
    private RetrofitClient client;

    public RetrofitClientBuilder(Class<? extends RetrofitClient> clientCls) {
        try {
            client = clientCls.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public RetrofitClientBuilder setLogVisible(Boolean isDebug) {
        client.setLogVisible(isDebug);
        return this;
    }

    public RetrofitClient create() {
        return client.create();
    }


}
