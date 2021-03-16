package com.net.model.gift;

import com.mei.orc.http.response.BaseResponse;

import java.util.List;

public class GiftListInfo {
    public static class Response extends BaseResponse<GiftListInfo> {

    }

    public List<GiftInfo> gifts;

}
