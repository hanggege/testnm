package com.mei.orc.http.log;

import okhttp3.logging.HttpLoggingInterceptor;

/**
 * @author Created by Ling on 2019/3/14
 */
public class EmptyLoger implements HttpLoggingInterceptor.Logger {
    @Override
    public void log(String message) {
    }
}
