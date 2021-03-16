package com.net.model.chick.upload;


import com.mei.orc.http.response.BaseResponse;

/**
 * Created by steven on 15/4/27.
 */

public class CheckAvatarViolation {
    public static class Response extends BaseResponse<CheckAvatarViolation> {

    }

    public boolean isViolation;
}
