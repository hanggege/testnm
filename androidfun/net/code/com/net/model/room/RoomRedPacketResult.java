package com.net.model.room;

import com.joker.im.custom.chick.CouponInfo;
import com.mei.orc.http.response.BaseResponse;

import androidx.annotation.Nullable;

/**
 * @author Created by lenna on 2020/7/28
 */
public class RoomRedPacketResult {
    public static class Response extends BaseResponse<RoomRedPacketResult> {
    }

    @Nullable
    public RoomRedPacket config;

    @Nullable
    public CouponInfo coupons;
}
