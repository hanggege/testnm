package com.net.model.chick.workroom;

import com.mei.orc.http.response.BaseResponse;
import com.net.model.chick.user.ChickUserInfo;
import com.net.model.chick.video.ShortVideoList;
import com.net.model.user.UserInfo;

import androidx.annotation.Nullable;

/**
 * @author Created by lenna on 2020/7/22
 */
public class WorkRoomInfo {

    @Nullable
    public UserInfo info;
    @Nullable
    public ChickUserInfo.Extra extra;
    public static class Response extends BaseResponse<WorkRoomInfo> {

    }
}
