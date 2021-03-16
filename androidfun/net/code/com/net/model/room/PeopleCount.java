package com.net.model.room;

import com.mei.orc.http.response.BaseResponse;

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2019-12-14
 */
public class PeopleCount {

    public static class Response extends BaseResponse<PeopleCount> {

    }

    public int applyCount;
    public int onlineCount;
    public int applyRank;
    public long receiveCount;

}
