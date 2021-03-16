package com.net.model.chat;


import com.mei.orc.http.response.BaseResponse;
import com.net.model.room.RoomInfo;

/**
 * Created by steven on 15/4/27.
 */

public class ExclusiveHandle {

    public static class Response extends BaseResponse<ExclusiveHandle> {

    }

    public RoomInfo roomInfo;
}
