package com.net.model.chick.user;

import com.mei.orc.http.response.BaseResponse;

import java.util.List;

/**
 * RangeOfAgeInfo
 *
 * @author kun
 * @version 1.0.0
 * @since 2020-08-18
 */
public class RangeOfAgeInfo {

    public static class Response extends BaseResponse<RangeOfAgeInfo> {
    }

    public List<String> years;
}
