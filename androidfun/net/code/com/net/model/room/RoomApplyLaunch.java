package com.net.model.room;

import com.mei.orc.http.response.BaseResponse;

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2019-12-13
 */

public class RoomApplyLaunch {
    public static class Response extends BaseResponse<RoomApplyLaunch> {

    }

    public int applyCount;
    public String title;
    public String subTitle;
    public String tips;
}
