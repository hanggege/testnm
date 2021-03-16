package com.net.model.room;

import com.mei.orc.http.response.BaseResponse;

import java.util.List;

/**
 * Created by hang on 2020-03-10.
 */
public class QueueTop {

    public List<QueueShow.ShowUser> apply_list;

    public static class Response extends BaseResponse<QueueTop> {
    }
}
