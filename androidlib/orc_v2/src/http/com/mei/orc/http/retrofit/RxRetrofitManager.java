package com.mei.orc.http.retrofit;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by joker on 16/8/26.
 */
public class RxRetrofitManager {
    private ConcurrentHashMap<String, RetrofitClientBuilder> clientMap;


    public RxRetrofitManager() {
        clientMap = new ConcurrentHashMap<>();
    }

    /**
     * get Client by classname
     */
    public RetrofitClientBuilder getRetrofitClient(Class<? extends RetrofitClient> clientCls) throws NullPointerException {
        RetrofitClientBuilder builder = clientMap.get(clientCls.getName());
        if (builder == null) {
            builder = new RetrofitClientBuilder(clientCls);
            clientMap.put(clientCls.getName(), builder);
        }
        return builder;
    }

    /**
     * destroy
     */
    public void recycleManager() {
        for (String key : clientMap.keySet()) {
            RetrofitClientBuilder builder = clientMap.get(key);
            builder.create().cancelAllRequest();
        }
        clientMap.clear();
    }

}
