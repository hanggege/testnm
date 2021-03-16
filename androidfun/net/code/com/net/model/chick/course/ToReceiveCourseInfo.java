package com.net.model.chick.course;

import com.mei.orc.http.response.BaseResponse;

import androidx.annotation.Nullable;

/**
 * @author Created by lenna on 2020/11/10
 */
public class ToReceiveCourseInfo {
    @Nullable
    public String detailLink;
    @Nullable
    public String detailHalfLink;

    public static class Response extends BaseResponse<ToReceiveCourseInfo> {

    }
}
