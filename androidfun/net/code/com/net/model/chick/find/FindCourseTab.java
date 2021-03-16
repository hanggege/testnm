package com.net.model.chick.find;

import com.mei.orc.http.response.BaseResponse;

import java.util.List;

/**
 * FindCourseTab
 *
 * @author kun
 * @version 1.0.0
 * @since 2020-07-23
 */
public class FindCourseTab {

    public static class Response extends BaseResponse<FindCourseTab> {

    }

    public List<TabInfo> partitions;

    public static class TabInfo {
        public int partitionId;
        public String partitionImage;
        public String partitionName;
        public String url;
        public String backgroundImage;
        public String backgroundImageHover;
    }


}