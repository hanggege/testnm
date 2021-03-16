package com.net.model.chick.find;

import com.mei.orc.http.response.BaseResponse;

import java.util.List;

/**
 * FindTab
 *
 * @author kun
 * @version 1.0.0
 * @since 2020-07-22
 */
public class FindTab {

    public static class Response extends BaseResponse<FindTab> {

    }

    public List<TabInfo> tab;

    public static class TabInfo {
        public String code;
        public String name;
        public int priority;
        public String url;
        public boolean active;
    }
}
