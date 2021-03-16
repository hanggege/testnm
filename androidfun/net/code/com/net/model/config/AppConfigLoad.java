package com.net.model.config;

import com.mei.orc.http.response.BaseResponse;

import java.util.Map;

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2020/7/8
 */
public class AppConfigLoad {

    public Map<String, String> custom;

    public static class Response extends BaseResponse<AppConfigLoad> {
    }

}
