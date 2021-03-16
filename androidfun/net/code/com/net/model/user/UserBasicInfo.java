package com.net.model.user;

import com.mei.orc.http.response.BaseResponse;

import java.util.ArrayList;
import java.util.List;

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2019-12-15
 */

public class UserBasicInfo {
    public static class Response extends BaseResponse<UserBasicInfo> {

    }

    public List<UserInfo> infoList = new ArrayList<>();
}
