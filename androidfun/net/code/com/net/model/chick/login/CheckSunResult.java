package com.net.model.chick.login;

import com.mei.orc.http.response.BaseResponse;

/**
 * Created by zzw on 2019-12-13
 * Des:
 */
public class CheckSunResult {

    public boolean ifSpecial;
    public String checksum;

    public static class Response extends BaseResponse<CheckSunResult> {

    }

}
