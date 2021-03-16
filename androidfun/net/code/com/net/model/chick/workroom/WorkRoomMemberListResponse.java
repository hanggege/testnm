package com.net.model.chick.workroom;

import com.mei.orc.http.response.BaseResponse;

import java.util.List;

import androidx.annotation.Nullable;

/**
 * @author Created by lenna on 2020/7/30
 */
public class WorkRoomMemberListResponse {
    @Nullable
    public List<WorkRoomMember> memberList;

    public static class Response extends BaseResponse<WorkRoomMemberListResponse> {

    }
}
