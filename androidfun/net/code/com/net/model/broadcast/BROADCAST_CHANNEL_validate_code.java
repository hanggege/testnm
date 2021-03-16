package com.net.model.broadcast;

import com.mei.orc.http.response.BaseResponse;

/**
 * Created by caowei on 15/12/21.
 */
public class BROADCAST_CHANNEL_validate_code {

    public static class Response extends BaseResponse<BROADCAST_CHANNEL_validate_code> {

    }

    public String broadcast_id;
    public String url;
}
