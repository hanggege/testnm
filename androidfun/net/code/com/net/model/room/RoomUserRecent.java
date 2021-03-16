package com.net.model.room;

import com.mei.orc.http.response.BaseResponse;

import java.util.List;

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2019-12-29
 */

public class RoomUserRecent {

    public static class Response extends BaseResponse<RoomUserRecent> {

    }

    public int total;
    public List<RoomInfo.CreateUser> users;
    public RoomInfo.CreateUser firstApplyUser;

}
