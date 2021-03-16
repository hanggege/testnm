package com.net.model.room;

import com.mei.orc.http.response.BaseResponse;

/**
 * @author Created by lenna on 2020/9/10
 */
public class RefreshRoomWeekRank {
    public String weekRankText;
    public long delayTime;

    public static class Response extends BaseResponse<RefreshRoomWeekRank> {

    }
}
