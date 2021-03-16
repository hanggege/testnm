package com.net.model.user;

import com.mei.orc.http.response.BaseResponse;

import java.util.List;

import androidx.annotation.Nullable;

/**
 * @author Created by lenna on 2020/11/3
 */
public class ShieldingInfo {
    @Nullable
    public List<Integer> userIds;

    public static class Response extends BaseResponse<ShieldingInfo> {

    }

}
