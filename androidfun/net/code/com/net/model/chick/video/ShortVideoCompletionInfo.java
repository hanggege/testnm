package com.net.model.chick.video;

import com.mei.orc.http.response.BaseResponse;

import androidx.annotation.Nullable;

/**
 * @author Created by lenna on 2020/8/24
 */
public class ShortVideoCompletionInfo {
    @Nullable
    public String toast;

    public static class Response extends BaseResponse<ShortVideoCompletionInfo> {

    }

}
