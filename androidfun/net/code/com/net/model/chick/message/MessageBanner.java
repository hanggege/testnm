package com.net.model.chick.message;

import com.mei.orc.http.response.BaseResponse;

import java.util.List;

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2020/2/25
 */

public class MessageBanner {


    public static class Response extends BaseResponse<MessageBanner> {

    }

    public List<Banner> banner;

    public static class Banner {
        public String action = "";
        public String image = "";
        public String title = "";

    }

}
