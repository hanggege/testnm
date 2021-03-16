package com.net.model.broadcast;


import com.mei.orc.http.response.BaseResponse;

/**
 * Created by guoyufeng on 20/8/15.
 */

public class BROADCAST_info {
    public static class Response extends BaseResponse<BROADCAST_info> {

    }

    private Room room;

    @Override
    public String toString() {
        return "BROADCAST_info{" +
                "room=" + room +
                '}';
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public Room getRoom() {
        return room;
    }

}
