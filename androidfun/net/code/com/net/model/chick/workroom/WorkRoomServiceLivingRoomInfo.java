package com.net.model.chick.workroom;

import com.mei.orc.http.response.BaseResponse;

/**
 * WorkRoomServiceLivingRoomInfo
 *
 * @author kun
 * @version 1.0.0
 * @since 2020-08-12
 */
public class WorkRoomServiceLivingRoomInfo {

    public String roomId;
    public boolean isLiving;

    public static class Response extends BaseResponse<WorkRoomServiceLivingRoomInfo> {

    }
}
