package com.net.model.chick.friends;

import com.mei.orc.http.response.BaseResponse;
import com.net.model.room.CourseBean;

/**
 * @author Created by lenna on 2020/7/7
 */
public class CourseListResult {
    public static class Response extends BaseResponse<CourseListResult> {

    }

    public CourseBean list;
}
