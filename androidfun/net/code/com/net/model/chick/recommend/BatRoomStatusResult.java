package com.net.model.chick.recommend;

import com.mei.orc.http.response.BaseResponse;

import java.util.HashMap;

/**
 * author : Song Jian
 * date   : 2020/1/8
 * desc   :
 */
public class BatRoomStatusResult {


    /**
     * targetId : {"begin":1578710129,"roomId":"27750899.1578709479338","roomType":"COMMON"}
     */

    public StatusResult targetId;


    public static class StatusResult {
        /**
         * begin : 1578710129
         * roomId : 27750899.1578709479338
         * roomType : COMMON
         */

        public Long begin;
        public String roomId;
        public String roomType;
        public int gender;
        public String personalImage;
        public String avatar;
    }

    public static class Response extends BaseResponse<HashMap<String, StatusResult>> {

    }


}
