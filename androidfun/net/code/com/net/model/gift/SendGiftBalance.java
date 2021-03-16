package com.net.model.gift;

import com.mei.orc.http.response.BaseResponse;

/**
 * Created by hang on 2019-12-17.
 */
public class SendGiftBalance {

    public static class Response extends BaseResponse<SendGiftBalance> {
    }

    public double balance;
}
