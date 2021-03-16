package com.net.model.broadcast;

import com.mei.orc.http.response.BaseResponse;

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2019/1/15
 */
public class BroadCast_Room_Status {
    public static class Response extends BaseResponse<BroadCast_Room_Status> {

    }

    public int broadcast_id;
    public String room_id;
    public int broadcast_state;

}
