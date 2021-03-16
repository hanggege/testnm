package com.net.model.chick.user;

import com.mei.orc.http.response.BaseResponse;

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2019-12-30
 */

public class HomeStatus {

    public static class Response extends BaseResponse<HomeStatus> {

    }

    public int datingStatus;
    public String roomId;

    public boolean isLiving() {
        return datingStatus > 0;
    }

    public String liveStatus() {
        if (datingStatus == 1) {
            return "相亲中";
        } else if (datingStatus == 2) {
            return "专属相亲房";
        } else {
            return "未相亲";
        }
    }

    public String getType() {
        if (datingStatus == 1) {
            return "COMMON";
        } else if (datingStatus == 2) {
            return "EXCLUSIVE";
        } else {
            return "";
        }
    }
}
