package com.net.model.room;

import com.mei.orc.http.response.BaseResponse;

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2019-12-29
 */

public class DataStatistics {
    public static class Response extends BaseResponse<DataStatistics> {

    }

    public int followCount;
    public int userCount;
    public int receiveCount;
    public String receiveCountTips;
    public long broadcastTime;
}
