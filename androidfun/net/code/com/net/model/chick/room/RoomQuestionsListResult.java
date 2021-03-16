package com.net.model.chick.room;

import com.mei.orc.http.response.BaseResponse;

import java.util.List;

/**
 * Created by hang on 2018/10/29.
 */
public class RoomQuestionsListResult {

    public static class Response extends BaseResponse<RoomQuestionsListResult> {
    }

    public List<String> questions;

}
