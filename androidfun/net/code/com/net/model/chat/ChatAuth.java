package com.net.model.chat;

import com.mei.orc.http.response.BaseResponse;

import androidx.annotation.Nullable;

/**
 * @author Created by lenna on 2020/8/4
 */
public class ChatAuth {
    public static class Response extends BaseResponse<ChatAuth> {

    }
    @Nullable
    public String specialServiceTips;
}
