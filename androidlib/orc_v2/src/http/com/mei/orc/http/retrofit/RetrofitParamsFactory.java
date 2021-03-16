package com.mei.orc.http.retrofit;

import androidx.annotation.NonNull;

import java.util.Map;

/**
 * @author Created by Ling on 2019/5/14
 */
public interface RetrofitParamsFactory {

    @NonNull
    Map<String, String> params();
}
